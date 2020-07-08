package com.bugjc.flink.datasource.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.io.Serializable;
import java.util.Properties;

/**
 * kafka 消费者属性配置
 *
 * @author aoki
 * @date 2020/7/2
 **/
@Data
@Slf4j
@ConfigurationProperties(prefix = "flink.kafka.consumer.")
public class KafkaConsumerConfig implements Serializable {

    @JSONField(name = "bootstrap.servers")
    private String bootstrapServers;
    @JSONField(name = "zookeeper.connect")
    private String zookeeperConnect;
    @JSONField(name = "group.id")
    private String groupId;
    @JSONField(name = "key.deserializer")
    private String keyDeserializer;
    @JSONField(name = "value.deserializer")
    private String valueDeserializer;
    @JSONField(name = "auto.offset.reset")
    private String autoOffsetReset;
    @JSONField(name = "topic")
    private String topic;

    @JSONField(serialize = false)
    public <T> FlinkKafkaConsumer011<T> getKafkaConsumer011(Class<T> eventClass) {
        Properties kafkaConsumerConfig = JSON.parseObject(JSON.toJSONString(this), Properties.class);
        return new FlinkKafkaConsumer011<T>(this.topic, new KafkaEventSchema<T>(eventClass), kafkaConsumerConfig);
    }
}
