package com.bugjc.flink.connector.kafka.test;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.ApplicationTest;
import com.bugjc.flink.config.util.GsonUtil;
import com.bugjc.flink.connector.kafka.KafkaProducerConfig;
import com.bugjc.flink.connector.kafka.test.event.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

@Slf4j
@ApplicationTest
class KafkaProducerConfigTest {
    /**
     * 构建环境配置文件对象
     */
    private static EnvironmentConfig environmentConfig;

    @BeforeAll
    static void init() {
        try {
            environmentConfig = new EnvironmentConfig(new String[]{});
        } catch (Exception exception) {
            log.info("{}", exception.getMessage());
            log.error("初始化环境配置失败！");
        }
    }

    @Test
    void getKafkaProducerConfig() throws Exception {
        StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();
        KafkaProducerConfig kafkaProducerConfig = environmentConfig.getComponent(KafkaProducerConfig.class);
        log.info("kafkaProducerConfig 配置信息：{}", kafkaProducerConfig);

        KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
        for (int i = 0; i < 100; i++) {
            //同步发送
            KafkaEvent kafkaEvent = new KafkaEvent("aoki" + i, i, System.currentTimeMillis());
            String message = GsonUtil.getInstance().getGson().toJson(kafkaEvent);
            ProducerRecord<String, String> record = kafkaProducerConfig.createKafkaProducerRecord(message);
            Future<RecordMetadata> recordMetadataFuture = producer.send(record);
            RecordMetadata recordMetadata = recordMetadataFuture.get();
            log.info("向 Topic={} 的 Partition={} 发送了一条 Message={} 的消息。", recordMetadata.topic(), recordMetadata.partition(), message);

        }
        producer.flush();
        producer.close();
    }
}