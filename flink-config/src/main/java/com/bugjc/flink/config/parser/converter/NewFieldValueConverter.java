package com.bugjc.flink.config.parser.converter;

/**
 * 数据转换
 * @author aoki
 * @date 2020/9/16
 * **/
public interface NewFieldValueConverter<T> {

    /**
     * 数据转换
     * @param value     --属性字段值
     * @return  T       --转换后的值
     */
   T transform(String value);
}
