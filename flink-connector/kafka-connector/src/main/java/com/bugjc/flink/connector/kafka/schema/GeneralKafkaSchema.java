package com.bugjc.flink.connector.kafka.schema;

import com.bugjc.flink.config.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

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
    public T deserialize(byte[] message) {
        return  GsonUtil.getInstance().getGson().fromJson(new String(message), entityClass);
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public byte[] serialize(T message) {
        return GsonUtil.getInstance().getGson().toJson(message).getBytes();
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(entityClass);
    }
}
