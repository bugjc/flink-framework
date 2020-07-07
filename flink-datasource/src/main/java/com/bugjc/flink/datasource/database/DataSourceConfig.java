package com.bugjc.flink.datasource.database;

import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.datasource.database.factory.DataSourceFactory;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 数据源组件配置
 *
 * @author aoki
 * @date 2020/7/2
 **/
@Data
@Slf4j
@ConfigurationProperties(prefix = "flink.datasource.")
public class DataSourceConfig implements Serializable {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String className;
    private int initialSize;
    private int maxTotal;
    private int minIdle;

    /**
     * 数据源工厂对象
     */
    @Getter
    private static transient DataSourceFactory dataSourceFactory;

    public DataSourceConfig(){
        dataSourceFactory = new DataSourceFactory();
    }

    /**
     * 初始化数据源工厂
     */
    public synchronized void init() {
        dataSourceFactory.createDataSource(this);
    }
}
