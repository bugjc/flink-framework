package com.bugjc.flink.config.model.component;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Type;

/**
 * 属性字段
 *
 * @author aoki
 * @date 2020/8/12
 **/
@Data
@AllArgsConstructor
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
     * 配置文件定义的 key
     */
    private String key;

    /**
     * 配置文件定义的 value
     */
    private String value;

    /**
     * 是否虚拟字段
     */
    private boolean virtualField = false;

    public NewField(String name, Class<?> type, Type genericType) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
    }

    public NewField(String name, Class<?> type, Type genericType, boolean virtualField) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.virtualField = virtualField;
    }
}
