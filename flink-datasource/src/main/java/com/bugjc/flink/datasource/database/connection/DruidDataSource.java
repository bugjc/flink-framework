package com.bugjc.flink.datasource.database.connection;

import com.bugjc.flink.datasource.database.DataSourceConfig;

import java.sql.SQLException;

/**
 * Druid 数据库连接池获取连接对象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public class DruidDataSource extends AbstractBasicDataSource<com.alibaba.druid.pool.DruidDataSource> {
    private final com.alibaba.druid.pool.DruidDataSource dataSource;

    public DruidDataSource(com.alibaba.druid.pool.DruidDataSource dataSource, DataSourceConfig dataSourceConfig) {
        super(dataSource, dataSourceConfig);
        this.dataSource = dataSource;
    }

    @Override
    public void close() throws SQLException {
        this.dataSource.close();
    }
}
