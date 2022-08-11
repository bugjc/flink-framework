package com.bugjc.flink.connector.kafka;

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
@ConfigurationProperties(prefix = "flink.kafka.producer.")
public class KafkaProducerConfig extends AbstractKafkaProducerConfig implements Serializable {

    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String topic;

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", this.bootstrapServers);
        properties.setProperty("key.serializer", this.keySerializer);
        properties.setProperty("value.serializer", this.valueSerializer);
        return properties;
    }

    @Override
    public <T> GeneralKafkaSchema<T> createGeneralKafkaSchema(Class<T> eventClass) {
        return new GeneralKafkaSchema<T>(eventClass);
    }

    @Override
    public <T> FlinkKafkaProducer011<T> createKafkaSink(Class<T> eventClass) {
        return new FlinkKafkaProducer011<T>(
                this.topic,
                this.createGeneralKafkaSchema(eventClass),
                this.getProperties());
    }

    @Override
    public <K, V> KafkaProducer<K, V> createKafkaProducer() {
        return new KafkaProducer<K, V>(this.getProperties());
    }

    @Override
    public <K, V> ProducerRecord<K, V> createKafkaProducerRecord(V value) {
        return new ProducerRecord<>(this.topic, value);
    }
}
