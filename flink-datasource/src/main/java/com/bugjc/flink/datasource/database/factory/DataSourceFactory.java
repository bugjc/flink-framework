package com.bugjc.flink.datasource.database.factory;

import com.bugjc.flink.datasource.database.DataSourceConfig;
import com.bugjc.flink.datasource.database.connection.Dbcp2DataSource;
import com.bugjc.flink.datasource.database.connection.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

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
public class DataSourceFactory implements Serializable {

    private final Map<String, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>(8);
    private DataSource currentDataSource = null;
    private DataSourceConfig currentDataSourceConfig = null;

    /**
     * 获取指定 className 的连接池对象
     *
     * @param dataSourceConfig --数据源配置
     */
    public DataSourceFactory createDataSource(DataSourceConfig dataSourceConfig) {
        if (dataSourceConfig.getClassName() == null) {
            throw new NullPointerException("`className` parameter not configured！");
        }

        // 如果已经存在已有数据源（连接池）直接返回
        final DataSource existedDataSource = DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        if (existedDataSource != null) {
            return this;
        }

        try {
            DataSource dataSource = (DataSource) Class.forName(dataSourceConfig.getClassName()).newInstance();
            if (dataSource instanceof BasicDataSource) {
                dataSource = new Dbcp2DataSource((BasicDataSource) dataSource, dataSourceConfig).getDataSource();
            } else if (dataSource instanceof com.alibaba.druid.pool.DruidDataSource) {
                dataSource = new DruidDataSource((com.alibaba.druid.pool.DruidDataSource) dataSource, dataSourceConfig).getDataSource();
            }

            DATA_SOURCE_MAP.put(dataSourceConfig.getClassName(), dataSource);
            this.currentDataSource = dataSource;
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
        DataSource dataSource = DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
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
        log.info("DataSource={}", currentDataSource.hashCode());
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
    public void close() {
        DATA_SOURCE_MAP.remove(this.currentDataSourceConfig.getClassName());
    }

    /**
     * 销毁工厂类，关闭所有数据源
     */
    public void destroy() {
        DATA_SOURCE_MAP.clear();
    }
}
