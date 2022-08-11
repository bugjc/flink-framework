package com.bugjc.flink.config.parser;


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
        return type.getTypeName().endsWith("Entity") || type.getTypeName().endsWith("JavaBean");
    }

    public static boolean isEnum(Type type) {
        return type.getTypeName().endsWith("Enum");
    }

    public static boolean isList(Type type) {
        return type.getTypeName().startsWith("java.util.List") || type.getTypeName().startsWith("java.util.ArrayList");
    }

    public static boolean isMap(Type type) {
        return type.getTypeName().startsWith("java.util.Map") || type.getTypeName().startsWith("java.util.HashMap");
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
     * @param valueType     --字段值类型
     * @return  返回泛型的最后一个类型
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
