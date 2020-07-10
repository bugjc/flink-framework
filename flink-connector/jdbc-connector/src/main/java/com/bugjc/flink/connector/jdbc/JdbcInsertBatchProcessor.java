package com.bugjc.flink.connector.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcInsertBatchProcessor {
    String sql();
    void invoke(PreparedStatement preparedStatement, Object values) throws SQLException;
}
