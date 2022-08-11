package com.bugjc.flink.config.test;


import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.ApplicationTest;
import com.bugjc.flink.config.test.component.DataSourceConfig;
import com.bugjc.flink.config.test.component.KafkaConsumerConfig;
import com.bugjc.flink.config.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.Random;

@Slf4j
@ApplicationTest
class EnvironmentConfigTest {

    /**
     * 构建环境配置文件对象
     */
    private static EnvironmentConfig environmentConfig;

    @BeforeAll
    static void setup() {
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
    void getKafkaConsumerConfig() {
        //获取 kafka 配置
        Properties kafkaProperties = environmentConfig.getComponentProperties(KafkaConsumerConfig.class);
        log.info("Kafka 配置信息：{}", GsonUtil.getInstance().getGson().toJson(kafkaProperties));
    }

    @Test
    void getDataSourceConfig() {
        DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class);
        log.info("DataSource 配置信息：{}", GsonUtil.getInstance().getGson().toJson(dataSourceConfig));
    }
}