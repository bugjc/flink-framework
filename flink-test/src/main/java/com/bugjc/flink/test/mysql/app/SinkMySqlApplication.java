package com.bugjc.flink.test.mysql.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.connector.jdbc.DataSourceConfig;
import com.bugjc.flink.connector.kafka.KafkaConsumerConfig;
import com.bugjc.flink.test.mysql.app.model.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.shaded.curator4.org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
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
        //闭包清理器
        env.getConfig().setClosureCleanerLevel(ExecutionConfig.ClosureCleanerLevel.TOP_LEVEL);
        //设置作业的默认并行度为
        env.setParallelism(2);
        //设置作业的默认最大并行度
        env.getConfig().setMaxParallelism(4);
        //失败重试，每次重试时间间隔 1000ms ,总共重试 5 次
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 1000));
        //设置执行模式,默认执行模式是 PIPELINED
        env.getConfig().setExecutionMode(ExecutionMode.PIPELINED);

        //--------checkpoint------
        // 每 1000ms 开始一次 checkpoint
        env.enableCheckpointing(1000);
        // 设置模式为精确一次 (这是默认值)
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 确认 checkpoints 之间的时间会进行 500 ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // Checkpoint 必须在一分钟内完成，否则就会被抛弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 同一时间只允许一个 checkpoint 进行
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 开启在 job 中止后仍然保留的 externalized checkpoints
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 允许在有更近 savepoint 时回退到 checkpoint
        env.getCheckpointConfig().setPreferCheckpointForRecovery(true);

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
