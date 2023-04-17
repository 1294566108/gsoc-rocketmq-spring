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
public @interface ExtTemplateConfiguration {
    /**
     * The component name of the Producer configuration.
     */
    String value() default "";

    /**
     * The property of "access-key".
     */
    String accessKey() default "${rocketmq.producer.accessKey:}";

    /**
     * The property of "secret-key".
     */
    String secretKey() default "${rocketmq.producer.secretKey:}";

    /**
     * Proxy address and port list
     */
    String endpoints() default "${rocketmq.producer.endpoints:}";

    /**
     * topic is used to prefetch the route
     */
    String topic() default "${rocketmq.producer.topic:}";

    /**
     * The requestTimeout of client.
     */
    int requestTimeout() default 3;

    /**
     * enable ssl or not.
     */
     boolean sslEnabled() default true;

    /**
     * max attempt quantity
     */
    int maxAttempts() default  3;

}
