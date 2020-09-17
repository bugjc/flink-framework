package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.ContainerType;
import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.GroupContainer;
import com.bugjc.flink.config.parser.NewField;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.model.tree.TrieNode;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * 基础字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class HashMapTypeNewFieldHandler implements NewFieldHandler {

    public final static HashMapTypeNewFieldHandler INSTANCE = new HashMapTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {

        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();
        ParameterizedType parameterizedType = (ParameterizedType) input.getCurrentField().getGenericType();
        Type[] types = parameterizedType.getActualTypeArguments();
        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[types.length - 1];

        List<NewField> valueFields = new ArrayList<>();
        List<TrieNode> children = input.getTrieNode().getChildren();
        for (TrieNode child : children) {
            NewField newField = new NewField(child.getData(), valueType, valueType, ContainerType.Virtual_HashMap);
            valueFields.add(newField);
        }

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.HashMap);
        Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
        deconstruction(newInput, output);
    }
}
