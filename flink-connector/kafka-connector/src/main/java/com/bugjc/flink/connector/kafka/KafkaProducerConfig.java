package com.bugjc.flink.connector.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.connector.kafka.config.AbstractKafkaProducerConfig;
import com.bugjc.flink.connector.kafka.schema.GeneralKafkaSchema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;
import java.util.Properties;

/**
 * kafka 生产者属性配置
 *
 * @author aoki
 * @date 2020/7/2
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@JSONType(includes = {"bootstrapServers","keySerializer","valueSerializer"})
@ConfigurationProperties(prefix = "flink.kafka.producer.")
public class KafkaProducerConfig extends AbstractKafkaProducerConfig implements Serializable {

    @JSONField(name = "bootstrap.servers")
    private String bootstrapServers;
    @JSONField(name = "key.serializer")
    private String keySerializer;
    @JSONField(name = "value.serializer")
    private String valueSerializer;

    @JSONField(name = "topic")
    private String topic;

    /**
     * 获取 kafka 参数
     *
     * @return
     */
    @Override
    @JSONField(serialize = false)
    public Properties getProperties() {
        return JSON.parseObject(JSON.toJSONString(this), Properties.class);
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
     * 创建一个 kafka 的 producer sink
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    @Override
    public <T> FlinkKafkaProducer011<T> createKafkaSink(Class<T> eventClass) {
        return new FlinkKafkaProducer011<T>(
                this.topic,
                this.createGeneralKafkaSchema(eventClass),
                this.getProperties());
    }

    /**
     * 创建一个 kafka KafkaProducer
     *
     * @param <K> --key 反序列化器
     * @param <V> --value 反序列化器
     * @return
     */
    @Override
    public <K, V> KafkaProducer<K, V> createKafkaProducer() {
        return new KafkaProducer<K, V>(this.getProperties());
    }

    @Override
    public <K, V> ProducerRecord<K, V> createKafkaProducerRecord(V value) {
        return new ProducerRecord<>(this.topic, value);
    }
}
