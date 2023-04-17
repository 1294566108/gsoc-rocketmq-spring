package org.apache.rocketmq.grpc.autoconfigure;


import org.apache.rocketmq.grpc.support.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see MessageConverter
 * 消息转换器的配置类
 */
@Configuration
@ConditionalOnMissingBean(MessageConverter.class)
class RocketMQMessageConverterConfiguration {

    @Bean
    public MessageConverter createMessageConverter() {
        return new MessageConverter();
    }

}
