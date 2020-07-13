package com.bugjc.flink.connector.jdbc;

import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.connector.jdbc.connection.BasicDataSource;
import com.bugjc.flink.connector.jdbc.factory.DataSourceConfigFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源组件配置
 *
 * @author aoki
 * @date 2020/7/2
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@ConfigurationProperties(prefix = "flink.datasource.")
public class DataSourceConfig extends AbstractDataSourceConfig implements Serializable {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String className;
    private int initialSize;
    private int maxTotal;
    private int minIdle;

    /**
     * 每次批量插入的大小（可选），默认：5000
     */
    private int batchSize = 5000;

    /**
     * （临时）当前将要执行的 SQL
     */
    private String sql;

    /**
     * 获取数据源对象
     *
     * @return
     */
    @Override
    public BasicDataSource getDataSource() {
        return DataSourceConfigFactory.createDataSource(this);
    }

    /**
     * 获取连接对象
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceConfigFactory.getConnection(this);
    }

    /**
     * 关闭数据源对象
     */
    @Override
    public void close() throws SQLException {
        DataSourceConfigFactory.close(this);
    }

    /**
     * 获取一个批量插入数据到数据库的 sink 函数
     *
     * @param <T> --数据库实体对象
     * @return
     */
    public <T> JdbcInsertBatchSink<T> createJdbcInsertBatchSink() {
        return new JdbcInsertBatchSink<T>(this);
    }

    /**
     * 获取一个批量插入数据到数据库的 sink 函数
     *
     * @param sql --批量插入数据 SQL
     * @param <T> --数据库实体对象
     * @return
     */
    public <T> JdbcInsertBatchSink<T> createJdbcInsertBatchSink(String sql) {
        this.setSql(sql);
        return createJdbcInsertBatchSink();
    }
}
