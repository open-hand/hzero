package org.hzero.jdbc.query;

import org.hzero.jdbc.Query;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.hzero.jdbc.statement.SqlPageStatement;

/**
 * MySQL数据库查询器类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:01:09
 */
public class HiveQueryer extends AbstractQueryer implements Query {
    private static final String PAGE_SQL = "SELECT t.* FROM (SELECT TMP_PAGE.*, ROW_NUMBER() OVER() ROW_ID FROM (%s) TMP_PAGE) t WHERE t.ROW_ID BETWEEN %d AND %d";

    public HiveQueryer(DatasourceStatement dataSource) {
        super(dataSource);
    }

    @Override
    protected String prePageSqlText(String sqlText, SqlPageStatement pageStatement) {
        return String.format(PAGE_SQL, sqlText, pageStatement.getBegin() + 1, pageStatement.getEnd());
    }

}
