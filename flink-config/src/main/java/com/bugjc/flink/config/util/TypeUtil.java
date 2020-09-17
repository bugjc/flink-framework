package com.bugjc.flink.config.util;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.EnumDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.MapDeserializer;
import com.alibaba.fastjson.serializer.CollectionCodec;

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
}
