package org.hzero.jdbc.query;

import org.apache.commons.lang3.StringUtils;
import org.hzero.jdbc.Query;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.hzero.jdbc.statement.SqlPageStatement;

/**
 * Oracle数据库查询器类。
 * 在使用该查询器时,请先参考:http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:01:09
 */
public class OracleQueryer extends AbstractQueryer implements Query {
    public OracleQueryer(DatasourceStatement dataSource) {
        super(dataSource);
    }

    @Override
    protected String prePageSqlText(String sqlText, SqlPageStatement pageStatement) {
        // 去掉SQL后面的结束符号";"
         sqlText = StringUtils.stripEnd(sqlText.trim(), ";");
        StringBuilder sqlBuilder = new StringBuilder(sqlText.length() + 120);
        // 处理分页逻辑
        if (pageStatement != null) {
            if (!pageStatement.isCount() && pageStatement.getMax() != null) {
                // 报表不分页，设置默认分页
                pageStatement.setSize(pageStatement.getMax());
                pageStatement.setEnd(pageStatement.getMax());
            }
            if (pageStatement.getBegin() > 0) {
                sqlBuilder.append("SELECT * FROM ( ");
            }
            if (pageStatement.getEnd() > 0) {
                sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM ( ");
            }
            sqlBuilder.append(sqlText);
            if (pageStatement.getEnd() > 0) {
                sqlBuilder.append(" ) TMP_PAGE WHERE ROWNUM <= ");
                sqlBuilder.append(pageStatement.getEnd());
            }
            if (pageStatement.getBegin() > 0) {
                sqlBuilder.append(" ) WHERE ROW_ID > ");
                sqlBuilder.append(pageStatement.getBegin());
            }
        }
        return sqlBuilder.toString();
    }

}
