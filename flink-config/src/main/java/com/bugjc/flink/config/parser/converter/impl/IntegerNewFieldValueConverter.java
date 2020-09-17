package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Integer 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class IntegerNewFieldValueConverter implements NewFieldValueConverter<Integer> {
    public final static IntegerNewFieldValueConverter INSTANCE = new IntegerNewFieldValueConverter();

    @Override
    public Integer transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }
}
