package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import org.apache.commons.lang3.StringUtils;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class IntegerTypeDataParser implements TypeDataParser {
    public final static IntegerTypeDataParser INSTANCE = new IntegerTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        if (StringUtils.isBlank(newField.getValue())) {
            return null;
        }
        return (T) TypeUtils.castToInt(newField.getValue());
    }
}
