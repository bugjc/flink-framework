package com.bugjc.flink.test.config.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;

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

        //2.使用配置值作为数据源
        DataSource<String> dataSource = env.fromCollection(env.getConfig().getGlobalJobParameters().toMap().values());

        //3.打印
        dataSource.print();
    }
}
