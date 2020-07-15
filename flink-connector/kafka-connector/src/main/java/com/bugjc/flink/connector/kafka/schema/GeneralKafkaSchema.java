package com.bugjc.flink.connector.kafka.schema;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

/**
 * 自定义 Kafka连接 序列怀器（org.apache.flink.api）
 *
 * @author aoki
 * @date 2020/7/1
 **/
public class GeneralKafkaSchema<T> implements DeserializationSchema<T>, SerializationSchema<T> {

    private final Class<T> entityClass;

    public GeneralKafkaSchema(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    @Override
    public T deserialize(byte[] message) throws IOException {
        return JSON.parseObject(message, entityClass);
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public byte[] serialize(T element) {
        return JSON.toJSONBytes(element);
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(entityClass);
    }
}
