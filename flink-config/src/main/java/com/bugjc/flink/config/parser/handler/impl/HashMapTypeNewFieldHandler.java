package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.model.tree.TrieNode;
import com.bugjc.flink.config.parser.*;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;
import com.bugjc.flink.config.util.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * HashMap 字段处理器
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
        //获取 Map<key,value> 类型
        Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        Type valueType = parameterizedType.getActualTypeArguments()[1];

        ContainerType virtualType = ContainerType.Virtual_HashMap;
        if (TypeUtil.isList(valueType)) {
            virtualType = ContainerType.ArrayList;
        }

        List<NewField> valueFields = new ArrayList<>();
        List<TrieNode> children = input.getTrieNode().getChildren();
        for (TrieNode child : children) {
            NewField newField = new NewField(child.getData(), keyType, valueType, virtualType);
            valueFields.add(newField);
        }

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.HashMap);
        Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
        deconstruction(newInput, output);
    }
}
