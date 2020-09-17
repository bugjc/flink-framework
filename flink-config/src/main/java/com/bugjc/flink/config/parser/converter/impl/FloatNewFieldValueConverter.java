package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Float 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class FloatNewFieldValueConverter implements NewFieldValueConverter<Float> {
    public final static FloatNewFieldValueConverter INSTANCE = new FloatNewFieldValueConverter();

    @Override
    public Float transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Float.valueOf(value);
    }
}
