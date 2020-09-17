package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

/**
 * BigInteger 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class BigIntegerNewFieldValueConverter implements NewFieldValueConverter<BigInteger> {
    public final static BigIntegerNewFieldValueConverter INSTANCE = new BigIntegerNewFieldValueConverter();

    @Override
    public BigInteger transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigInteger(value);
    }
}
