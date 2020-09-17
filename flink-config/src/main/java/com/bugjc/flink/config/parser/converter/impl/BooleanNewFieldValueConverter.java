package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Boolean 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class BooleanNewFieldValueConverter implements NewFieldValueConverter<Boolean> {
    public final static BooleanNewFieldValueConverter INSTANCE = new BooleanNewFieldValueConverter();

    @Override
    public Boolean transform(String value) {
        if (StringUtils.isBlank(value)){
            return null;
        }
        return Boolean.valueOf(value);
    }
}
