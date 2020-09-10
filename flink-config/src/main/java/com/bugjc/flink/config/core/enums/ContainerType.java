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
    // HashMap          如：Map<String,String> 类型的变量
    HashMap,
    // HashMap_Entity   如：Map<String,Entity> 类型的变量
    HashMap_Entity,
    //HashMap 的虚拟 key
    Virtual;


    /**
     * 获取容器类型
     *
     * @param field --字段
     * @return
     */
    public static ContainerType getType(NewField field) {

        if (field.isVirtualField()){
            return ContainerType.Virtual;
        }

        Type type = field.getType();
        if (TypeUtil.isMap(type)) {
            boolean isParameterizedType = field.getGenericType() instanceof ParameterizedType;
            if (isParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] types = parameterizedType.getActualTypeArguments();
                Class<?> valueType = (Class<?>) types[types.length - 1];
                if (TypeUtil.isJavaBean(valueType)) {
                    return ContainerType.HashMap_Entity;
                }
            }

            return ContainerType.HashMap;
        } else if (TypeUtil.isList(type)) {
            return ContainerType.ArrayList;
        } else if (TypeUtil.isBasic(type)) {
            return ContainerType.Basic;
        } else {
            return ContainerType.None;
        }
    }
}
