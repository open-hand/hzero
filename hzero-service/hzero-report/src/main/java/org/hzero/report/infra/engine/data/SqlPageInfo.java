package org.hzero.report.infra.engine.data;

import org.hzero.jdbc.statement.SqlPageStatement;
import org.hzero.report.infra.config.ReportConfig;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * SQL分页参数信息
 *
 * @author xianzhi.chen@hand-china.com 2018年12月17日下午6:56:05
 */
public class SqlPageInfo extends SqlPageStatement {
    public SqlPageInfo() {
    }

    public SqlPageInfo(int page, int size, boolean count) {
        super(ApplicationContextHelper.getContext().getBean(ReportConfig.class).getMaxRows(), page, size, count);
    }
}
