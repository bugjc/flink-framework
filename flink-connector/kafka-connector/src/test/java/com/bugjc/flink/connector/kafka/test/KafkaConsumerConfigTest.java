package com.bugjc.flink.connector.kafka.test;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.connector.kafka.KafkaConsumerConfig;
import com.bugjc.flink.connector.kafka.test.event.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class KafkaConsumerConfigTest {
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
    void getKafkaConsumerConfig() throws Exception {
        StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();
        KafkaConsumerConfig kafkaConsumerConfig = environmentConfig.getComponent(KafkaConsumerConfig.class);
        log.info("KafkaConsumerConfig 配置信息：{}", kafkaConsumerConfig);
        FlinkKafkaConsumer011<KafkaEvent> consumer011 = kafkaConsumerConfig.getKafkaConsumer(KafkaEvent.class);
        log.info("{}", consumer011);

        SingleOutputStreamOperator<KafkaEvent> kafkaEventSource = env
                .addSource(consumer011)
                .setParallelism(2);

        kafkaEventSource.print();

        env.execute("111");
    }
}