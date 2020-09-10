package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class FloatTypeDataParser implements TypeDataParser {
    public final static FloatTypeDataParser INSTANCE = new FloatTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        String value = newField.getValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return (T) TypeUtils.castToFloat(value);
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), Float.class, Float.class)).collect(Collectors.toList());
    }
}
