package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Long 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class LongNewFieldValueConverter implements NewFieldValueConverter<Long> {
    public final static LongNewFieldValueConverter INSTANCE = new LongNewFieldValueConverter();

    @Override
    public Long transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Long.valueOf(value);
    }
}
