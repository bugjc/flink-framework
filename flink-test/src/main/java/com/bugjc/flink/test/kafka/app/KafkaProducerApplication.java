package com.bugjc.flink.test.kafka.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.connector.kafka.KafkaProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * 程序入口
 * @author aoki
 * @date 2020/7/14
 * **/
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
        for (int i = 0; i < 100; i++) {
            //直接发送
            ProducerRecord<String, String> record = kafkaProducerConfig.createKafkaProducerRecord("hello" + i);
            producer.send(record);
        }
        producer.flush();

        Thread.sleep(10000);
    }
}
