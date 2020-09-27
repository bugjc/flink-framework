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

        ContainerType containerType;
        if (TypeUtil.isList(valueType)) {
            containerType = ContainerType.ArrayList_Entity;
        } else if (TypeUtil.isMap(valueType)) {
            containerType = ContainerType.HashMap_Entity;
        } else if (TypeUtil.isJavaBean(valueType)) {
            containerType = ContainerType.Virtual_ArrayList_Entity;
        } else {
            throw new NullPointerException();
        }

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.ArrayList_Entity);
        Params newInput = Params.create(
                nextGroupContainer,
                input.getFields(field.getType(), valueType, containerType),
                input.getOriginalData());
        deconstruction(newInput, output);
    }
}
