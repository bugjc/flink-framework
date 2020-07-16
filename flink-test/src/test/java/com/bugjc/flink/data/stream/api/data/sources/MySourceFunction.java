package com.bugjc.flink.data.stream.api.data.sources;

import cn.hutool.core.util.RandomUtil;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class MySourceFunction implements SourceFunction<MySource> {
    @Override
    public void run(SourceContext<MySource> ctx) throws Exception {
        for (int i = 0; i < 1000; i++) {
            String key1 = RandomUtil.randomNumbers(5) + "," + RandomUtil.randomNumbers(5);
            ctx.collect(new MySource(key1));
            Thread.sleep(1000);
        }

    }

    @Override
    public void cancel() {

    }
}
