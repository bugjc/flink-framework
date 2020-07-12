package com.bugjc.flink.connector.jdbc;


import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.bugjc.flink.connector.jdbc.annotation.TableField;
import com.bugjc.flink.connector.jdbc.annotation.TableName;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Preconditions;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据批量 sink 数据到 mysql
 *
 * @author aoki
 * @date 2020/7/1
 **/
@Slf4j
public class JdbcInsertBatchSink<T> extends RichSinkFunction<List<T>> implements Serializable {
    private PreparedStatement preparedStatement;
    private Connection connection;
    private final DataSourceConfig dataSourceConfig;
    private String sql;

    public JdbcInsertBatchSink(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = Preconditions.checkNotNull(dataSourceConfig);
    }

    public JdbcInsertBatchSink(DataSourceConfig dataSourceConfig, String sql) {
        this.dataSourceConfig = Preconditions.checkNotNull(dataSourceConfig);
        this.sql = sql;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //获取连接
        this.connection = this.dataSourceConfig.getConnection();
        //声明 SQL
        //this.preparedStatement = this.connection.prepareStatement(this.sql);
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
    public void invoke(List<T> values, Context context) throws SQLException {

        if (values.isEmpty()) {
            return;
        }

        if (StrUtil.isBlank(this.sql)) {
            this.sql = generateSQL(values.get(0));
        }

        log.info("SQL:{}", sql);
        this.preparedStatement = this.connection.prepareStatement(sql);
        if (preparedStatement == null) {
            return;
        }


        try {
            long startTime = System.currentTimeMillis();
            connection.setAutoCommit(false);

            //解析 SQL
            SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
            SQLStatement sqlStatement = sqlStatementParser.parseStatement();
            SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
            sqlStatement.accept(schemaStatVisitor);

            for (int i = 0; i < values.size(); i++) {

                autoParseSQL(schemaStatVisitor, values.get(i));

                if (i != 0 && i % this.dataSourceConfig.getBatchSize() == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                    connection.commit();
                    log.info("连接对象：{}，线程 ID：{} 成功了插入了 {} 行数据", this.connection, Thread.currentThread().getId(), this.dataSourceConfig.getBatchSize());
                }
            }

            int[] count = preparedStatement.executeBatch();
            connection.commit();
            //重新打开事物自动提交开关
            connection.setAutoCommit(true);
            preparedStatement.close();
            log.info("成功了插入了 {} 行数据", count.length);
            log.info("插入 {} 条数据的运行时间 = {}s", values.size(), (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception ex) {
            //ignore
            log.info("批量插入失败！错误信息：{}！详细错误信息栈：{}", ex.getMessage(), ex);
        }
    }

    /**
     * 自动解析 SQL
     *
     * @param value
     */
    private void autoParseSQL(SchemaStatVisitor schemaStatVisitor, T value) throws SQLException {

        Class<?> tClass = value.getClass();
        //获取数据库字段`键值对`
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            TableField tableField = field.getAnnotation(TableField.class);
            String fieldName = field.getName();
            if (tableField != null && StrUtil.isNotBlank(tableField.value())) {
                fieldName = tableField.value();
            }
            try {
                fieldMap.put(fieldName, field.get(value));
            } catch (IllegalAccessException e) {
                //ignore
                log.info("IllegalAccessException:{}", e.getMessage());
            }
        }

        //根据 SQL 设置对应的值
        int parameterIndex = 1;
        for (TableStat.Column column : schemaStatVisitor.getColumns()) {
            try {
                preparedStatement.setObject(parameterIndex, fieldMap.get(column.getName()));
                parameterIndex++;
            } catch (SQLException e) {
                //ignore
                log.info("SQLException:{}", e.getMessage());
            }
        }
        preparedStatement.addBatch();
    }


    /**
     * 生成 SQL
     *
     * @return
     */
    private String generateSQL(T entity) {
        Class<?> tClass = entity.getClass();

        //获取要插入数据的表
        TableName table = tClass.getAnnotation(TableName.class);
        String tableName = tClass.getName();
        if (table != null && StrUtil.isNotBlank(table.value())) {
            tableName = table.value();
        }

        //获取要插入数据的字段
        Field[] declaredFields = tClass.getDeclaredFields();
        StringBuilder fieldBuffer = new StringBuilder();
        StringBuilder fieldValueBuffer = new StringBuilder();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            TableField tableField = field.getAnnotation(TableField.class);
            String fieldName = field.getName();
            if (tableField != null && StrUtil.isNotBlank(tableField.value())) {
                fieldName = tableField.value();
            }
            fieldBuffer.append(fieldName).append(",");
            fieldValueBuffer.append("?,");
        }
        String insertFields = fieldBuffer.substring(0, fieldBuffer.length() - 1);
        String insertValueFields = fieldValueBuffer.substring(0, fieldValueBuffer.length() - 1);
        return "insert into " + tableName + "(" + insertFields + ")" + " values(" + insertValueFields + ")";
    }
}
