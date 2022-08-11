package com.bugjc.flink.config.parser.converter;

import com.bugjc.flink.config.parser.converter.impl.*;
import com.bugjc.flink.config.parser.TypeUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 类型数据解析工具类
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class NewFieldValueConverterUtil {


    private final static Map<Type, NewFieldValueConverter<?>> REGISTER = new HashMap<Type, NewFieldValueConverter<?>>() {{
        put(Collection.class, CollectionNewFieldValueConverter.INSTANCE);
        put(List.class, CollectionNewFieldValueConverter.INSTANCE);
        put(ArrayList.class, CollectionNewFieldValueConverter.INSTANCE);

        put(String.class, StringNewFieldValueConverter.INSTANCE);
        put(StringBuffer.class, StringNewFieldValueConverter.INSTANCE);
        put(StringBuilder.class, StringNewFieldValueConverter.INSTANCE);
        put(char.class, CharacterNewFieldValueConverter.INSTANCE);
        put(Character.class, CharacterNewFieldValueConverter.INSTANCE);
        put(byte.class, ByteNewFieldValueConverter.INSTANCE);
        put(Byte.class, ByteNewFieldValueConverter.INSTANCE);
        put(short.class, ShortNewFieldValueConverter.INSTANCE);
        put(Short.class, ShortNewFieldValueConverter.INSTANCE);
        put(int.class, IntegerNewFieldValueConverter.INSTANCE);
        put(Integer.class, IntegerNewFieldValueConverter.INSTANCE);
        put(long.class, LongNewFieldValueConverter.INSTANCE);
        put(Long.class, LongNewFieldValueConverter.INSTANCE);
        put(BigInteger.class, BigIntegerNewFieldValueConverter.INSTANCE);
        put(BigDecimal.class, BigDecimalNewFieldValueConverter.INSTANCE);
        put(float.class, FloatNewFieldValueConverter.INSTANCE);
        put(Float.class, FloatNewFieldValueConverter.INSTANCE);
        put(double.class, DoubleNewFieldValueConverter.INSTANCE);
        put(Double.class, DoubleNewFieldValueConverter.INSTANCE);
        put(boolean.class, BooleanNewFieldValueConverter.INSTANCE);
        put(Boolean.class, BooleanNewFieldValueConverter.INSTANCE);
        put(char[].class, CharArrayNewFieldValueConverter.INSTANCE);
        put(String[].class, StringArrayNewFieldValueConverter.INSTANCE);

        put(AtomicBoolean.class, BooleanNewFieldValueConverter.INSTANCE);
        put(AtomicInteger.class, IntegerNewFieldValueConverter.INSTANCE);
        put(AtomicLong.class, LongNewFieldValueConverter.INSTANCE);
    }};

    /**
     * 获取类型转换后的值
     *
     * @param type      --字段类型
     * @param value     --字段值
     * @return  转换后的值
     */
    public static Object getNewFieldValue(Type type, String value) {
        NewFieldValueConverter<?> converter = REGISTER.get(type);
        if (converter != null) {
            return converter.transform(value);
        }

        if (TypeUtil.isEnum(type)) {
            return REGISTER.get(String.class).transform(value);
        }

        return null;
    }
}
