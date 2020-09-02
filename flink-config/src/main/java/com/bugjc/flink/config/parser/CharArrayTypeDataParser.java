package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;
import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class CharArrayTypeDataParser implements TypeDataParser {
    public final static CharArrayTypeDataParser INSTANCE = new CharArrayTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        String value = newField.getValue();
        return (T) (value).toCharArray();
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), CharArray.class, CharArray.class)).collect(Collectors.toList());
    }
}
