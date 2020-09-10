package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 整型
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class LongTypeDataParser implements TypeDataParser {
    public final static LongTypeDataParser INSTANCE = new LongTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        if (StringUtils.isBlank(newField.getValue())) {
            return null;
        }
        Long longObject = TypeUtils.castToLong(newField.getValue());
        return newField.getType() == AtomicLong.class
                ? (T) new AtomicLong(longObject.longValue())
                :  (T) longObject;
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        return trieNode.getChildren().stream().map(trieNode1 -> new NewField(trieNode1.getData(), Long.class, Long.class)).collect(Collectors.toList());
    }
}
