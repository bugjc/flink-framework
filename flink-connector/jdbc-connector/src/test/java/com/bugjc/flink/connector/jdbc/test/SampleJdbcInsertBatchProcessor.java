package com.bugjc.flink.connector.jdbc.test;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.bugjc.flink.connector.jdbc.JdbcInsertBatchProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 备注：insert ignore ... 这种形式的 SQL 解析不了。
 * @param <T>
 */
@Slf4j
public class SampleJdbcInsertBatchProcessor<T> implements JdbcInsertBatchProcessor<T>, Serializable {

    @Override
    public String sql() {
        return "insert ignore into tbs_job(job_id, status) values(?, ?)";
    }

    @Override
    public void invoke(PreparedStatement preparedStatement, T value) throws SQLException {
        String sql = "insert into tbs_job(job_id, status) values(?, ?)";
        //String sql = "insert into odl_kafka_event_test(word, frequency) values(?, ?)";
        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);
        SQLStatement sqlStatement = sqlStatementParser.parseStatement();
        SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
        sqlStatement.accept(schemaStatVisitor);

        Collection<TableStat.Column> columns = schemaStatVisitor.getColumns();

        int parameterIndex = 1;
        for (TableStat.Column column : columns) {
            try {
                Field field = value.getClass().getDeclaredField(column.getName());
                field.setAccessible(true);
                preparedStatement.setObject(parameterIndex, field.get(value));
                parameterIndex++;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //ignore
                log.info("{}", e.getMessage());
            }
        }


//        JobEntity jobEntity = (JobEntity) values;
//        preparedStatement.setString(1, jobEntity.getJob_id());
//        preparedStatement.setInt(2, jobEntity.getStatus());
        preparedStatement.addBatch();
    }
}
