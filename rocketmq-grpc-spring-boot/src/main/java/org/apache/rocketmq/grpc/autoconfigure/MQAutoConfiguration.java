package org.apache.rocketmq.grpc.autoconfigure;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.java.impl.producer.ProducerBuilderImpl;
import org.apache.rocketmq.grpc.core.RocketMQGRpcTemplate;
import org.apache.rocketmq.grpc.support.MessageConverter;
import org.apache.rocketmq.grpc.support.RocketMQUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Akai
 * 该配置类必须在MessageConverterConfiguration配置之后，在TransactionConfiguration之前
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@Import({RocketMQMessageConverterConfiguration.class, ListenerContainerConfiguration.class, ExtTemplateResetConfiguration.class,
        ExtConsumerResetConfiguration.class, TransactionConfiguration.class, RocketMQListenerConfiguration.class})
@AutoConfigureAfter({RocketMQMessageConverterConfiguration.class})
@AutoConfigureBefore({TransactionConfiguration.class})
public class MQAutoConfiguration implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(MQAutoConfiguration.class);
    public static final String ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME = "rocketMQTemplate";
    public static final String PRODUCER_BUILDER_BEAN_NAME = "producerBuilder";
    public static final String SIMPLE_CONSUMER_BUILDER_BEAN_NAME = "simpleConsumerBuilder";
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * description:gRPC-SDK ProducerBuilder
     */
    @Bean(PRODUCER_BUILDER_BEAN_NAME)
    @ConditionalOnMissingBean(ProducerBuilderImpl.class)
    @ConditionalOnProperty(prefix = "rocketmq", value = {"producer.endpoints"})
    public ProducerBuilder producerBuilder(RocketMQProperties rocketMQProperties) {
        RocketMQProperties.Producer rocketMQProducer = rocketMQProperties.getProducer();
        log.info("Init Producer Args: " + rocketMQProducer);
        String topic = rocketMQProducer.getTopic();
        String endPoints = rocketMQProducer.getEndpoints();
        Assert.hasText(topic, "[rocketmq.producer.topic] must not be null");
        ClientConfiguration clientConfiguration = RocketMQUtil.createProducerClientConfiguration(rocketMQProducer);
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ProducerBuilder producerBuilder;
        producerBuilder = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the topic name(s), which is optional but recommended. It makes producer could prefetch the topic
                // route before message publishing.
                .setTopics(rocketMQProducer.getTopic())
                .setMaxAttempts(rocketMQProducer.getMaxAttempts());
        log.info(String.format("a producer init on proxy %s", endPoints));
        return producerBuilder;
    }


    /**
     * description:gRPC-SDK SimpleConsumerBuilder
     */
    @Bean(SIMPLE_CONSUMER_BUILDER_BEAN_NAME)
    @ConditionalOnMissingBean(SimpleConsumerBuilder.class)
    @ConditionalOnProperty(prefix = "rocketmq", value = {"simple-consumer.endpoints"})
    public SimpleConsumerBuilder simpleConsumerBuilder(RocketMQProperties rocketMQProperties) {
        //此处getConsumer返回一个pushConsumer,getPullConsumer返回一个pullConsumer
        RocketMQProperties.SimpleConsumer simpleConsumer = rocketMQProperties.getSimpleConsumer();
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        String consumerGroup = simpleConsumer.getConsumerGroup();
        FilterExpression filterExpression = RocketMQUtil.createFilterExpression(simpleConsumer.getTag(), simpleConsumer.getFilterExpressionType());
        ClientConfiguration clientConfiguration = RocketMQUtil.createConsumerClientConfiguration(simpleConsumer);
        SimpleConsumerBuilder simpleConsumerBuilder = provider.newSimpleConsumerBuilder()
                .setClientConfiguration(clientConfiguration);
        // set await duration for long-polling.
        simpleConsumerBuilder.setAwaitDuration(Duration.ofSeconds(simpleConsumer.getAwaitDuration()));

        // Set the consumer group name.
        if (StringUtils.hasLength(consumerGroup)) {
            simpleConsumerBuilder.setConsumerGroup(consumerGroup);
        }
        // Set the subscription for the consumer.
        if (Objects.nonNull(filterExpression)) {
            simpleConsumerBuilder.setSubscriptionExpressions(Collections.singletonMap(simpleConsumer.getTopic(), filterExpression));
        }
        return simpleConsumerBuilder;
    }

    @Bean(destroyMethod = "destroy")
    @Conditional(ProducerOrConsumerPropertyCondition.class)
    @ConditionalOnMissingBean(name = ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
    public RocketMQGRpcTemplate rocketMQGRpcTemplate(MessageConverter rocketMQMessageConverter) {
        RocketMQGRpcTemplate rocketMQRemotingTemplate = new RocketMQGRpcTemplate();

        if (applicationContext.containsBean(PRODUCER_BUILDER_BEAN_NAME)) {
            rocketMQRemotingTemplate.setProducerBuilder((ProducerBuilder) applicationContext.getBean(PRODUCER_BUILDER_BEAN_NAME));
        }
        if (applicationContext.containsBean(SIMPLE_CONSUMER_BUILDER_BEAN_NAME)) {
            rocketMQRemotingTemplate.setSimpleConsumerBuilder((SimpleConsumerBuilder) applicationContext.getBean(SIMPLE_CONSUMER_BUILDER_BEAN_NAME));
        }
        rocketMQRemotingTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        return rocketMQRemotingTemplate;
    }

    /**
     *
     */
    static class ProducerOrConsumerPropertyCondition extends AnyNestedCondition {

        public ProducerOrConsumerPropertyCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(ProducerBuilder.class)
        static class DefaultMQProducerExistsCondition {
        }

        @ConditionalOnBean(SimpleConsumerBuilder.class)
        static class SimpleConsumerExistsCondition {
        }
    }
}
