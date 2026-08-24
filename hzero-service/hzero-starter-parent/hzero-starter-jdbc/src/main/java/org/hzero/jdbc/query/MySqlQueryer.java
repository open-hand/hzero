package org.hzero.jdbc.query;

import org.apache.commons.lang3.StringUtils;
import org.hzero.jdbc.Query;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.hzero.jdbc.statement.SqlPageStatement;

/**
 * MySQL数据库查询器类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:01:09
 */
public class MySqlQueryer extends AbstractQueryer implements Query {

    public MySqlQueryer(DatasourceStatement dataSource) {
        super(dataSource);
    }

    @Override
    protected String prePageSqlText(String sqlText, SqlPageStatement pageStatement) {
        // 去掉SQL后面的结束符号";"
        sqlText = StringUtils.stripEnd(sqlText.trim(), ";");
        StringBuilder sqlBuilder = new StringBuilder(sqlText.length() + 14);
        sqlBuilder.append(sqlText);
        // 处理分页逻辑
        if (pageStatement != null) {
            if (!pageStatement.isCount() && pageStatement.getMax() != null) {
                // 报表不分页，设置默认分页
                pageStatement.setSize(pageStatement.getMax());
                pageStatement.setEnd(pageStatement.getMax());
            }
            // 如设置了分页，则进行分页处理
            if (pageStatement.getBegin() == 0) {
                sqlBuilder.append(" LIMIT ");
                sqlBuilder.append(pageStatement.getSize());
            } else {
                sqlBuilder.append(" LIMIT ");
                sqlBuilder.append(pageStatement.getBegin());
                sqlBuilder.append(",");
                sqlBuilder.append(pageStatement.getSize());
            }
        }

        return sqlBuilder.toString();
    }

}
