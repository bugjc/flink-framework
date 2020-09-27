package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.parser.TypeUtil;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;

import java.lang.reflect.Type;

/**
 * HashMap 基础类型字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class VirtualHashMapTypeNewFieldHandler implements NewFieldHandler {

    public final static VirtualHashMapTypeNewFieldHandler INSTANCE = new VirtualHashMapTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {
        //判断类型是否存在嵌套
        Type valueType = input.getCurrentField().getGenericType();
        if (!TypeUtil.isBasic(valueType)) {
            throw new NullPointerException();
        }
        BasicTypeNewFieldHandler.INSTANCE.process(input, output);
    }
}
