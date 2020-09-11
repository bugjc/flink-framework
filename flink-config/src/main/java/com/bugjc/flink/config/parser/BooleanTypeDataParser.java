package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.component.NewField;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class BooleanTypeDataParser implements TypeDataParser {
    public final static BooleanTypeDataParser INSTANCE = new BooleanTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        String value = newField.getValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Boolean aBoolean = Boolean.parseBoolean(value);
        if (newField.getType() == AtomicBoolean.class) {
            return (T) new AtomicBoolean(new Boolean(aBoolean));
        }

        return (T) aBoolean;
    }
}
