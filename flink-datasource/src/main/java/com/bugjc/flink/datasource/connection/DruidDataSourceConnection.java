package com.bugjc.flink.datasource.connection;

import com.alibaba.druid.pool.DruidDataSource;
import com.bugjc.flink.datasource.DataSourceConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Druid 数据库连接池获取连接对象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public class DruidDataSourceConnection extends AbstractConnection {
    private final DruidDataSource dataSource;
    private final DataSourceConfig dataSourceConfig;

    public DruidDataSourceConnection(DataSource dataSource, DataSourceConfig dataSourceConfig) {
        this.dataSource = (DruidDataSource) dataSource;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public DataSource getDataSource() {
        this.dataSource.setDriverClassName(this.dataSourceConfig.getDriverClassName());
        this.dataSource.setUrl(this.dataSourceConfig.getUrl());
        this.dataSource.setUsername(this.dataSourceConfig.getUsername());
        this.dataSource.setPassword(this.dataSourceConfig.getPassword());
        //设置连接池的一些参数
        this.dataSource.setInitialSize(this.dataSourceConfig.getInitialSize());
        this.dataSource.setMinIdle(this.dataSourceConfig.getMinIdle());
        return this.dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() throws SQLException {
        this.dataSource.close();
    }
}
