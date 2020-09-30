package com.bugjc.flink.connector.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.connector.kafka.config.AbstractKafkaConsumerConfig;
import com.bugjc.flink.connector.kafka.schema.GeneralKafkaSchema;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.io.Serializable;
import java.util.Arrays;
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

    /**
     * 获取 kafka 参数
     *
     * @return
     */
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

    /**
     * 获取 kafka 序列化、反序列化器
     *
     * @param eventClass
     * @param <T>
     * @return
     */
    @Override
    public <T> GeneralKafkaSchema<T> createGeneralKafkaSchema(Class<T> eventClass) {
        return new GeneralKafkaSchema<T>(eventClass);
    }

    /**
     * 创建一个 kafka 的 consumer source
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    @Override
    public <T> FlinkKafkaConsumer011<T> createKafkaSource(Class<T> eventClass) {
        Pattern pattern = Pattern.compile(this.topic);
        if (pattern.matcher(pattern.pattern()).matches()) {
            //使用 单topic 或 多topic
            return new FlinkKafkaConsumer011<T>(
                    Arrays.stream(this.topic.split(",")).map(String::trim).collect(Collectors.toList()),
                    this.createGeneralKafkaSchema(eventClass),
                    this.getProperties());
        }

        //使用 Topic 发现
        return new FlinkKafkaConsumer011<T>(
                pattern,
                this.createGeneralKafkaSchema(eventClass),
                this.getProperties());

    }
}
