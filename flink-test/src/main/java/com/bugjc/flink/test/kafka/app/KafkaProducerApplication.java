package com.bugjc.flink.test.kafka.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.connector.kafka.KafkaProducerConfig;
import com.bugjc.flink.test.kafka.app.model.KafkaEvent;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * 程序入口
 *
 * @author aoki
 * @date 2020/7/14
 **/
@Slf4j
@Application
public class KafkaProducerApplication {
    public static void main(String[] args) throws Exception {
        //1.环境参数配置
        EnvironmentConfig environmentConfig = new EnvironmentConfig(args);

        //2.获取 kafka 生产者配置
        KafkaProducerConfig kafkaProducerConfig = environmentConfig.getComponent(KafkaProducerConfig.class);

        //3.构建 kafka 生产者并发送消息
        KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
        for (int i = 0; i < 1; i++) {
            //同步发送
            KafkaEvent kafkaEvent = new KafkaEvent("aoki" + i, i, System.currentTimeMillis());
            String message =  new GsonBuilder().disableHtmlEscaping().create().toJson(kafkaEvent);
            ProducerRecord<String, String> record = kafkaProducerConfig.createKafkaProducerRecord(message);
            Future<RecordMetadata> recordMetadataFuture = producer.send(record);
            RecordMetadata recordMetadata = recordMetadataFuture.get();
            log.info("向 Topic={} 的 Partition={} 发送了一条 Message={} 的消息。", recordMetadata.topic(), recordMetadata.partition(), message);

        }
        producer.flush();
        producer.close();
    }
}
