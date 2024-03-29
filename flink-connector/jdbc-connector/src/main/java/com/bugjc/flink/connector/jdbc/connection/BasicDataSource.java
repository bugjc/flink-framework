package com.bugjc.flink.connector.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接对象抽象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public interface BasicDataSource {

    /**
     * 获取一个数据库连接对象
     *
     * @return  连接对象
     * @throws SQLException     异常
     */
    Connection getConnection() throws SQLException;

    /**
     * 关闭对应数据源连接池
     *
     * @throws SQLException 异常
     */
    void close() throws SQLException;
}
