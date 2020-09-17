package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * BigDecimal 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class BigDecimalNewFieldValueConverter implements NewFieldValueConverter<BigDecimal> {
    public final static BigDecimalNewFieldValueConverter INSTANCE = new BigDecimalNewFieldValueConverter();

    @Override
    public BigDecimal transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value);
    }
}
