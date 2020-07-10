package com.bugjc.flink.connector.jdbc;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * 数据批量 sink 数据到 mysql
 *
 * @author aoki
 * @date 2020/7/1
 **/
@Slf4j
public class JdbcInsertBatch<IN> extends RichSinkFunction<IN> implements Serializable {
    private PreparedStatement preparedStatement;
    private Connection connection;
    private final DataSourceConfig dataSourceConfig;
    private final JdbcInsertBatchProcessor jdbcInsertBatchProcessor;

    public JdbcInsertBatch(DataSourceConfig dataSourceConfig, JdbcInsertBatchProcessor jdbcInsertBatchProcessor) {
        this.jdbcInsertBatchProcessor = jdbcInsertBatchProcessor;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //获取连接
        dataSourceConfig.init();
        this.connection = dataSourceConfig.getDataSourceConfigFactory().getConnection();
        //声明 SQL
        this.preparedStatement = this.connection.prepareStatement(jdbcInsertBatchProcessor.sql());
    }

    @Override
    public void close() throws Exception {
        super.close();
        //关闭连接和释放资源
        if (this.connection != null) {
            this.connection.close();
        }
        if (this.preparedStatement != null) {
            this.preparedStatement.close();
        }
    }

    /**
     * 每条数据的插入都要调用一次 invoke() 方法
     *
     * @param values
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(IN values, Context context) {
        if (preparedStatement == null) {
            return;
        }

        try {
            long startTime = System.currentTimeMillis();
            connection.setAutoCommit(false);

            List list = (List) values;
            for (int i = 0; i < list.size(); i++) {
                jdbcInsertBatchProcessor.invoke(this.preparedStatement, list.get(i));
                if (i != 0 && i % this.dataSourceConfig.getBatchSize() == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    log.info("连接对象：{}，线程 ID：{} 成功了插入了 {} 行数据", this.connection, Thread.currentThread().getId(), this.dataSourceConfig.getBatchSize());
                }
            }

            int[] count = preparedStatement.executeBatch();
            connection.commit();
            //重新打开事物自动提交开关
            connection.setAutoCommit(true);
            log.info("成功了插入了 {} 行数据", count.length);
            log.info("插入 {} 条数据的运行时间 = {}s", list.size(), (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception ex) {
            //ignore
            log.info("批量插入失败！错误信息：{}！详细错误信息栈：{}", ex.getMessage(), ex);
        }
    }
}
