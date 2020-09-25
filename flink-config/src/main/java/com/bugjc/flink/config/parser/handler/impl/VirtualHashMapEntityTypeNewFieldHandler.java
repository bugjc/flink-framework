package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.ContainerType;
import com.bugjc.flink.config.parser.GroupContainer;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;
import com.bugjc.flink.config.util.TypeUtil;

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

        //判断类型是否存在嵌套
        Type valueType = input.getCurrentField().getGenericType();
        if (TypeUtil.isMap(valueType)) {
            //解析下一级
            HashMapEntityTypeNewFieldHandler.INSTANCE.process(input, output);
            return;
        }

        if (TypeUtil.isList(valueType)) {
            //解析下一级
            ArrayListEntityTypeNewFieldHandler.INSTANCE.process(input, output);
            return;
        }

        ContainerType currentContainerType = output.getCurrentGroupContainer().getCurrentContainerType();
        ContainerType upperContainerType = output.getCurrentGroupContainer().getUpperContainerType();
        String currentGroupName = output.getCurrentGroupContainer().getCurrentGroupName();

        GroupContainer nextGroupContainer = GroupContainer.create(currentContainerType, currentGroupName, upperContainerType);
        Params newInput = Params.create(nextGroupContainer, input.getEntityFields(valueType), input.getOriginalData());
        deconstruction(newInput, output);
    }
}
