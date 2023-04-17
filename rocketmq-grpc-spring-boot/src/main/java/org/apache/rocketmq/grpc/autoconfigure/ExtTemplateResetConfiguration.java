package org.apache.rocketmq.grpc.autoconfigure;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.grpc.annotation.ExtTemplateConfiguration;
import org.apache.rocketmq.grpc.core.RocketMQGRpcTemplate;
import org.apache.rocketmq.grpc.support.MessageConverter;
import org.apache.rocketmq.grpc.support.RocketMQUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Akai
 */
@Configuration
public class ExtTemplateResetConfiguration implements ApplicationContextAware, SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(ExtTemplateResetConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    private RocketMQProperties rocketMQProperties;

    private MessageConverter rocketMQMessageConverter;

    public ExtTemplateResetConfiguration(MessageConverter rocketMQMessageConverter,
                                         ConfigurableEnvironment environment, RocketMQProperties rocketMQProperties) {
        this.rocketMQMessageConverter = rocketMQMessageConverter;
        this.environment = environment;
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }




    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(ExtTemplateConfiguration.class)
                .entrySet().stream().filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        beans.forEach(this::registerTemplate);
    }

    private void registerTemplate(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        if (!RocketMQGRpcTemplate.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + RocketMQGRpcTemplate.class.getName());
        }

        ExtTemplateConfiguration annotation = clazz.getAnnotation(ExtTemplateConfiguration.class);
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        validate(annotation, genericApplicationContext);

        ProducerBuilder producerBuilder = createProducer(annotation);
        RocketMQGRpcTemplate rocketMQTemplate = (RocketMQGRpcTemplate) bean;
        rocketMQTemplate.setProducerBuilder(producerBuilder);
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        log.info("Set real producerBuilder to :{} {}", beanName, annotation.value());
    }

    private ProducerBuilder createProducer(ExtTemplateConfiguration annotation) {
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        if (producerConfig == null) {
            producerConfig = new RocketMQProperties.Producer();
        }
        String topic = environment.resolvePlaceholders(annotation.topic());
        topic = StringUtils.hasLength(topic) ? topic : producerConfig.getTopic();
        String endpoints = environment.resolvePlaceholders(annotation.endpoints());
        endpoints = StringUtils.hasLength(endpoints) ? endpoints : producerConfig.getEndpoints();
        String accessKey = environment.resolvePlaceholders(annotation.accessKey());
        accessKey = StringUtils.hasLength(accessKey) ? accessKey : producerConfig.getAccessKey();
        String secretKey = environment.resolvePlaceholders(annotation.secretKey());
        secretKey = StringUtils.hasLength(secretKey) ? secretKey : producerConfig.getSecretKey();
        int requestTimeout = annotation.requestTimeout();
        ClientConfiguration clientConfiguration = RocketMQUtil.createClientConfiguration(accessKey, secretKey, endpoints, Duration.ofDays(requestTimeout));
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ProducerBuilder producerBuilder = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration).setMaxAttempts(annotation.maxAttempts())
                .setTopics(topic);
        return producerBuilder;
    }

    private void validate(ExtTemplateConfiguration annotation,
                          GenericApplicationContext genericApplicationContext) {
        if (genericApplicationContext.isBeanNameInUse(annotation.value())) {
            throw new BeanDefinitionValidationException(String.format("Bean {} has been used in Spring Application Context, " +
                            "please check the @ExtTemplateConfiguration",
                    annotation.value()));
        }
    }

}
