package com.bugjc.flink.connector.kafka.config;

import com.bugjc.flink.config.Config;
import com.bugjc.flink.connector.kafka.schema.GeneralKafkaSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

/**
 * 类描述
 * @see <a href="">https://leetcode-cn.com</a>
 * @author aoki
 * @date 2020/7/14
 * **/
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
    public abstract <T> GeneralKafkaSchema<T> createGeneralKafkaSchema(Class<T> eventClass);

    /**
     * 创建一个 kafka 的 consumer source
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    public abstract <T> FlinkKafkaConsumer011<T> createKafkaSource(Class<T> eventClass);
}
