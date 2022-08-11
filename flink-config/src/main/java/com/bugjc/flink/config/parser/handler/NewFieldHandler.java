package com.bugjc.flink.config.parser.handler;

import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.Params;

/**
 * 字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public interface NewFieldHandler {

    /**
     * 处理函数
     *
     * @param input     --输入参数对象
     * @param output    --输出数据对象
     */
    void process(Params input, Container output);
}
