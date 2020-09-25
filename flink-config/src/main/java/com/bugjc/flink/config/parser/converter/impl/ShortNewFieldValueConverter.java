package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 短整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class ShortNewFieldValueConverter implements NewFieldValueConverter<Short> {

    public final static ShortNewFieldValueConverter INSTANCE = new ShortNewFieldValueConverter();

    @Override
    public Short transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Short.valueOf(value);
    }
}
