package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class IntegerTypeDataParser implements TypeDataParser {
    public final static IntegerTypeDataParser INSTANCE = new IntegerTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        return (T) TypeUtils.castToInt(newField.getValue());
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), Integer.class, Integer.class)).collect(Collectors.toList());
    }
}
