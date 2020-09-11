package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符解析器
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class CharacterTypeDataParser implements TypeDataParser {

    public final static CharacterTypeDataParser INSTANCE = new CharacterTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        String value = newField.getValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return (T) TypeUtils.castToChar(value);
    }
}
