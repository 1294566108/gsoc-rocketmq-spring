package org.apache.rocketmq.samples.springboot;


import org.apache.rocketmq.grpc.annotation.ExtConsumerConfiguration;
import org.apache.rocketmq.grpc.core.RocketMQGRpcTemplate;

/**
 * @author Akai
 */
@ExtConsumerConfiguration(topic = "${ext.rocketmq.topic:}")
public class ExtRocketMQTemplate extends RocketMQGRpcTemplate {
}
