package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.model.tree.TrieNode;
import com.bugjc.flink.config.parser.*;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;
import com.bugjc.flink.config.parser.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * List<Entity> 字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class ArrayListEntityTypeNewFieldHandler implements NewFieldHandler {

    public final static ArrayListEntityTypeNewFieldHandler INSTANCE = new ArrayListEntityTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {
        //ArrayList_Entity 类型的字段递归解构非字段部分，如：属性配置 com.bugjc.list.[0].field1 中的 [0] 部分
        NewField field = input.getCurrentField();
        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();

        ParameterizedType parameterizedType = (ParameterizedType) input.getCurrentField().getGenericType();
        Type valueType = parameterizedType.getActualTypeArguments()[0];

        if (TypeUtil.isList(valueType)) {
            List<NewField> valueFields = new ArrayList<>();
            List<TrieNode> children = input.getTrieNode().getChildren();
            for (TrieNode child : children) {
                NewField newField = new NewField(child.getData(), field.getType(), valueType, ContainerType.ArrayList_Entity);
                valueFields.add(newField);
            }

            GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.ArrayList_Entity);
            Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
            deconstruction(newInput, output);
            return;
        } else if (TypeUtil.isMap(valueType)) {
            List<NewField> valueFields = new ArrayList<>();
            List<TrieNode> children = input.getTrieNode().getChildren();
            for (TrieNode child : children) {
                NewField newField = new NewField(child.getData(), field.getType(), valueType, ContainerType.HashMap_Entity);
                valueFields.add(newField);
            }

            GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.ArrayList_Entity);
            Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
            deconstruction(newInput, output);
            return;
        } else if (TypeUtil.isJavaBean(valueType)){

            List<NewField> valueFields = new ArrayList<>();
            List<TrieNode> children = input.getTrieNode().getChildren();
            for (TrieNode child : children) {
                NewField newField = new NewField(child.getData(), field.getType(), valueType, ContainerType.Virtual_ArrayList_Entity);
                valueFields.add(newField);
            }

            GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.ArrayList_Entity);
            Params newInput = Params.create(nextGroupContainer, valueFields, input.getOriginalData());
            deconstruction(newInput, output);
            return;
        }

        throw new NullPointerException();


    }
}
