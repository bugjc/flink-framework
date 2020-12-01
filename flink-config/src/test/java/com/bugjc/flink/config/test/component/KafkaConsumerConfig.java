package com.bugjc.flink.config.test.component;

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

    private String bootstrapServers;
    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private String autoOffsetReset;
    private String automaticPartition;
}
