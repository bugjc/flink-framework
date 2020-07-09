package com.bugjc.flink.connector.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

/**
 * 自定义序列怀器
 *
 * @author aoki
 * @date 2020/7/1
 **/
public class KafkaEventSchema<T> implements DeserializationSchema<T>, SerializationSchema<T> {

    private final Class<T> eventClass;

    public KafkaEventSchema(Class<T> eventClass) {
        this.eventClass = eventClass;
    }
    
    @Override
    public T deserialize(byte[] message) throws IOException {
        return JSON.parseObject(message, eventClass);
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
        return TypeInformation.of(eventClass);
    }
}
