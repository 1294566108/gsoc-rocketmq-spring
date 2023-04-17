package org.apache.rocketmq.grpc.autoconfigure;

import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.grpc.annotation.RocketMQMessageListener;
import org.apache.rocketmq.grpc.support.DefaultListenerContainer;
import org.apache.rocketmq.grpc.support.MessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Akai
 */
@Configuration
public class ListenerContainerConfiguration implements ApplicationContextAware {
    private final static Logger log = LoggerFactory.getLogger(ListenerContainerConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);

    private ConfigurableEnvironment environment;

    private RocketMQProperties rocketMQProperties;

    private MessageConverter rocketMQMessageConverter;

    public ListenerContainerConfiguration(MessageConverter rocketMQMessageConverter,
                                          ConfigurableEnvironment environment, RocketMQProperties rocketMQProperties) {
        this.rocketMQMessageConverter = rocketMQMessageConverter;
        this.environment = environment;
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    public void registerContainer(String beanName, Object bean, RocketMQMessageListener annotation) {
        validate(annotation);
        String containerBeanName = String.format("%s_%s", DefaultListenerContainer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(containerBeanName, DefaultListenerContainer.class,
                () -> createRocketMQListenerContainer(containerBeanName, bean, annotation));
        DefaultListenerContainer container = genericApplicationContext.getBean(containerBeanName,
                DefaultListenerContainer.class);
        if (!container.isRunning()) {
            try {
                container.start();
            } catch (Exception e) {
                log.error("Started container failed. {}", container, e);
                throw new RuntimeException(e);
            }
        }
        log.info("Register the listener to container, listenerBeanName:{}, containerBeanName:{}", beanName, containerBeanName);
    }

    private DefaultListenerContainer createRocketMQListenerContainer(String name, Object bean, RocketMQMessageListener annotation) {
        DefaultListenerContainer container = new DefaultListenerContainer();
        container.setName(name);
        container.setRocketMQMessageListener(annotation);
        container.setMessageListener((MessageListener) bean);
        container.setAccessKey(environment.resolvePlaceholders(annotation.accessKey()));
        container.setSecretKey(environment.resolvePlaceholders(annotation.secretKey()));
        container.setConsumerGroup(environment.resolvePlaceholders(annotation.consumerGroup()));
        container.setTag(environment.resolvePlaceholders(annotation.tag()));
        container.setEndpoints(environment.resolvePlaceholders(annotation.endpoints()));
        container.setTopic(environment.resolvePlaceholders(annotation.topic()));
        container.setRequestTimeout(Duration.ofDays(annotation.requestTimeout()));
        container.setMaxCachedMessageCount(annotation.maxCachedMessageCount());
        container.setConsumptionThreadCount(annotation.consumptionThreadCount());
        container.setMaxCacheMessageSizeInBytes(annotation.maxCacheMessageSizeInBytes());
        container.setType(annotation.filterExpressionType());
        return container;
    }

    private void validate(RocketMQMessageListener annotation) {
        Assert.hasText(annotation.accessKey(), "[accessKey] must not be null");
        Assert.hasText(annotation.secretKey(), "[secretKey] must not be null");
        //Assert.hasText(annotation.consumerGroup(), "[consumerGroup] must not be null");
        Assert.hasText(annotation.topic(), "[topic] must not be null");
        Assert.hasText(annotation.endpoints(), "[endpoints] must not be null");
    }
}
