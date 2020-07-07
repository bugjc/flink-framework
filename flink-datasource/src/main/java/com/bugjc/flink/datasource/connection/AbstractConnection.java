package com.bugjc.flink.datasource.connection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接对象抽象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public abstract class AbstractConnection {

    /**
     * 获取数据连接池
     *
     * @return
     */
    public abstract DataSource getDataSource();

    /**
     * 获取一个数据库连接对象
     *
     * @return
     * @throws SQLException
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * 关闭对应数据源连接池
     * @throws SQLException
     */
    public abstract void close() throws SQLException;
}
