package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.ContainerType;
import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.GroupContainer;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.ParameterizedType;

import static com.bugjc.flink.config.parser.PropertyParser.deconstruction;

/**
 * ArrayList Entity 字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class VirtualArrayListEntityTypeNewFieldHandler implements NewFieldHandler {

    public final static VirtualArrayListEntityTypeNewFieldHandler INSTANCE = new VirtualArrayListEntityTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {

        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        ContainerType upperContainerType = output.getCurrentGroupContainer().getUpperContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();

        ParameterizedType parameterizedType = (ParameterizedType) input.getCurrentField().getGenericType();
        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, upperContainerType);
        Params newInput = Params.create(nextGroupContainer, input.getEntityFields(valueType), input.getOriginalData());
        deconstruction(newInput, output);
    }
}
