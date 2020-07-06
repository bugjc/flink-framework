package com.bugjc.flink.config.test.component;

import com.bugjc.flink.config.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源属性配置
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
    private int initialSize;
    private int maxTotal;
    private int minIdle;

    /**
     * 获取数据连接对象
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection(BasicDataSource basicDataSource) throws SQLException {
        //dbcp2
        basicDataSource.setDriverClassName(this.getDriverClassName());
        basicDataSource.setUrl(this.getUrl());
        basicDataSource.setUsername(this.getUsername());
        basicDataSource.setPassword(this.getPassword());
        //设置连接池的一些参数
        basicDataSource.setInitialSize(this.getInitialSize());
        basicDataSource.setMaxTotal(this.maxTotal);
        basicDataSource.setMinIdle(this.minIdle);


        try {
            Connection con = basicDataSource.getConnection();
            log.info("BasicDataSource:{},创建连接池：{}", basicDataSource, con);
            return con;
        } catch (SQLException sqlException) {
            log.error("mysql get connection has exception , msg = {} \n {}", sqlException.getMessage(), sqlException.getStackTrace());
            throw sqlException;
        }
    }
}
