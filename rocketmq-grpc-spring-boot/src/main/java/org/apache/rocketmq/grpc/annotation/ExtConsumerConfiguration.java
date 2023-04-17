package org.apache.rocketmq.grpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Akai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ExtConsumerConfiguration {
    String value() default "";

    String accessKey() default "${rocketmq.simple-consumer.accessKey:}";

    String secretKey() default "${rocketmq.simple-consumer.secretKey:}";

    String endpoints() default "${rocketmq.simple-consumer.endpoints:}";

    String consumerGroup() default "${rocketmq.simple-consumer.consumerGroup:}";

    String tag() default "${rocketmq.simple-consumer.tag:}";

    String topic() default "${rocketmq.simple-consumer.topic:}";

    String filterExpressionType() default "${rocketmq.simple-consumer.filterExpressionType:}";

    int requestTimeout() default 3;

    int awaitDuration() default 0;

}
