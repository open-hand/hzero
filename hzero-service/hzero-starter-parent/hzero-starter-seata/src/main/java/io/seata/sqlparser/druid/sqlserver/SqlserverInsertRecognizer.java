package io.seata.sqlparser.druid.sqlserver;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import io.seata.sqlparser.SQLInsertRecognizer;
import io.seata.sqlparser.SQLParsingException;
import io.seata.sqlparser.SQLType;
import io.seata.sqlparser.struct.Null;
import io.seata.sqlparser.struct.SqlMethodExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/3/13 2:11 下午
 */
public class SqlserverInsertRecognizer extends BaseSqlserverRecognizer implements SQLInsertRecognizer {

    private SQLServerInsertStatement ast;

    public SqlserverInsertRecognizer(String originalSql, SQLStatement ast) {
        super(originalSql);
        this.ast = (SQLServerInsertStatement) ast;
    }

    @Override
    public SQLType getSQLType() {
        return SQLType.INSERT;
    }

    @Override
    public String getTableAlias() {
        return ast.getTableSource().getAlias();
    }

    @Override
    public String getTableName() {
        StringBuilder sb = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(sb) {

            @Override
            public boolean visit(SQLExprTableSource x) {
                printTableSourceExpr(x.getExpr());
                return false;
            }
        };
        visitor.visit(ast.getTableSource());
        return sb.toString();
    }

    @Override
    public List<String> getInsertColumns() {
        List<SQLExpr> columnSQLExprs = ast.getColumns();
        if (columnSQLExprs.isEmpty()) {
            // INSERT INTO ta VALUES (...), without fields clarified
            return null;
        }
        List<String> list = new ArrayList<>(columnSQLExprs.size());
        for (SQLExpr expr : columnSQLExprs) {
            if (expr instanceof SQLIdentifierExpr) {
                list.add(((SQLIdentifierExpr)expr).getName());
            } else {
                throw new SQLParsingException("Unknown SQLExpr: " + expr.getClass() + " " + expr);
            }
        }
        return list;
    }

    @Override
    public List<List<Object>> getInsertRows() {
        List<SQLInsertStatement.ValuesClause> valuesClauses = ast.getValuesList();
        List<List<Object>> rows = new ArrayList<>(valuesClauses.size());
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauses) {
            List<SQLExpr> exprs = valuesClause.getValues();
            List<Object> row = new ArrayList<>(exprs.size());
            rows.add(row);
            for (SQLExpr expr : valuesClause.getValues()) {
                if (expr instanceof SQLNullExpr) {
                    row.add(Null.get());
                } else if (expr instanceof SQLValuableExpr) {
                    row.add(((SQLValuableExpr)expr).getValue());
                } else if (expr instanceof SQLVariantRefExpr) {
                    row.add(((SQLVariantRefExpr)expr).getName());
                } else if (expr instanceof SQLMethodInvokeExpr) {
                    row.add(new SqlMethodExpr());
                } else {
                    throw new SQLParsingException("Unknown SQLExpr: " + expr.getClass() + " " + expr);
                }
            }
        }
        return rows;
    }
}
