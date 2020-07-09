package com.bugjc.flink.connector.jdbc;

import com.bugjc.flink.config.AbstractConfig;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.connector.jdbc.factory.DataSourceConfigFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

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
public class DataSourceConfig extends AbstractConfig implements Serializable {

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
    private transient DataSourceConfigFactory dataSourceConfigFactory;

    @Override
    public synchronized void init() {
        //配置属性初始化的时候自动执行此方法
        this.dataSourceConfigFactory = new DataSourceConfigFactory().createDataSource(this);
    }
}
