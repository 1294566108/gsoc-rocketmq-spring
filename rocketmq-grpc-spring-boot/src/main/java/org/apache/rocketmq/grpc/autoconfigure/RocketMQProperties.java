package org.apache.rocketmq.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Akai
 */
@SuppressWarnings("WeakerAccess")
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {

    private Producer producer;

    private SimpleConsumer simpleConsumer = new SimpleConsumer();

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public SimpleConsumer getSimpleConsumer() {
        return simpleConsumer;
    }

    public void setSimpleConsumer(SimpleConsumer simpleConsumer) {
        this.simpleConsumer = simpleConsumer;
    }

    public static class Producer {

        /**
         * The property of "access-key".
         */
        private String accessKey;

        /**
         * The property of "secret-key".
         */
        private String secretKey;

        /**
         * Proxy address and port list
         */
        private String endpoints;

        /**
         * topic is used to prefetch the route
         */
        private String topic;

        /**
         * The requestTimeout of client.
         */
        private int requestTimeout = 3;

        /**
         * enable ssl or not.
         */
        private boolean sslEnabled = true;

        /**
         * max attempt quantity
         */
        private int maxAttempts = 3;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(String endpoints) {
            this.endpoints = endpoints;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getRequestTimeout() {
            return requestTimeout;
        }

        public void setRequestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
        }

        public boolean isSslEnabled() {
            return sslEnabled;
        }

        public void setSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        @Override
        public String toString() {
            return "Producer{" +
                    "accessKey='" + accessKey + '\'' +
                    ", secretKey='" + secretKey + '\'' +
                    ", endpoints='" + endpoints + '\'' +
                    ", topic='" + topic + '\'' +
                    ", requestTimeout=" + requestTimeout +
                    ", sslEnabled=" + sslEnabled +
                    '}';
        }
    }

    public static class SimpleConsumer {
        /**
         * The property of "access-key".
         */
        private String accessKey;

        /**
         * The property of "secret-key".
         */
        private String secretKey;

        /**
         * Proxy address and port list
         */
        private String endpoints;

        /**
         * Group name of consumer.
         */
        private String consumerGroup;

        /**
         * awaitDuration of consumer.
         */
        private int awaitDuration = 0;

        /**
         * Tag  of consumer.
         */
        private String tag;

        /**
         * Topic name of consumer.
         */
        private String topic;

        /**
         * The requestTimeout of client.
         */
        private int requestTimeout = 3;

        /**
         * The type of filter expression
         */
        private String filterExpressionType = "tag";

        /**
         * enable ssl or not.
         */
        private boolean sslEnabled = true;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(String endpoints) {
            this.endpoints = endpoints;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }

        public int getAwaitDuration() {
            return awaitDuration;
        }

        public void setAwaitDuration(int awaitDuration) {
            this.awaitDuration = awaitDuration;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getRequestTimeout() {
            return requestTimeout;
        }

        public void setRequestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
        }

        public boolean isSslEnabled() {
            return sslEnabled;
        }

        public void setSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
        }

        public String getFilterExpressionType() {
            return filterExpressionType;
        }

        public void setFilterExpressionType(String filterExpressionType) {
            this.filterExpressionType = filterExpressionType;
        }

        @Override
        public String toString() {
            return "SimpleConsumer{" +
                    "accessKey='" + accessKey + '\'' +
                    ", secretKey='" + secretKey + '\'' +
                    ", endpoints='" + endpoints + '\'' +
                    ", consumerGroup='" + consumerGroup + '\'' +
                    ", awaitDuration='" + awaitDuration + '\'' +
                    ", tag='" + tag + '\'' +
                    ", topic='" + topic + '\'' +
                    ", requestTimeout=" + requestTimeout +
                    ", filterExpressionType='" + filterExpressionType + '\'' +
                    ", sslEnabled=" + sslEnabled +
                    '}';
        }
    }

}
