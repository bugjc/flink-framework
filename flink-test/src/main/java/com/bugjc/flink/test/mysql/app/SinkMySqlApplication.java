package com.bugjc.flink.test.mysql.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.connector.jdbc.DataSourceConfig;
import com.bugjc.flink.connector.kafka.KafkaConsumerConfig;
import com.bugjc.flink.test.mysql.app.model.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.curator4.org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序入口类
 *
 * @author aoki
 * @date 2020/7/14
 **/
@Slf4j
@Application
public class SinkMySqlApplication {

    public static void main(String[] args) throws Exception {

        //1.环境参数配置
        EnvironmentConfig environmentConfig = new EnvironmentConfig(args);
        final StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();

        //2.获取 kafka 消费者配置
        KafkaConsumerConfig kafkaConsumerConfig = environmentConfig.getComponent(KafkaConsumerConfig.class);
        FlinkKafkaConsumer011<KafkaEvent> consumer011 = kafkaConsumerConfig.createKafkaSource(KafkaEvent.class);
        SingleOutputStreamOperator<KafkaEvent> kafkaEventSource = env
                .addSource(consumer011)
                .setParallelism(2);

        //3.按时间窗口汇集数据
        SingleOutputStreamOperator<List<KafkaEvent>> kafkaEvent = kafkaEventSource.timeWindowAll(Time.seconds(2)).apply(new AllWindowFunction<KafkaEvent, List<KafkaEvent>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<KafkaEvent> values, Collector<List<KafkaEvent>> out) {
                ArrayList<KafkaEvent> kafkaEvents = Lists.newArrayList(values);
                if (kafkaEvents.size() > 0) {
                    out.collect(kafkaEvents);
                    log.info("2 秒内收集到 KafkaEvent 的数据条数是：" + kafkaEvents.size());
                }
            }
        });

        //4.sink to mysql
        kafkaEvent.addSink(environmentConfig.getComponent(DataSourceConfig.class).createJdbcInsertBatchSink());
        env.execute("xxx");
    }


}
