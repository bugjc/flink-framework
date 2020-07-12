package com.bugjc.flink.connector.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcInsertBatchProcessor<T> {
    String sql();

    void invoke(PreparedStatement preparedStatement, T value) throws SQLException;
}
