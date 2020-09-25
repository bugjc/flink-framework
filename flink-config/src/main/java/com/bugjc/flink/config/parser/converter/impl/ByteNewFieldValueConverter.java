package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 字节类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class ByteNewFieldValueConverter implements NewFieldValueConverter<Byte> {

    public final static ByteNewFieldValueConverter INSTANCE = new ByteNewFieldValueConverter();

    @Override
    public Byte transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Byte.valueOf(value);
    }
}
