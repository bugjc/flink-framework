package com.bugjc.flink.test.kafka.app.config;

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
@Data
@Slf4j
@ConfigurationProperties(prefix = "flink.kafka.producer2.")
public class KafkaProducerConfig2 extends AbstractKafkaProducerConfig implements Serializable {

    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String topic;

    /**
     * 获取 kafka 参数
     *
     * @return
     */
    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", this.bootstrapServers);
        properties.setProperty("key.serializer", this.keySerializer);
        properties.setProperty("value.serializer", this.valueSerializer);
        return properties;
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
