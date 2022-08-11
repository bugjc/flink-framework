package com.bugjc.flink.connector.jdbc.connection;

import com.bugjc.flink.connector.jdbc.DataSourceConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Druid 数据库连接池获取连接对象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public class DruidDataSource implements BasicDataSource {

    private final com.alibaba.druid.pool.DruidDataSource dataSource;
    private final DataSourceConfig dataSourceConfig;

    public DruidDataSource(DataSource dataSource, DataSourceConfig dataSourceConfig) {
        this.dataSource = (com.alibaba.druid.pool.DruidDataSource) dataSource;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public Connection getConnection() throws SQLException {
        this.dataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        this.dataSource.setUrl(dataSourceConfig.getUrl());
        this.dataSource.setUsername(dataSourceConfig.getUsername());
        this.dataSource.setPassword(dataSourceConfig.getPassword());
        return this.dataSource.getConnection();
    }

    @Override
    public void close() {
        this.dataSource.close();
    }
}
