package com.bugjc.flink.data.stream.api.operators;

import com.bugjc.flink.data.stream.api.data.sources.MySourceFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.Test;

class MapOperatorsTest {

    @Test
    void map() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new MySourceFunction()).map(item -> 1 + item.getKey1()).print();
        env.execute();
    }
}
