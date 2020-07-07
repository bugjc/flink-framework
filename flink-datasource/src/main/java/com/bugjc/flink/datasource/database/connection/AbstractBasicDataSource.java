package com.bugjc.flink.datasource.database.connection;

import cn.hutool.core.bean.BeanUtil;
import com.bugjc.flink.datasource.database.DataSourceConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractBasicDataSource<T> implements BasicDataSource {

    private final T dataSource;
    private final DataSourceConfig dataSourceConfig;

    public AbstractBasicDataSource(T dataSource, DataSourceConfig dataSourceConfig) {
        this.dataSource = dataSource;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public DataSource getDataSource() {
        BeanUtil.copyProperties(this.dataSourceConfig, this.dataSource);
        return (DataSource) this.dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ((DataSource)this.dataSource).getConnection();
    }

    @Override
    public void close() throws SQLException {
        //ignore
    }
}
