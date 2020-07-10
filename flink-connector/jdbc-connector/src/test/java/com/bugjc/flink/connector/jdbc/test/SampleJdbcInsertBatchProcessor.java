package com.bugjc.flink.connector.jdbc.test;

import com.bugjc.flink.connector.jdbc.JdbcInsertBatchProcessor;
import com.bugjc.flink.connector.jdbc.test.entity.JobEntity;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SampleJdbcInsertBatchProcessor implements JdbcInsertBatchProcessor, Serializable {
    @Override
    public String sql() {
        return "insert into odl_kafka_event_test(word, frequency) values(?, ?)";
    }

    @Override
    public void invoke(PreparedStatement preparedStatement, Object values) throws SQLException {
        JobEntity jobEntity = (JobEntity) values;
        preparedStatement.setString(1, jobEntity.getWord());
        preparedStatement.setInt(2, jobEntity.getFrequency());
        preparedStatement.addBatch();
    }
}
