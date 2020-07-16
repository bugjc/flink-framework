package com.bugjc.flink.data.stream.api.operators;

import com.bugjc.flink.data.stream.api.data.sources.MySource;
import com.bugjc.flink.data.stream.api.data.sources.MySourceFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.Test;

class FilterOperatorsTest {

    @Test
    void filter() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new MySourceFunction()).filter(new FilterFunction<MySource>() {
            @Override
            public boolean filter(MySource value) throws Exception {
                return value.getKey1().startsWith("1");
            }
        }).print();
        env.execute();
    }
}
