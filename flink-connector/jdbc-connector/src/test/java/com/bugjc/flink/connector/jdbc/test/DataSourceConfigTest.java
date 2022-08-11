package com.bugjc.flink.connector.jdbc.test;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.ApplicationTest;
import com.bugjc.flink.connector.jdbc.DataSourceConfig;
import com.bugjc.flink.connector.jdbc.test.entity.JobEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.curator5.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@ApplicationTest
class DataSourceConfigTest {
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
    void getDataSourceConfig() {
        Gson gson =  new GsonBuilder().disableHtmlEscaping().create();
        DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class);
        log.info("getDataSourceConfigFactory：{}", dataSourceConfig.getDataSource());
        String dataSourceConfigJson = gson.toJson(dataSourceConfig);
        log.info("DataSource 配置信息：{}", dataSourceConfigJson);
        dataSourceConfig = gson.fromJson(dataSourceConfigJson, DataSourceConfig.class);
        log.info("getDataSourceConfigFactory：{}", dataSourceConfig.getDataSource());
    }

    @Test
    void execJdbcInsertBatchJob() throws Exception {
        //构建环境 和 source
        StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();
        DataStreamSource<JobEntity> dataStreamSource = env.addSource(new RichParallelSourceFunction<JobEntity>() {

            @Override
            public void run(SourceContext<JobEntity> ctx) throws Exception {
                for (int i = 0; i < 100; i++) {
                    JobEntity jobEntity = new JobEntity("aoki" + i, i, new Date());
                    ctx.collect(jobEntity);
                    Thread.sleep(200);
                }
            }

            @Override
            public void cancel() {

            }

        }).setParallelism(1);

        //时间窗口内汇集数据
        SingleOutputStreamOperator<List<JobEntity>> streamOperator = dataStreamSource.timeWindowAll(Time.seconds(2)).apply(new AllWindowFunction<JobEntity, List<JobEntity>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<JobEntity> values, Collector<List<JobEntity>> out) {
                ArrayList<JobEntity> kafkaEvents = Lists.newArrayList(values);
                if (kafkaEvents.size() > 0) {
                    out.collect(kafkaEvents);
                    log.info("2 秒内收集到 JobEntity 的数据条数是：" + kafkaEvents.size());
                }
            }

        });

        //sink
        DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class);
        String sql = "insert ignore into tbs_job(job_id, status) values(?, ?)";
        streamOperator.addSink(dataSourceConfig.createJdbcInsertBatchSink(sql));
        env.execute("test");
    }


}