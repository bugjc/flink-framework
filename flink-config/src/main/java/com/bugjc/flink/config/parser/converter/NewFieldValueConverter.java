package com.bugjc.flink.config.parser.converter;

/**
 * 数据转换
 * @author aoki
 * @date 2020/9/16
 * **/
public interface NewFieldValueConverter<T> {

    /**
     * 数据转换
     * @param value
     * @return
     */
    T transform(String value);
}
