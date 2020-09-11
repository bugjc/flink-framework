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
public class BigIntegerTypeDataParser implements TypeDataParser {
    public final static BigIntegerTypeDataParser INSTANCE = new BigIntegerTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        String value = newField.getValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return (T) TypeUtils.castToBigInteger(value);
    }
}