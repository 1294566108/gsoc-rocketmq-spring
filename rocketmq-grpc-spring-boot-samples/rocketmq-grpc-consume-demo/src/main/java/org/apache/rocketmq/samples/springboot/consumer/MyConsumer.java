package org.apache.rocketmq.samples.springboot.consumer;


import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.grpc.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;

/**
 * @author Akai
 */
@Service
@RocketMQMessageListener(endpoints = "${demo.rocketmq.endpoints:}", topic = "${demo.rocketmq.topic:}",
        consumerGroup = "${demo.rocketmq.consumer-group:}", tag = "${demo.rocketmq.tag:}")
public class MyConsumer implements org.apache.rocketmq.client.apis.consumer.MessageListener {

    @Override
    public ConsumeResult consume(MessageView messageView) {
        System.out.println("handle my message:" + messageView);
        return ConsumeResult.SUCCESS;
    }
}
