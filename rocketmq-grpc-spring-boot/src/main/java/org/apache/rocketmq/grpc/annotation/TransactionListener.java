package org.apache.rocketmq.grpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Akai
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TransactionListener {
    String rocketMQTemplateBeanName() default "rocketMQGRpcTemplate";
}
