package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;

import java.util.List;
import java.util.stream.Collectors;

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
        Object value = newField.getValue();
        return value == null
                ? null
                : (T) TypeUtils.castToChar(value);
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), Character.class, Character.class)).collect(Collectors.toList());
    }
}
