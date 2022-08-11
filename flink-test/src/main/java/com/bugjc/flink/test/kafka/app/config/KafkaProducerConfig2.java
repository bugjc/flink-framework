package com.bugjc.flink.test.kafka.app.config;

import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.connector.kafka.config.AbstractKafkaProducerConfig;
import com.bugjc.flink.connector.kafka.schema.GeneralKafkaSchema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
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
@ConfigurationProperties(prefix = "flink.kafka.producer2.")
public class KafkaProducerConfig2 extends AbstractKafkaProducerConfig implements Serializable {

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
    public <T> KafkaSink<T> createKafkaSink(Class<T> eventClass) {
        return KafkaSink.<T>builder()
                .setKafkaProducerConfig(this.getProperties())
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(this.topic)
                        .setValueSerializationSchema(this.createGeneralKafkaSchema(eventClass))
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
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
