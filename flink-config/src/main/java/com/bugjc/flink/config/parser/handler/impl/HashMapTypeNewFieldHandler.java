package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.*;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

        ContainerType containerType;
        if (TypeUtil.isList(valueType)) {
            containerType = ContainerType.ArrayList;
        } else if (TypeUtil.isMap(valueType)) {
            containerType = ContainerType.HashMap;
        } else if (TypeUtil.isBasic(valueType)) {
            containerType = ContainerType.Virtual_HashMap;
        } else {
            throw new NullPointerException();
        }

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, ContainerType.HashMap);
        Params newInput = Params.create(
                nextGroupContainer,
                input.getFields(keyType, valueType, containerType),
                input.getOriginalData());
        deconstruction(newInput, output);
    }
}
