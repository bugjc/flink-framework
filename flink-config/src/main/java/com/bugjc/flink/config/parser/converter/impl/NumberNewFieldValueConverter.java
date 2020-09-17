package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 数字类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class NumberNewFieldValueConverter implements NewFieldValueConverter<Number> {

    public final static NumberNewFieldValueConverter INSTANCE = new NumberNewFieldValueConverter();

    @Override
    public Number transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return new Number() {
            @Override
            public int intValue() {
                return Integer.parseInt(value);
            }

            @Override
            public long longValue() {
                return Long.parseLong(value);
            }

            @Override
            public float floatValue() {
                return Float.parseFloat(value);
            }

            @Override
            public double doubleValue() {
                return Double.parseDouble(value);
            }
        };
    }
}
