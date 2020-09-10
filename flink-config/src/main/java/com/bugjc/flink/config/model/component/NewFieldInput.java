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
     * 分组容器
     */
    private GroupContainer groupContainer;

    /**
     * 要处理的字段列表
     */
    private List<NewField> fields;

    /**
     * 原始数据
     */
    private Map<String, String> originalData;

    public NewFieldInput(GroupContainer groupContainer, List<NewField> fields, Map<String, String> parameterTool) {
        this.groupContainer = groupContainer;
        this.fields = fields;
        this.originalData = parameterTool;
    }


}
