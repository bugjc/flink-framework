package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字符串类型的数据解析
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class StringTypeDataParser implements TypeDataParser {

    public final static StringTypeDataParser INSTANCE = new StringTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        Type type = newField.getType();
        String value = newField.getValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (type == StringBuffer.class) {
            return (T) new StringBuffer(value);
        }

        if (type == StringBuilder.class) {
            return (T) new StringBuilder(value);
        }

        return (T) value;
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), String.class, String.class)).collect(Collectors.toList());
    }
}
