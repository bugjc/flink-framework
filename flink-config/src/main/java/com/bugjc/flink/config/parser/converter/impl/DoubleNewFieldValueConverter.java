package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 双精度浮点类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class DoubleNewFieldValueConverter implements NewFieldValueConverter<Double> {

    public final static DoubleNewFieldValueConverter INSTANCE = new DoubleNewFieldValueConverter();

    @Override
    public Double transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Double.valueOf(value);
    }
}
