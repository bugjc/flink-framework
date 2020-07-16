package com.bugjc.flink.data.stream.api.operators;

import com.bugjc.flink.data.stream.api.data.sources.MySource;
import com.bugjc.flink.data.stream.api.data.sources.MySourceFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.Test;

class FlatMapOperatorsTest {

    @Test
    void flatMap() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new MySourceFunction()).flatMap(new FlatMapFunction<MySource, String>() {
            @Override
            public void flatMap(MySource value, Collector<String> out) throws Exception {
                String[] keyArr = value.getKey1().split(",");
                for (String s : keyArr) {
                    out.collect(s);
                }
            }
        }).print();
        env.execute();
    }
}
