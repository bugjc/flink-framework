package com.bugjc.flink.config.model.component;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 数据分组
 *
 * @author aoki
 * @date 2020/8/12
 **/
public class Params {

    /**
     * 分组容器
     */
    @Getter
    private final GroupContainer groupContainer;

    /**
     * 要处理的字段列表
     */
    @Getter
    private final List<NewField> fields;

    /**
     * 原始数据
     */
    @Getter
    private final Map<String, String> originalData;

    public Params(GroupContainer groupContainer, List<NewField> fields, Map<String, String> originalData) {
        this.groupContainer = groupContainer;
        this.fields = fields;
        this.originalData = originalData;
    }

    /**
     * 创建
     * @param groupContainer
     * @param fields
     * @param originalData
     * @return
     */
    public static Params create(GroupContainer groupContainer, List<NewField> fields, Map<String, String> originalData) {
        return new Params(groupContainer, fields, originalData);
    }

}
