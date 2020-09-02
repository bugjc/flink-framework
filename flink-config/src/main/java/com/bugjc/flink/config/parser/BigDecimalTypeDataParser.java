package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class BigDecimalTypeDataParser implements TypeDataParser {
    public final static BigDecimalTypeDataParser INSTANCE = new BigDecimalTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        String value = newField.getValue();
        return value == null
                ? null
                : (T) TypeUtils.castToBigDecimal(value);
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), BigDecimal.class, BigDecimal.class)).collect(Collectors.toList());
    }
}
