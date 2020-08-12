package com.bugjc.flink.config.model.component;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Type;

/**
 * 属性字段
 * @author aoki
 * @date 2020/8/12
 * **/
@Data
@AllArgsConstructor
public class NewField {
    private String name;
    private Class<?> type;
    private Type genericType;
}
