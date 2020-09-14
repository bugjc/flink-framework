package com.bugjc.flink.config.core.enums;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.util.TypeUtil;

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

        if (field.getVirtualType() != null) {
            return field.getVirtualType();
        }

        Type type = field.getType();
        if (TypeUtil.isMap(type)) {
            if (isJavaBean(field)) {
                return ContainerType.HashMap_Entity;
            }
            return ContainerType.HashMap;
        } else if (TypeUtil.isList(type)) {
            if (isJavaBean(field)) {
                return ContainerType.ArrayList_Entity;
            }
            return ContainerType.ArrayList;
        } else if (TypeUtil.isBasic(type)) {
            return ContainerType.Basic;
        } else {
            return ContainerType.None;
        }
    }

    /**
     * 判断字段类型是否为 Java Bean
     *
     * @param field
     * @return
     */
    private static boolean isJavaBean(NewField field) {
        boolean isParameterizedType = field.getGenericType() instanceof ParameterizedType;
        if (isParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] types = parameterizedType.getActualTypeArguments();
            //获取最后的泛型类
            Class<?> valueType = (Class<?>) types[types.length - 1];
            return TypeUtil.isJavaBean(valueType);
        }
        return false;
    }
}
