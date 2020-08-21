package com.bugjc.flink.config.model.component;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
    private Type type;

    /**
     * 组件属性前缀
     */
    private String prefix;

    /**
     * 当前处理的字段
     */
    private NewField currentNewField;

    /**
     * 字段列表
     */
    private List<NewField> fields;

    /**
     * key value
     */
    private Map<String, String> parameterTool;

    public NewFieldInput(String name, Type type, String prefix, List<NewField> fields,Map<String, String> parameterTool) {
        this.name = name;
        this.type = type;
        if (!prefix.endsWith(".")) {
            this.prefix = prefix + ".";
        } else {
            this.prefix = prefix;
        }

        this.fields = fields;
        this.parameterTool = parameterTool;
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
