package org.apache.rocketmq.grpc.autoconfigure;

import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.grpc.core.RocketMQGRpcTemplate;
import org.apache.rocketmq.grpc.annotation.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Akai
 */
@Configuration
public class TransactionConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private final static Logger log = LoggerFactory.getLogger(TransactionConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    //获取被@RocketMQTransactionListener标记的类
    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(TransactionListener.class)
                .entrySet().stream().filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        beans.forEach(this::handleTransactionChecker);
    }

    public void handleTransactionChecker(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        if (!TransactionChecker.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + TransactionChecker.class.getName());
        }
        TransactionListener annotation = clazz.getAnnotation(TransactionListener.class);
        if (Objects.isNull(annotation)) {
            throw new IllegalStateException("The transactionListener annotation is missing");
        }
        //获取注解上的template,默认为RocketMQGRpcTemplate
        RocketMQGRpcTemplate rocketMQTemplate = (RocketMQGRpcTemplate) applicationContext.getBean(annotation.rocketMQTemplateBeanName());
        if ((rocketMQTemplate.getProducerBuilder()) != null) {
            rocketMQTemplate.getProducerBuilder().setTransactionChecker((TransactionChecker) bean);
        }
    }

}
