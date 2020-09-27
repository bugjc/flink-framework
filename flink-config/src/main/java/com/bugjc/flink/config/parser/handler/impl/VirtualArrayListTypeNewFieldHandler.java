package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.parser.TypeUtil;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * List 字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class VirtualArrayListTypeNewFieldHandler implements NewFieldHandler {

    public final static VirtualArrayListTypeNewFieldHandler INSTANCE = new VirtualArrayListTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {

        ParameterizedType parameterizedType = (ParameterizedType) input.getCurrentField().getGenericType();
        Type valueType = parameterizedType.getActualTypeArguments()[0];

        if (!TypeUtil.isBasic(valueType)) {
            throw new NullPointerException();
        }

        //重写 type,并使用基础类型处理器设置值
        input.getCurrentField().setType(List.class);
        BasicTypeNewFieldHandler.INSTANCE.process(input, output);
    }
}
