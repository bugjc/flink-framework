package com.bugjc.flink.connector.jdbc;


import cn.hutool.core.util.StrUtil;
import com.bugjc.flink.connector.jdbc.annotation.TableField;
import com.bugjc.flink.connector.jdbc.annotation.TableName;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Preconditions;

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
public class JdbcInsertBatchSink<T> implements SinkFunction<List<T>> {
    private final DataSourceConfig dataSourceConfig;

    public JdbcInsertBatchSink(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = Preconditions.checkNotNull(dataSourceConfig);
    }

    /**
     * 每条数据的插入都要调用一次 invoke() 方法
     *
     * @param values
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(List<T> values, Context context) throws Exception {
        if (values.isEmpty()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        //初识化 SQL
        Insert insert = initInsert(values.get(0));
        log.info("SQL:{}", insert.toString());

        //获取连接对象
        try (Connection connection = this.dataSourceConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insert.toString());) {

            if (preparedStatement == null) {
                return;
            }

            connection.setAutoCommit(false);
            for (int i = 0; i < values.size(); i++) {
                autoDeclareInsertedParameters(preparedStatement, insert, values.get(i));
                if (i != 0 && i % this.dataSourceConfig.getBatchSize() == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                    connection.commit();
                }
            }

            preparedStatement.executeBatch();
            connection.commit();
            //重新打开事物自动提交开关
            connection.setAutoCommit(true);
            log.info("插入 {} 条数据的运行时间 = {}s", values.size(), (System.currentTimeMillis() - startTime) / 1000);
        }
    }

    /**
     * 自动解析 SQL
     *
     * @param value
     */
    private void autoDeclareInsertedParameters(PreparedStatement preparedStatement, Insert insert, T value) throws SQLException {

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
                log.error("AutoParseSql method IllegalAccessException:{}", e.getMessage());
            }
        }

        //根据 SQL 设置对应的值
        int parameterIndex = 1;
        for (Column column : insert.getColumns()) {
            preparedStatement.setObject(parameterIndex, fieldMap.get(column.getName(false)));
            parameterIndex++;
        }
        preparedStatement.addBatch();
    }

    /**
     * 初识 `Insert` 对象
     *
     * @param t --实体对象
     * @return
     * @throws JSQLParserException
     */
    private Insert initInsert(T t) throws JSQLParserException {
        Insert insert;
        if (StrUtil.isBlank(this.dataSourceConfig.getSql())) {
            insert = this.generateSql(t);
        } else {
            insert = (Insert) CCJSqlParserUtil.parse(this.dataSourceConfig.getSql());
        }
        return insert;
    }

    /**
     * 生成 SQL 语句
     *
     * @author aoki
     * @date 2020/7/13
     **/
    private Insert generateSql(T t) throws JSQLParserException {
        //构建插入 SQL 模板
        Insert insertExpand = (Insert) CCJSqlParserUtil.parse("INSERT INTO Placeholder1 (Placeholder2) values (Placeholder3)");
        Class<?> tClass = t.getClass();
        //获取要插入数据的表
        TableName table = tClass.getAnnotation(TableName.class);
        String tableName = tClass.getName();
        if (table != null && StrUtil.isNotBlank(table.value())) {
            tableName = table.value();
        }

        //获取要插入数据的字段
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            TableField tableField = field.getAnnotation(TableField.class);
            String fieldName = field.getName();
            if (tableField != null && StrUtil.isNotBlank(tableField.value())) {
                fieldName = tableField.value();
            }
            //添加一个字段
            insertExpand.getColumns().add(new Column(fieldName));
            //添加一个占位符
            ((ExpressionList) insertExpand.getItemsList()).getExpressions().add(new HexValue("?"));
        }

        //替换数据表名
        insertExpand.getTable().setName(tableName);
        //删除占位符
        insertExpand.getColumns().remove(0);
        ((ExpressionList) insertExpand.getItemsList()).getExpressions().remove(0);
        return insertExpand;
    }
}
