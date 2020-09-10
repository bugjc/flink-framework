package com.bugjc.flink.config.test;


import com.alibaba.fastjson.JSON;
import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.test.component.DataSourceConfig;
import com.bugjc.flink.config.test.component.KafkaConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.Random;

@Slf4j
class EnvironmentConfigTest {

    /**
     * 构建环境配置文件对象
     */
    private static EnvironmentConfig environmentConfig;

    @BeforeAll
    static void init() {
        try {
            environmentConfig = new EnvironmentConfig(new String[]{});
        } catch (Exception exception) {
            log.info("{}", exception.getMessage());
            log.error("初始化环境配置失败！");
        }
    }


    @Test
    void getStreamExecutionEnvironment() throws Exception {
        StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();
        DataStream<String> dataStream = env.addSource(new RichParallelSourceFunction<String>() {
            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<String> ctx) throws InterruptedException {
                Random random = new Random();
                int count = 0;
                while (isRunning) {
                    ctx.collect(String.valueOf(random.nextInt()));
                    count++;
                    if (count > 100){
                        isRunning = false;
                    }
                    Thread.sleep(10);
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });

        dataStream.print();
        env.execute("job");
    }

    @Test
    void getComponentProperties() {
        //获取 kafka 配置
        Properties kafkaProperties = environmentConfig.getComponentProperties(KafkaConsumerConfig.class);
        log.info("Kafka 配置信息：{}", JSON.toJSONString(kafkaProperties));
    }

    @Test
    void getComponent() {
        DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class);
        log.info("DataSource 配置信息：{}", JSON.toJSONString(dataSourceConfig));
    }

//    @Test
//    void get() {
//        log.info("application name：{}", environmentConfig.getParameterTool().get("flink.application.name"));
//    }
//
//    @Test
//    void getParameterTool() {
//        ParameterTool parameterTool = environmentConfig.getParameterTool();
//        boolean flag = parameterTool.has("flink.application.name");
//        log.info("ParameterTool 配置文件 {} `flink.application.name` 属性", flag ? "存在" : "不存在");
//    }
}