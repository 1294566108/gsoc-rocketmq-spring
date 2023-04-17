package org.apache.rocketmq.grpc.annotation;

import java.lang.annotation.*;
import java.time.Duration;

/**
 * @author Akai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMQMessageListener {

    String ACCESS_KEY_PLACEHOLDER = "${rocketmq.push-consumer.access-key:}";
    String SECRET_KEY_PLACEHOLDER = "${rocketmq.push-consumer.secret-key:}";
    String ENDPOINTS_PLACEHOLDER = "${rocketmq.push-consumer.endpoints:}";
    String TOPIC_PLACEHOLDER = "${rocketmq.push-consumer.endpoints:}";
    String TAG_PLACEHOLDER = "${rocketmq.push-consumer.tag:}";

    String accessKey() default ACCESS_KEY_PLACEHOLDER;

    String secretKey() default SECRET_KEY_PLACEHOLDER;

    String endpoints() default ENDPOINTS_PLACEHOLDER;

    String topic() default TOPIC_PLACEHOLDER;

    String tag() default TAG_PLACEHOLDER;

    String filterExpressionType() default "tag";

    String consumerGroup();

    int requestTimeout() default 3;

    int maxCachedMessageCount() default 1024;

    int maxCacheMessageSizeInBytes() default 67108864;

    int consumptionThreadCount() default 20;


}
