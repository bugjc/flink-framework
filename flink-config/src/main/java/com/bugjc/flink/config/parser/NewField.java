package com.bugjc.flink.config.parser;

import lombok.Data;

import java.lang.reflect.Type;

/**
 * 属性字段
 *
 * @author aoki
 * @date 2020/8/12
 **/
@Data
public class NewField {
    /**
     * 字段名
     */
    private String name;

    /**
     * 字段类型
     */
    private Class<?> type;

    /**
     * 字段类型
     */
    private Type genericType;

    /**
     * 容器类型字段
     */
    private ContainerType containerType;

    public NewField(String name, Class<?> type, Type genericType) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
    }

    public NewField(String name, Class<?> type, Type genericType, ContainerType containerType) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.containerType = containerType;
    }
}
