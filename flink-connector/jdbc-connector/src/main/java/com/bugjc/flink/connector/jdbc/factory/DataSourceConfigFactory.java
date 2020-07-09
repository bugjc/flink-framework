package com.bugjc.flink.connector.jdbc.factory;

import com.bugjc.flink.connector.jdbc.DataSourceConfig;
import com.bugjc.flink.connector.jdbc.connection.BasicDataSource;
import com.bugjc.flink.connector.jdbc.connection.Dbcp2DataSource;
import com.bugjc.flink.connector.jdbc.connection.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源工厂内
 *
 * @author aoki
 * @date 2020/7/7
 **/
@Slf4j
public class DataSourceConfigFactory implements Serializable {

    private final Map<String, BasicDataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>(8);
    private BasicDataSource currentDataSource = null;
    private DataSourceConfig currentDataSourceConfig = null;

    /**
     * 获取指定 className 的连接池对象
     *
     * @param dataSourceConfig --数据源配置
     */
    public DataSourceConfigFactory createDataSource(DataSourceConfig dataSourceConfig) {
        if (dataSourceConfig.getClassName() == null) {
            throw new NullPointerException("`className` parameter not configured！");
        }

        // 如果已经存在已有数据源（连接池）直接返回
        BasicDataSource basicDataSource = this.DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        if (basicDataSource != null) {
            return this;
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

            this.DATA_SOURCE_MAP.put(dataSourceConfig.getClassName(), basicDataSource);
            this.currentDataSource = basicDataSource;
            this.currentDataSourceConfig = dataSourceConfig;

            return this;
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
     * @return
     * @throws SQLException
     */
    public Connection getConnection(DataSourceConfig dataSourceConfig) throws SQLException {
        BasicDataSource dataSource = this.DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        log.info("DataSource={}", dataSource.hashCode());
        return dataSource.getConnection();
    }

    /**
     * 获取当前数据源的一个连接对象
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        if (this.currentDataSource == null) {
            throw new NullPointerException("this database connection pool is not supported");
        }
        log.info("DataSource={}", this.currentDataSource.hashCode());
        return this.currentDataSource.getConnection();
    }


    /**
     * 关闭对应数据源连接池
     *
     * @param dataSourceConfig --数据源配置
     */
    public void close(DataSourceConfig dataSourceConfig) {
        DATA_SOURCE_MAP.remove(dataSourceConfig.getClassName());
    }

    /**
     * 关闭当前数据源连接池
     */
    public void close() throws SQLException {
        this.DATA_SOURCE_MAP.get(this.currentDataSourceConfig.getClassName()).close();
        this.DATA_SOURCE_MAP.remove(this.currentDataSourceConfig.getClassName());
        this.currentDataSource = null;
        this.currentDataSourceConfig = null;
    }

    /**
     * 销毁工厂类，关闭所有数据源
     */
    public void destroy() throws SQLException {
        for (String key : DATA_SOURCE_MAP.keySet()) {
            this.DATA_SOURCE_MAP.get(key).close();
        }
        this.DATA_SOURCE_MAP.clear();
    }
}
