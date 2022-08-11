package com.bugjc.flink.connector.kafka;

import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.connector.kafka.config.AbstractKafkaConsumerConfig;
import com.bugjc.flink.connector.kafka.schema.GeneralKafkaSchema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.kafka.source.KafkaSource;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * kafka 消费者属性配置
 *
 * @author aoki
 * @date 2020/7/2
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@ConfigurationProperties(prefix = "flink.kafka.consumer.")
public class KafkaConsumerConfig extends AbstractKafkaConsumerConfig implements Serializable {

    private String bootstrapServers;
    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private String autoOffsetReset;
    private String automaticPartition;

    /**
     * topic 值有三类，分别对应：单 topic、多 topic 和 topic 正则表达式消费数据的方式。
     * 例：单 topic --> testTopicName;多 topic --> testTopicName,testTopicName1;topic 正则表达式 --> testTopicName[0-9]
     */
    private String topic;

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", this.bootstrapServers);
        properties.setProperty("group.id", this.groupId);
        properties.setProperty("key.deserializer", this.keyDeserializer);
        properties.setProperty("value.deserializer", this.valueDeserializer);
        properties.setProperty("auto.offset.reset", this.autoOffsetReset);
        if (this.automaticPartition !=null){
            properties.setProperty("flink.partition-discovery.interval-millis", this.automaticPartition);
        }
        return properties;
    }

    @Override
    public <T> GeneralKafkaSchema<T> createGeneralKafkaSchema(Class<T> eventClass) {
        return new GeneralKafkaSchema<T>(eventClass);
    }

    @Override
    public <T> KafkaSource<T> createKafkaSource(Class<T> eventClass) {
        Pattern pattern = Pattern.compile(this.topic);
        if (pattern.matcher(pattern.pattern()).matches()) {
            List<String> topics = Arrays.stream(this.topic.split(",")).map(String::trim).collect(Collectors.toList());
            return KafkaSource.<T>builder()
                    .setBootstrapServers(this.bootstrapServers)
                    .setTopics(topics)
                    .setGroupId(this.groupId)
                    //.setStartingOffsets(OffsetsInitializer.earliest())
                    .setProperties(this.getProperties())
                    .setValueOnlyDeserializer(this.createGeneralKafkaSchema(eventClass))
                    .build();
        }

        //使用 Topic 发现
        return KafkaSource.<T>builder()
                .setBootstrapServers(this.bootstrapServers)
                .setTopicPattern(pattern)
                .setGroupId(this.groupId)
                //.setStartingOffsets(OffsetsInitializer.earliest())
                .setProperties(this.getProperties())
                .setValueOnlyDeserializer(this.createGeneralKafkaSchema(eventClass))
                .build();
    }
}
