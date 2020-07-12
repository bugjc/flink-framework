package com.bugjc.flink.connector.jdbc;

import com.bugjc.flink.config.Config;
import com.bugjc.flink.connector.jdbc.connection.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 组件配置文件自动初始化标记类。
 * 此类会在组件的配置文件初始化后执行
 * 注意：仅支持 @ConfigurationProperties 注解标记的类
 *
 * @author aoki
 * @date 2020/7/8
 **/
public abstract class AbstractDataSourceConfig implements Config {

    /**
     * 获取数据源对象
     * @return
     */
    public abstract BasicDataSource getDataSource();

    /**
     * 获取连接对象
     * @return
     * @throws SQLException
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * 关闭数据源对象
     */
    public abstract void close() throws SQLException;

}
