package com.bugjc.flink.datasource.factory;

import com.alibaba.druid.pool.DruidDataSource;
import com.bugjc.flink.datasource.DataSourceConfig;
import com.bugjc.flink.datasource.connection.BasicDataSourceConnection;
import com.bugjc.flink.datasource.connection.DruidDataSourceConnection;
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

    /**
     * 获取指定 className 的连接池对象
     *
     * @param dataSourceConfig --数据源配置
     */
    public void createDataSource(DataSourceConfig dataSourceConfig) {
        if (dataSourceConfig.getClassName() == null) {
            throw new NullPointerException("`className` parameter not configured！");
        }

        // 如果已经存在已有数据源（连接池）直接返回
        final DataSource existedDataSource = DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        if (existedDataSource != null) {
            return;
        }

        try {
            DataSource dataSource = (DataSource) Class.forName(dataSourceConfig.getClassName()).newInstance();
            if (dataSource instanceof BasicDataSource) {
                dataSource = new BasicDataSourceConnection(dataSource, dataSourceConfig).getDataSource();
            } else if (dataSource instanceof DruidDataSource) {
                dataSource = new DruidDataSourceConnection(dataSource, dataSourceConfig).getDataSource();
            }
            DATA_SOURCE_MAP.put(dataSourceConfig.getClassName(), dataSource);
            return;
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            log.error("error message of instantiated data source：{}", e.getMessage());
        } catch (NoClassDefFoundError e) {
            //ignore
            log.error("no data source dependent packages");
        }
        throw new NullPointerException("this database connection pool is not supported");
    }


    /**
     * 获取一个数据源连接池连接
     *
     * @param dataSourceConfig
     * @return
     * @throws SQLException
     */
    public Connection getConnection(DataSourceConfig dataSourceConfig) throws SQLException {
        DataSource dataSource = DATA_SOURCE_MAP.get(dataSourceConfig.getClassName());
        log.info("DataSource={}", dataSource.hashCode());
        if (dataSource instanceof BasicDataSource) {
            return new BasicDataSourceConnection(dataSource, dataSourceConfig).getConnection();
        } else if (dataSource instanceof DruidDataSource) {
            return new DruidDataSourceConnection(dataSource, dataSourceConfig).getConnection();
        }

        throw new NullPointerException("this database connection pool is not supported");
    }


    /**
     * 关闭对应数据源连接池
     *
     * @param className 数据源连接池
     */
    public void close(String className) {
        DATA_SOURCE_MAP.remove(className);
    }

    /**
     * 销毁工厂类，关闭所有数据源
     */
    public void destroy() {
        DATA_SOURCE_MAP.clear();
    }
}
