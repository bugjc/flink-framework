package com.bugjc.flink.datasource.connection;

import com.bugjc.flink.datasource.DataSourceConfig;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * dbcp2 数据库连接池获取连接对象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public class BasicDataSourceConnection extends AbstractConnection {
    private final BasicDataSource dataSource;
    private final DataSourceConfig dataSourceConfig;

    public BasicDataSourceConnection(DataSource dataSource, DataSourceConfig dataSourceConfig) {
        this.dataSource = (BasicDataSource) dataSource;
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
        this.dataSource.setMaxTotal(this.dataSourceConfig.getMaxTotal());
        this.dataSource.setMinIdle(this.dataSourceConfig.getMinIdle());
        return this.dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void close() throws SQLException {
        this.dataSource.close();
    }
}
