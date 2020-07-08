package com.bugjc.flink.datasource.database.test;

import com.alibaba.fastjson.JSON;
import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.datasource.database.DataSourceConfig;
import com.bugjc.flink.datasource.database.factory.DataSourceConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class DataSourceConfigTest {
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
    void getComponent() throws SQLException {
        DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class);
        log.info("DataSource 配置信息：{}", JSON.toJSONString(dataSourceConfig));
        DataSourceConfigFactory dataSourceConfigFactory = dataSourceConfig.getDataSourceConfigFactory();
        log.info("{}", dataSourceConfigFactory.getConnection());
        dataSourceConfigFactory.createDataSource(dataSourceConfig);
        log.info("{}", dataSourceConfigFactory.getConnection());
        dataSourceConfigFactory.close();
        log.info("{}", dataSourceConfigFactory.getConnection());
    }
}
