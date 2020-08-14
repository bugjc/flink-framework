package com.bugjc.flink.test.config.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.config.util.ClassUtil;
import com.bugjc.flink.test.config.app.config.TestComponentConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.ExecutionEnvironment;

/**
 * 程序入口
 *
 * @author aoki
 * @date 2020/7/15
 **/
@Slf4j
@Application
public class ConfigApplication {

    public static void main(String[] args) throws Exception {
        //1.环境参数配置
        EnvironmentConfig environmentConfig = new EnvironmentConfig(args);
        final ExecutionEnvironment env = environmentConfig.getExecutionEnvironment();
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

        //2.使用配置值作为数据源
        //DataSource<String> dataSource = env.fromCollection(env.getConfig().getGlobalJobParameters().toMap().values());

        //3.打印所有参数
        //dataSource.print();


        //4.测试组件参数自动配置
        TestComponentConfig testComponentConfig = environmentConfig.getComponent(TestComponentConfig.class);
        ClassUtil.print(testComponentConfig);

    }
}
