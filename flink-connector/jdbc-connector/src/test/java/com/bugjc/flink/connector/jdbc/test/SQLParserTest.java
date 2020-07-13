package com.bugjc.flink.connector.jdbc.test;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class SQLParserTest {

    @Test
    void druidParser(){
        String sql = "insert ignore into odl_kafka_event_test(word, frequency) values(?, ?)";
        SQLStatementParser sqlStatementParser = new SQLCreateTableParser(sql);
        SQLStatement sqlStatement = sqlStatementParser.parseStatement();
        SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
        sqlStatement.accept(schemaStatVisitor);
        //获取操作方法名称,依赖于表名称
        log.info("Tables : " + schemaStatVisitor.getTables());
        //获取字段名称
        log.info("Columns : " + schemaStatVisitor.getColumns());
    }

    @Test
    void jSqlParser() throws JSQLParserException {
        String sql = "insert ignore into odl_kafka_event_test(word, frequency) values(?, ?)";
        Insert insert = (Insert) CCJSqlParserUtil.parse(sql);
        log.info("Table : " + insert.getTable());
        log.info("Columns : " + insert.getColumns());
        //Placeholder
        Insert insertExpand = (Insert)CCJSqlParserUtil.parse("INSERT IGNORE INTO Placeholder1 (Placeholder2) values (Placeholder3)");
        log.info("Insert SQL:{}",insertExpand.toString());

        insertExpand.getTable().setName("T");

        //adding a column
        insertExpand.getColumns().add(new Column("col2"));
        insertExpand.getColumns().remove(0);
        log.info("Insert SQL:{}",insertExpand.toString());

        //adding a value using a visitor
        ((ExpressionList)insertExpand.getItemsList()).getExpressions().remove(0);
        ((ExpressionList)insertExpand.getItemsList()).getExpressions().add(new HexValue("?"));
        log.info("Insert SQL:{}",insertExpand.toString());
    }

    public void enhanceSql(String sql) {
        // 解析
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        // 只考虑一条语句
        SQLStatement statement = statements.get(0);
        // 只考虑查询语句
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) statement;
        SQLSelectQuery sqlSelectQuery     = sqlSelectStatement.getSelect().getQuery();
        // 非union的查询语句
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
            // 获取字段列表
            List<SQLSelectItem> selectItems         = sqlSelectQueryBlock.getSelectList();
            selectItems.forEach(x -> {
                // 处理---------------------
            });
            // 获取表
            SQLTableSource table = sqlSelectQueryBlock.getFrom();
            // 普通单表
            if (table instanceof SQLExprTableSource) {
                // 处理---------------------
                // join多表
            } else if (table instanceof SQLJoinTableSource) {
                // 处理---------------------
                // 子查询作为表
            } else if (table instanceof SQLSubqueryTableSource) {
                // 处理---------------------
            }
            // 获取where条件
            SQLExpr where = sqlSelectQueryBlock.getWhere();
            // 如果是二元表达式
            if (where instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr   sqlBinaryOpExpr = (SQLBinaryOpExpr) where;
                SQLExpr           left            = sqlBinaryOpExpr.getLeft();
                SQLBinaryOperator operator        = sqlBinaryOpExpr.getOperator();
                SQLExpr           right           = sqlBinaryOpExpr.getRight();
                // 处理---------------------
                // 如果是子查询
            } else if (where instanceof SQLInSubQueryExpr) {
                SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) where;
                // 处理---------------------
            }
            // 获取分组
            SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
            // 处理---------------------
            // 获取排序
            SQLOrderBy orderBy = sqlSelectQueryBlock.getOrderBy();
            // 处理---------------------
            // 获取分页
            SQLLimit limit = sqlSelectQueryBlock.getLimit();
            // 处理---------------------
            // union的查询语句
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            // 处理---------------------
        }
    }
}
