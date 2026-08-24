package org.hzero.jdbc.query;

import org.apache.commons.lang3.StringUtils;
import org.hzero.jdbc.Query;
import org.hzero.jdbc.parser.SqlServerParser;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.hzero.jdbc.statement.SqlPageStatement;

/**
 * MS SQLServer 数据库查询器类。 在使用该查询器时,请先参考:https://msdn.microsoft.com/library/mt484311.aspx
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:43:40
 */
public class SqlServerQueryer extends AbstractQueryer implements Query {

    public SqlServerQueryer(DatasourceStatement dataSource) {
        super(dataSource);
    }

    protected SqlServerParser sqlServerParser = new SqlServerParser();
    // with(nolock)
    protected String withnolock = ", PAGEWITHNOLOCK";

    @Override
    protected String prePageSqlText(String sqlText, SqlPageStatement pageStatement) {
        // 去掉SQL后面的结束符号";"
         sqlText = StringUtils.stripEnd(sqlText.trim(), ";");
        // 处理分页逻辑
        if (pageStatement != null) {
            if (!pageStatement.isCount() && pageStatement.getMax() != null) {
                // 报表不分页，设置默认分页
                pageStatement.setSize(pageStatement.getMax());
                pageStatement.setEnd(pageStatement.getMax());
            }
            sqlText = sqlText.replaceAll("((?i)with\\s*\\(nolock\\))", withnolock);
            sqlText = sqlServerParser.convertToPageSql(sqlText, null, null);
            sqlText = sqlText.replaceAll(withnolock, " with(nolock)");
            sqlText = sqlText.replace(String.valueOf(Long.MIN_VALUE), String.valueOf(pageStatement.getBegin()));
            sqlText = sqlText.replace(String.valueOf(Long.MAX_VALUE), String.valueOf(pageStatement.getSize()));
        }
        return sqlText;
    }

    @Override
    protected String getSmartCountSql(String sqlText) {
        sqlText = sqlText.replaceAll("((?i)with\\s*\\(nolock\\))", withnolock);
        sqlText = parser.getSmartCountSql(sqlText);
        sqlText = sqlText.replaceAll(withnolock, " with(nolock)");
        return sqlText;
    }

}
