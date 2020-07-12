package com.bugjc.flink.connector.kafka;

import com.bugjc.flink.config.Config;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

public abstract class AbstractKafkaConsumerConfig implements Config {
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
    public abstract <T> KafkaEventSchema<T> getKafkaEventSchema(Class<T> eventClass);

    /**
     * 创建一个 kafka 的 consumer source
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    public abstract <T> FlinkKafkaConsumer011<T> getKafkaConsumer(Class<T> eventClass);
}
