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
    private String groupName;
    /**
     * 分组数据存储的容器类型
     */
    private Type groupType;

    /**
     * 分组的属性前缀
     */
    private String groupPrefix;

    /**
     * 要处理的字段列表
     */
    private List<NewField> fields;

    /**
     * 原始数据
     */
    private Map<String, String> originalData;

    public NewFieldInput(String groupName, Type groupType, String groupPrefix, List<NewField> fields, Map<String, String> parameterTool) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.groupPrefix = groupPrefix;
        this.fields = fields;
        this.originalData = parameterTool;
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
        // HashMap          如：Map<String,String> 类型的变量
        HashMap,
        // HashMap_Entity   如：Map<String,Entity> 类型的变量
        HashMap_Entity
    }
}
