package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符数组
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class CharArrayNewFieldValueConverter implements NewFieldValueConverter<char[]> {
    public final static CharArrayNewFieldValueConverter INSTANCE = new CharArrayNewFieldValueConverter();

    @Override
    public char[] transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.toCharArray();
    }
}
