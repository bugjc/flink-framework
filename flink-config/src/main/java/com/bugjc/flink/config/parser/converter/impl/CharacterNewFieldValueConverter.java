package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符 类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class CharacterNewFieldValueConverter implements NewFieldValueConverter<Character> {

    public final static CharacterNewFieldValueConverter INSTANCE = new CharacterNewFieldValueConverter();

    @Override
    public Character transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.charAt(0);
    }
}
