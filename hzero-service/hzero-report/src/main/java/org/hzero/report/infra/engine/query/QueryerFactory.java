package org.hzero.report.infra.engine.query;

import org.hzero.report.infra.engine.data.ReportDataSource;
import org.hzero.report.infra.engine.data.ReportParameter;

/**
 * 报表查询器工厂方法类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:43:40
 */
public class QueryerFactory extends org.hzero.jdbc.QueryFactory {

    private QueryerFactory() {
    }

    public static Query create(ReportDataSource dataSource) {
        return QueryerFactory.create(dataSource, null);
    }

    public static Query create(ReportDataSource dataSource, ReportParameter parameter) {
        if (dataSource != null) {
            return new JdbcQueryer(dataSource, parameter);
        } else {
            return new UrlQueryer(parameter);
        }
    }
}
