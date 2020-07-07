package com.bugjc.flink.datasource.database.connection;

import com.bugjc.flink.datasource.database.DataSourceConfig;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;


/**
 * dbcp2 数据库连接池获取连接对象类
 *
 * @author aoki
 * @date 2020/7/7
 **/
public class Dbcp2DataSource extends AbstractBasicDataSource<BasicDataSource> {
    private final BasicDataSource dataSource;
    //数据库
    public Dbcp2DataSource(BasicDataSource dataSource, DataSourceConfig dataSourceConfig) {
        super(dataSource, dataSourceConfig);
        this.dataSource = dataSource;
    }

    @Override
    public void close() throws SQLException {
        this.dataSource.close();
    }
}
