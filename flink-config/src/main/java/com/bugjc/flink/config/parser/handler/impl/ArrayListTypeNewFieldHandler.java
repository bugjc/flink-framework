package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.model.tree.TrieNode;
import com.bugjc.flink.config.parser.*;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * List 字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class ArrayListTypeNewFieldHandler implements NewFieldHandler {

    public final static ArrayListTypeNewFieldHandler INSTANCE = new ArrayListTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {

        NewField field = input.getCurrentField();
        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();

        ParameterizedType parameterizedType = (ParameterizedType) input.getCurrentField().getGenericType();
        Type valueType = parameterizedType.getActualTypeArguments()[0];

        if (TypeUtil.isList(valueType)) {
            List<NewField> valueFields = new ArrayList<>();
            List<TrieNode> children = input.getTrieNode().getChildren();
            for (TrieNode child : children) {
                NewField newField = new NewField(child.getData(), field.getType(), valueType, ContainerType.ArrayList);
                valueFields.add(newField);
            }

            GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.ArrayList);
            Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
            deconstruction(newInput, output);
            return;
        } else if (TypeUtil.isMap(valueType)) {
            List<NewField> valueFields = new ArrayList<>();
            List<TrieNode> children = input.getTrieNode().getChildren();
            for (TrieNode child : children) {
                NewField newField = new NewField(child.getData(), field.getType(), valueType, ContainerType.HashMap);
                valueFields.add(newField);
            }

            GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.ArrayList);
            Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
            deconstruction(newInput, output);
            return;
        } else if (TypeUtil.isBasic(valueType)) {
            VirtualArrayListTypeNewFieldHandler.INSTANCE.process(input, output);
            return;
        }
        throw new NullPointerException();
    }
}
