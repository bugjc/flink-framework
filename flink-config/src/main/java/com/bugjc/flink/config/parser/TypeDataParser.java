package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.component.NewField;

public interface TypeDataParser {

    /**
     * 解析类型数据
     *
     * @param newField -- 输入参数
     * @param <T>      --返回的数据类型
     * @return --解析的类型数据
     */
    <T> T getTypeData(NewField newField);
}