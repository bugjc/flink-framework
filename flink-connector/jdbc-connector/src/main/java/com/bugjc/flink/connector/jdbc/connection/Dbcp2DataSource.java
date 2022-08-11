package com.bugjc.flink.connector.jdbc.connection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.bugjc.flink.connector.jdbc.DataSourceConfig;
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
public class Dbcp2DataSource implements com.bugjc.flink.connector.jdbc.connection.BasicDataSource {
    private final BasicDataSource dataSource;

    public Dbcp2DataSource(DataSource dataSource, DataSourceConfig dataSourceConfig) {
        this.dataSource = (BasicDataSource) dataSource;
        CopyOptions copyOptions = CopyOptions.create().ignoreCase().ignoreNullValue();
        BeanUtil.copyProperties(dataSourceConfig, this.dataSource, copyOptions);
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
