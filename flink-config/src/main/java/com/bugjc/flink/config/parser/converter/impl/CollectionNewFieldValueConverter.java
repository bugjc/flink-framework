package com.bugjc.flink.config.parser.converter.impl;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * 集合类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class CollectionNewFieldValueConverter implements NewFieldValueConverter<Collection<String>> {
    public final static CollectionNewFieldValueConverter INSTANCE = new CollectionNewFieldValueConverter();

    @Override
    public Collection<String> transform(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] arr = value.split(",");
        return Arrays.asList(arr);
    }
}
