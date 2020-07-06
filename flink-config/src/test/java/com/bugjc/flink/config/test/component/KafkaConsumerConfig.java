package com.bugjc.flink.config.test.component;

import com.alibaba.fastjson.annotation.JSONField;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

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
}
