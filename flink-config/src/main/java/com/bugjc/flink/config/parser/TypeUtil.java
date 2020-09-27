package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.EnumDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.MapDeserializer;
import com.alibaba.fastjson.serializer.CollectionCodec;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型工具类
 *
 * @author aoki
 * @date 2020/9/17
 **/
public class TypeUtil {

    public static boolean isJavaBean(Type type) {
        if (null == type) {
            throw new NullPointerException();
        }
        // 根据 getDeserializer 返回值类型判断是否为 java bean 类型
        return ParserConfig.global.getDeserializer(type) instanceof JavaBeanDeserializer;
    }

    public static boolean isEnum(Type type) {
        return ParserConfig.global.getDeserializer(type) instanceof EnumDeserializer;
    }

    public static boolean isList(Type type) {
        return ParserConfig.global.getDeserializer(type) instanceof CollectionCodec;
    }

    public static boolean isMap(Type type) {
        return ParserConfig.global.getDeserializer(type) instanceof MapDeserializer;
    }

    public static boolean isBasic(Type type) {
        if (isJavaBean(type)) {
            return false;
        }

        if (isMap(type)) {
            return false;
        }

        return !isList(type);
    }

    /**
     * 获取泛型的最后一个类型
     *
     * @param valueType
     * @return
     */
    public static Type getLastType(Type valueType) {
        if (TypeUtil.isMap(valueType) || TypeUtil.isList(valueType)) {
            Type[] types = ((ParameterizedType) valueType).getActualTypeArguments();
            valueType = types[types.length - 1];
            return getLastType(valueType);
        }
        return valueType;
    }
}
