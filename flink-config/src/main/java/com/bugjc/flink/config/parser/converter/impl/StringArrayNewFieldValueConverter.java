package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符串数组类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class StringArrayNewFieldValueConverter implements NewFieldValueConverter<String[]> {
    public final static StringArrayNewFieldValueConverter INSTANCE = new StringArrayNewFieldValueConverter();

    @Override
    public String[] transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.split(",");
    }
}
