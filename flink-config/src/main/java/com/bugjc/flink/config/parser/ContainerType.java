package com.bugjc.flink.config.parser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 容器类型
 *
 * @author aoki
 * @date 2020/9/4
 **/
public enum ContainerType {
    //None(也表示初始状态)
    None,
    //基础类型
    Basic,
    // ArrayList
    ArrayList,
    Virtual_ArrayList,
    ArrayList_Entity,
    Virtual_ArrayList_Entity,
    // HashMap          如：Map<String,String> 类型的变量
    HashMap,
    Virtual_HashMap,
    // HashMap_Entity   如：Map<String,Entity> 类型的变量
    HashMap_Entity,
    Virtual_HashMap_Entity;


    /**
     * 获取容器类型
     *
     * @param field --字段
     * @return
     */
    public static ContainerType getType(NewField field) {

        if (field.getContainerType() != null) {
            return field.getContainerType();
        }

        /*Type type = field.getType();*/
        Type genericType = field.getGenericType();
        if (TypeUtil.isMap(genericType)) {
            if (isJavaBean(genericType)) {
                return ContainerType.HashMap_Entity;
            }
            return ContainerType.HashMap;
        } else if (TypeUtil.isList(genericType)) {
            if (isJavaBean(genericType)) {
                return ContainerType.ArrayList_Entity;
            }
            return ContainerType.ArrayList;
        } else if (TypeUtil.isBasic(genericType) || TypeUtil.isEnum(genericType)) {
            return ContainerType.Basic;
        } else {
            return ContainerType.None;
        }
    }

    /**
     * 判断字段类型是否为 Java Bean
     *
     * @param genericType
     * @return
     */
    private static boolean isJavaBean(Type genericType) {
        boolean isParameterizedType = genericType instanceof ParameterizedType;
        if (isParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] types = parameterizedType.getActualTypeArguments();
            //获取最后的泛型类
            Type valueType = TypeUtil.getLastType(types[types.length - 1]);
            return TypeUtil.isJavaBean(valueType);
        }
        return false;
    }


}
