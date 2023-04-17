package org.apache.rocketmq.grpc.autoconfigure;

import org.apache.rocketmq.grpc.annotation.MessageListenerBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Akai
 * 在配置完MQAutoConfiguration后配置RocketMQListenerConfiguration
 * 运行时向应用程序上下文中动态添加注册Bean：MessageListenerBeanPostProcessor
 * 在这个bean初始化之前调用postProcessBeforeInitialization
 * 在初始话bean时调用afterPropertiesSet构建一个注解增强器，具体实现过程是去拿到所有实现AnnotationEnhancer接口的类并重写父类的apply方法
 * apply方法是：遍历所有注解增强器，将前一次注解增强得到的结果作为下一个注解增强器的参数，最后返回多个注解增强器处理后的结果，这个结果就是增强后的注解实体
 * 接着填充MessageListenerBeanPostProcessor中的属性this.listenerContainerConfiguration
 * 实例化bean后，获取所有@MessageListener注解进行增强，并将ListenerContainerConfiguration注册成容器，构造一个生产者开始监听消息
 */
@Configuration
@AutoConfigureAfter(MQAutoConfiguration.class)
public class RocketMQListenerConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(MessageListenerBeanPostProcessor.class.getName())) {
            registry.registerBeanDefinition(MessageListenerBeanPostProcessor.class.getName(),
                    new RootBeanDefinition(MessageListenerBeanPostProcessor.class));
        }
    }
}
