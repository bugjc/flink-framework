package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.ContainerType;
import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.GroupContainer;
import com.bugjc.flink.config.parser.NewField;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.model.tree.TrieNode;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.util.ArrayList;
import java.util.List;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * 基础字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class HashMapEntityTypeNewFieldHandler implements NewFieldHandler {

    public final static HashMapEntityTypeNewFieldHandler INSTANCE = new HashMapEntityTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {
        //HashMap_Entity 类型的字段递归解构非字段部分，如：属性配置 com.bugjc.map.key.entity.field1 中的 key 部分
        NewField field = input.getCurrentField();
        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();

        List<NewField> valueFields = new ArrayList<>();
        List<TrieNode> children = input.getTrieNode().getChildren();
        for (TrieNode child : children) {
            NewField newField = new NewField(child.getData(), field.getType(), field.getGenericType(), ContainerType.Virtual_HashMap_Entity);
            valueFields.add(newField);
        }

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.HashMap_Entity);
        Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
        deconstruction(newInput, output);
    }
}
