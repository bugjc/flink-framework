package com.bugjc.flink.config.util;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.EnumDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.MapDeserializer;
import com.alibaba.fastjson.serializer.CollectionCodec;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;
import com.bugjc.flink.config.parser.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 类型工具类
 *
 * @author aoki
 * @date 2020/8/28
 **/
public class TypeUtil {

    private final static Map<Type, TypeDataParser> PARSER_MAP = new HashMap<>();

    static {
        initTypeDataParser();
    }

    private static void initTypeDataParser() {
//        PARSER_MAP.put(Map.class, MapDeserializer.instance);
//        PARSER_MAP.put(HashMap.class, MapDeserializer.instance);
//        PARSER_MAP.put(LinkedHashMap.class, MapDeserializer.instance);
//        PARSER_MAP.put(TreeMap.class, MapDeserializer.instance);
//        PARSER_MAP.put(ConcurrentMap.class, MapDeserializer.instance);
//        PARSER_MAP.put(ConcurrentHashMap.class, MapDeserializer.instance);
        //基本类型的 list 数据处理
        PARSER_MAP.put(Collection.class, CollectionTypeDataParser.INSTANCE);
        PARSER_MAP.put(List.class, CollectionTypeDataParser.INSTANCE);
        PARSER_MAP.put(ArrayList.class, CollectionTypeDataParser.INSTANCE);
//
//        PARSER_MAP.put(Object.class, JavaObjectDeserializer.instance);

        PARSER_MAP.put(String.class, StringTypeDataParser.INSTANCE);
        PARSER_MAP.put(StringBuffer.class, StringTypeDataParser.INSTANCE);
        PARSER_MAP.put(StringBuilder.class, StringTypeDataParser.INSTANCE);
        PARSER_MAP.put(char.class, CharacterTypeDataParser.INSTANCE);
        PARSER_MAP.put(Character.class, CharacterTypeDataParser.INSTANCE);
        PARSER_MAP.put(byte.class, NumberTypeDataParser.INSTANCE);
        PARSER_MAP.put(Byte.class, NumberTypeDataParser.INSTANCE);
        PARSER_MAP.put(short.class, NumberTypeDataParser.INSTANCE);
        PARSER_MAP.put(Short.class, NumberTypeDataParser.INSTANCE);
        PARSER_MAP.put(int.class, IntegerTypeDataParser.INSTANCE);
        PARSER_MAP.put(Integer.class, IntegerTypeDataParser.INSTANCE);
        PARSER_MAP.put(long.class, LongTypeDataParser.INSTANCE);
        PARSER_MAP.put(Long.class, LongTypeDataParser.INSTANCE);
        PARSER_MAP.put(BigInteger.class, BigIntegerTypeDataParser.INSTANCE);
        PARSER_MAP.put(BigDecimal.class, BigDecimalTypeDataParser.INSTANCE);
        PARSER_MAP.put(float.class, FloatTypeDataParser.INSTANCE);
        PARSER_MAP.put(Float.class, FloatTypeDataParser.INSTANCE);
        PARSER_MAP.put(double.class, NumberTypeDataParser.INSTANCE);
        PARSER_MAP.put(Double.class, NumberTypeDataParser.INSTANCE);
        PARSER_MAP.put(boolean.class, BooleanTypeDataParser.INSTANCE);
        PARSER_MAP.put(Boolean.class, BooleanTypeDataParser.INSTANCE);
        PARSER_MAP.put(char[].class, CharArrayTypeDataParser.INSTANCE);
        PARSER_MAP.put(String[].class, StringArrayTypeDataParser.INSTANCE);

        PARSER_MAP.put(AtomicBoolean.class, BooleanTypeDataParser.INSTANCE);
        PARSER_MAP.put(AtomicInteger.class, IntegerTypeDataParser.INSTANCE);
        PARSER_MAP.put(AtomicLong.class, LongTypeDataParser.INSTANCE);

//        PARSER_MAP.put(Serializable.class, JavaObjectDeserializer.instance);
//        PARSER_MAP.put(Cloneable.class, JavaObjectDeserializer.instance);
//        PARSER_MAP.put(Comparable.class, JavaObjectDeserializer.instance);
//        PARSER_MAP.put(Closeable.class, JavaObjectDeserializer.instance);
    }


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
        } else if (isMap(type)) {
            return false;
        } else if (isList(type)) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 获取数据
     *
     * @param <T>
     * @param newField
     * @return
     */
    public static <T> T getTypeData(NewField newField) {
        TypeDataParser typeDataParser = PARSER_MAP.get(newField.getType());
        if (typeDataParser != null) {
            return typeDataParser.getTypeData(newField);
        }

        if (isEnum(newField.getType())) {
            return PARSER_MAP.get(String.class).getTypeData(newField);
        }

        return null;
    }

    /**
     * 获取要处理的字段列表
     *
     * @param type
     * @param trieNode
     * @return
     */
    public static List<NewField> getNewFields(Class<?> type, TrieNode trieNode) {
        TypeDataParser typeDataParser = PARSER_MAP.get(type);
        if (typeDataParser == null) {
            return null;
        }
        return typeDataParser.getNewFields(trieNode);
    }
}
