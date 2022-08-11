package com.bugjc.flink.connector.kafka.config;

import com.bugjc.flink.config.Config;
import com.bugjc.flink.connector.kafka.schema.GeneralKafkaSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Kafka 抽象生产者配置
 * @author aoki
 * @date 2020/7/14
 **/
public abstract class AbstractKafkaProducerConfig implements Config {
    /**
     * 获取 kafka 参数
     *
     * @return
     */
    public abstract Properties getProperties();

    /**
     * 获取 kafka 序列化、反序列化器
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    public abstract <T> GeneralKafkaSchema<T> createGeneralKafkaSchema(Class<T> eventClass);

    /**
     * 创建一个 kafka 的 consumer source
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    public abstract <T> FlinkKafkaProducer011<T> createKafkaSink(Class<T> eventClass);

    /**
     * 创建一个 kafka KafkaProducer
     *
     * @param <K> --key 序列化器
     * @param <V> --value 序列化器
     * @return
     */
    public abstract <K, V> KafkaProducer<K, V> createKafkaProducer();

    /**
     * 创建一个 kafka ProducerRecord
     * @param value -- 待发送的消息
     * @param <K> --key 序列化器
     * @param <V> --value 序列化器
     * @return
     */
    public abstract <K, V> ProducerRecord<K, V> createKafkaProducerRecord(V value);
}
