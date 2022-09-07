/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hzero.starter.sqlparser.sql.builder.impl;

import org.hzero.starter.sqlparser.sql.SQLUtils;
import org.hzero.starter.sqlparser.sql.ast.SQLExpr;
import org.hzero.starter.sqlparser.sql.ast.SQLStatement;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLBinaryOperator;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLIdentifierExpr;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLDeleteStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLExprTableSource;
import org.hzero.starter.sqlparser.sql.builder.SQLDeleteBuilder;
import org.hzero.starter.sqlparser.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import org.hzero.starter.sqlparser.util.JdbcConstants;

import java.util.List;

public class SQLDeleteBuilderImpl implements SQLDeleteBuilder {

    private SQLDeleteStatement stmt;
    private String             dbType;

    public SQLDeleteBuilderImpl(String dbType){
        this.dbType = dbType;
    }
    
    public SQLDeleteBuilderImpl(String sql, String dbType){
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        }

        SQLDeleteStatement stmt = (SQLDeleteStatement) stmtList.get(0);
        this.stmt = stmt;
        this.dbType = dbType;
    }

    public SQLDeleteBuilderImpl(SQLDeleteStatement stmt, String dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    @Override
    public SQLDeleteBuilderImpl limit(int rowCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLDeleteBuilderImpl limit(int rowCount, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLDeleteBuilder from(String table) {
        return from(table, null);
    }

    @Override
    public SQLDeleteBuilder from(String table, String alias) {
        SQLDeleteStatement delete = getSQLDeleteStatement();
        SQLExprTableSource from = new SQLExprTableSource(new SQLIdentifierExpr(table), alias);
        delete.setTableSource(from);
        return this;
    }

    @Override
    public SQLDeleteBuilder where(String expr) {
        SQLDeleteStatement delete = getSQLDeleteStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        delete.setWhere(exprObj);

        return this;
    }

    @Override
    public SQLDeleteBuilder whereAnd(String expr) {
        SQLDeleteStatement delete = getSQLDeleteStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, exprObj, false, delete.getWhere());
        delete.setWhere(newCondition);

        return this;
    }

    @Override
    public SQLDeleteBuilder whereOr(String expr) {
        SQLDeleteStatement delete = getSQLDeleteStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, exprObj, false, delete.getWhere());
        delete.setWhere(newCondition);

        return this;
    }

    public SQLDeleteStatement getSQLDeleteStatement() {
        if (stmt == null) {
            stmt = createSQLDeleteStatement();
        }
        return stmt;
    }

    public SQLDeleteStatement createSQLDeleteStatement() {
        if (JdbcConstants.ORACLE.equals(dbType)) {
            return new OracleDeleteStatement();    
        }
        
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return new MySqlDeleteStatement();    
        }
        
        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return new PGDeleteStatement();    
        }
        
        return new SQLDeleteStatement();
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
