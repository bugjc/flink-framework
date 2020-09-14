package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.component.NewField;
import org.apache.commons.lang3.StringUtils;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class StringArrayTypeDataParser implements TypeDataParser {
    public final static StringArrayTypeDataParser INSTANCE = new StringArrayTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        if (StringUtils.isBlank(newField.getValue())) {
            return null;
        }

        return (T) newField.getValue().split(",");
    }
}
