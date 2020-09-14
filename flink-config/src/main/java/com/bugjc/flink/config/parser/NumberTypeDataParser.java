package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * 数字类型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class NumberTypeDataParser implements TypeDataParser {

    public final static NumberTypeDataParser INSTANCE = new NumberTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        Type type = newField.getType();
        String value = newField.getValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (type == double.class || type == Double.class) {
            return (T) Double.valueOf(Double.parseDouble(value));
        }

        if (type == short.class || type == Short.class) {
            short shortValue = TypeUtils.shortValue(new BigDecimal(value));
            return (T) Short.valueOf(shortValue);
        }

        if (type == byte.class || type == Byte.class) {
            byte byteValue = TypeUtils.byteValue(new BigDecimal(value));
            return (T) Byte.valueOf(byteValue);
        }

        return (T) value;
    }
}
