package com.imamba.boot.autoconfigure.kafka;


import com.imamba.boot.kafka.listener.AbstractListener;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(
        prefix = "spring.kafka"
)
/**
 * desc:
 * author:zhongjianbin
 * Date:2019/7/13 20:01
 */
public class KafkaProperties {
    private List<String> bootstrapServers;
    private final Map<String, Consumer> consumers = new HashMap();
    private final KafkaProperties.Producer producer = new KafkaProperties.Producer();
    private Map<String, String> properties = new HashMap();

    public KafkaProperties() {
    }

    public List<String> getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Map<String, Consumer> getConsumers() {
        return this.consumers;
    }

    public KafkaProperties.Producer getProducer() {
        return this.producer;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    private Map<String, Object> buildCommonProperties() {
        Map<String, Object> properties = new HashMap();
        if (this.bootstrapServers != null) {
            properties.put("bootstrap.servers", this.bootstrapServers);
        }

        if (!CollectionUtils.isEmpty(this.properties)) {
            properties.putAll(this.properties);
        }

        return properties;
    }

    public Map<String, Object> buildConsumerProperties(KafkaProperties.Consumer consumer) {
        Map<String, Object> properties = this.buildCommonProperties();
        properties.putAll(consumer.buildProperties());
        return properties;
    }

    public Map<String, Object> buildProducerProperties() {
        Map<String, Object> properties = this.buildCommonProperties();
        properties.putAll(this.producer.buildProperties());
        return properties;
    }

    public static class Producer {
        private List<String> bootstrapServers;
        private String topic;
        private Class<? extends Serializer> keySerializer = StringSerializer.class;
        private Class<? extends Serializer> valueSerializer = StringSerializer.class;

        public Producer() {
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public Class<? extends Serializer> getKeySerializer() {
            return this.keySerializer;
        }

        public void setKeySerializer(Class<? extends Serializer> keySerializer) {
            this.keySerializer = keySerializer;
        }

        public Class<? extends Serializer> getValueSerializer() {
            return this.valueSerializer;
        }

        public void setValueSerializer(Class<? extends Serializer> valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public String getTopic() {
            return this.topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public Map<String, Object> buildProperties() {
            Map<String, Object> properties = new HashMap();
            if (this.bootstrapServers != null) {
                properties.put("bootstrap.servers", this.bootstrapServers);
            }

            if (this.keySerializer != null) {
                properties.put("key.serializer", this.keySerializer);
            }

            if (this.valueSerializer != null) {
                properties.put("value.serializer", this.valueSerializer);
            }

            return properties;
        }
    }

    public static class Consumer {
        private List<String> bootstrapServers;
        @NotBlank
        private String topic;
        @NotBlank
        private String groupId;
        private Boolean enableAutoCommit;
        private Class<? extends Deserializer> keyDeserializer = StringDeserializer.class;
        private Class<? extends Deserializer> valueDeserializer = StringDeserializer.class;
        @Min(1L)
        @NotNull
        private Integer maxPollRecords;
        @Min(1L)
        @NotNull
        private Integer taskNum = 1;
        private Class<? extends AbstractListener> listener;

        public Consumer() {
        }

        public String getTopic() {
            return this.topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public Boolean getEnableAutoCommit() {
            return this.enableAutoCommit;
        }

        public void setEnableAutoCommit(Boolean enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }

        public Class<? extends Deserializer> getKeyDeserializer() {
            return this.keyDeserializer;
        }

        public void setKeyDeserializer(Class<? extends Deserializer> keyDeserializer) {
            this.keyDeserializer = keyDeserializer;
        }

        public Class<? extends Deserializer> getValueDeserializer() {
            return this.valueDeserializer;
        }

        public void setValueDeserializer(Class<? extends Deserializer> valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
        }

        public Integer getMaxPollRecords() {
            return this.maxPollRecords;
        }

        public void setMaxPollRecords(Integer maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
        }

        public Integer getTaskNum() {
            return this.taskNum;
        }

        public void setTaskNum(Integer taskNum) {
            this.taskNum = taskNum;
        }

        public Class<? extends AbstractListener> getListener() {
            return this.listener;
        }

        public void setListener(Class<? extends AbstractListener> listener) {
            this.listener = listener;
        }

        public Map<String, Object> buildProperties() {
            Map<String, Object> properties = new HashMap();
            if (this.bootstrapServers != null) {
                properties.put("bootstrap.servers", this.bootstrapServers);
            }

            if (this.groupId != null) {
                properties.put("group.id", this.groupId);
            }

            if (this.enableAutoCommit != null) {
                properties.put("enable.auto.commit", this.enableAutoCommit);
            }

            if (this.keyDeserializer != null) {
                properties.put("key.deserializer", this.keyDeserializer);
            }

            if (this.valueDeserializer != null) {
                properties.put("value.deserializer", this.valueDeserializer);
            }

            if (this.maxPollRecords != null) {
                properties.put("max.poll.records", this.maxPollRecords);
            }

            return properties;
        }
    }
}
