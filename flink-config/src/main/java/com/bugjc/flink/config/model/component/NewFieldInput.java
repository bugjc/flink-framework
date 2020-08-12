package com.bugjc.flink.config.model.component;

import lombok.Data;

import java.util.List;

/**
 * 数据分组
 *
 * @author aoki
 * @date 2020/8/12
 **/
@Data
public class NewFieldInput {
    /**
     * 分组名
     */
    private String name;
    /**
     * 分组数据存储的容器
     */
    private Enum<Type> typeEnum;

    /**
     * 组件属性前缀
     */
    private String prefix;

    /**
     * 字段列表
     */
    private List<NewField> fields;

    public NewFieldInput(String name, Type typeEnum, String prefix, List<NewField> fields) {
        this.name = name;
        this.typeEnum = typeEnum;
        if (!prefix.endsWith(".")) {
            this.prefix = prefix + ".";
        } else {
            this.prefix = prefix;
        }

        this.fields = fields;
    }


    public enum Type {
        //None
        None,
        //String
        String,
        //Array
        Array,
        // ArrayList
        ArrayList,
        // HashMap
        HashMap
    }
}
