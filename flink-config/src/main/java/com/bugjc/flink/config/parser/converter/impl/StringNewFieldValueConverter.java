package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;

/**
 * 字符串类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class StringNewFieldValueConverter implements NewFieldValueConverter<String> {

    public final static StringNewFieldValueConverter INSTANCE = new StringNewFieldValueConverter();

    @Override
    public String transform(String value) {
        return value;
    }
}
