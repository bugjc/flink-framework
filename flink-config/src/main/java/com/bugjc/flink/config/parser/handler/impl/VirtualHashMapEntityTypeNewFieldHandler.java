package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.ContainerType;
import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.GroupContainer;
import com.bugjc.flink.config.parser.NewField;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * 基础字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class VirtualHashMapEntityTypeNewFieldHandler implements NewFieldHandler {

    public final static VirtualHashMapEntityTypeNewFieldHandler INSTANCE = new VirtualHashMapEntityTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {
        NewField field = input.getCurrentField();
        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        ContainerType upperContainerType = output.getCurrentGroupContainer().getUpperContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();

        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type[] types = parameterizedType.getActualTypeArguments();
        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[types.length - 1];

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, upperContainerType);
        Params newInput = Params.create(nextGroupContainer, input.getEntityFields(valueType), input.getOriginalData());
        deconstruction(newInput, output);
    }
}
