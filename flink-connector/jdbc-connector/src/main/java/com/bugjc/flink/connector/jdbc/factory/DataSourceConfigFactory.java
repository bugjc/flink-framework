package com.bugjc.flink.connector.jdbc.factory;

import com.bugjc.flink.connector.jdbc.DataSourceConfig;
import com.bugjc.flink.connector.jdbc.connection.BasicDataSource;
import com.bugjc.flink.connector.jdbc.connection.Dbcp2DataSource;
import com.bugjc.flink.connector.jdbc.connection.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源工厂
 *
 * @author aoki
 * @date 2020/7/7
 **/
@Slf4j
public class DataSourceConfigFactory {

    /**
     * 数据源，当前只允许没类数据库连接池只能创建一个数据源
     */
    private static final Map<String, BasicDataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>(8);

    /**
     * 获取指定 className 的连接池对象
     *
     * @param dataSourceConfig --数据源配置
     */
    public static synchronized BasicDataSource createDataSource(DataSourceConfig dataSourceConfig) {

        if (dataSourceConfig.getClassName() == null) {
            throw new NullPointerException("`className` parameter not configured！");
        }

        // 如果已经存在已有数据源（连接池）直接返回
        BasicDataSource basicDataSource = DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        if (basicDataSource != null) {
            return basicDataSource;
        }

        try {
            DataSource dataSource = (DataSource) Class.forName(dataSourceConfig.getClassName()).newInstance();
            if (dataSource instanceof org.apache.commons.dbcp2.BasicDataSource) {
                basicDataSource = new Dbcp2DataSource(dataSource, dataSourceConfig);
            } else if (dataSource instanceof com.alibaba.druid.pool.DruidDataSource) {
                basicDataSource = new DruidDataSource(dataSource, dataSourceConfig);
            } else {
                throw new NullPointerException("this database connection pool is not supported");
            }

            DATA_SOURCE_MAP.put(dataSourceConfig.getClassName(), basicDataSource);
            return basicDataSource;
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            log.error("error message of instantiated data source：{}", e.getMessage());
        } catch (NoClassDefFoundError e) {
            //ignore
            log.error("no data source dependent packages");
        }
        throw new NullPointerException("this database connection pool is not supported");
    }


    /**
     * 获取指定配置文件的一个数据源连接对象
     *
     * @param dataSourceConfig --数据源配置
     * @return  连接对象
     * @throws SQLException     异常
     */
    public static Connection getConnection(DataSourceConfig dataSourceConfig) throws SQLException {
        BasicDataSource dataSource = DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        if (dataSource == null) {
            return DataSourceConfigFactory.createDataSource(dataSourceConfig).getConnection();
        }
        return dataSource.getConnection();
    }


    /**
     * 关闭对应数据源连接池
     *
     * @param dataSourceConfig --数据源配置
     */
    public static void close(DataSourceConfig dataSourceConfig) throws SQLException {
        DATA_SOURCE_MAP.get(dataSourceConfig.getClassName()).close();
        DATA_SOURCE_MAP.remove(dataSourceConfig.getClassName());
    }

    /**
     * 销毁工厂类，关闭所有数据源
     */
    public static void destroy() throws SQLException {
        for (String key : DATA_SOURCE_MAP.keySet()) {
            DATA_SOURCE_MAP.get(key).close();
        }
        DATA_SOURCE_MAP.clear();
    }
}
