package com.bugjc.flink.config.parser;

import com.alibaba.fastjson.util.TypeUtils;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.tree.TrieNode;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 字符串类型的数据解析
 *
 * @author aoki
 * @date 2020/9/1
 **/
public class CollectionTypeDataParser implements TypeDataParser {

    public final static CollectionTypeDataParser INSTANCE = new CollectionTypeDataParser();

    @Override
    public <T> T getTypeData(NewField newField) {
        Type type = newField.getType();
        String value = newField.getValue();
        Collection list = TypeUtils.createCollection(type);
        String[] arr = value.split(",");
        Collections.addAll(list, arr);
        return (T) list;
    }

    @Override
    public List<NewField> getNewFields(TrieNode trieNode) {
        throw new UnsupportedOperationException();
    }
}
