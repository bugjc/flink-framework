package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class StringArrayTypeDataParser implements TypeDataParser {
    public final static StringArrayTypeDataParser INSTANCE = new StringArrayTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        return (T) newField.getValue().split(",");
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), StringArray.class, StringArray.class)).collect(Collectors.toList());
    }
}
