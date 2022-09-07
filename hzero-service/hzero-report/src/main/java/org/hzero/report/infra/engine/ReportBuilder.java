package org.hzero.report.infra.engine;

import org.hzero.report.infra.engine.data.ReportTable;

/**
 * 报表构建接口
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:10:59
 */
public interface ReportBuilder {

    /**
     * 生成报表表头
     */
    void drawTableHeaderRows();

    /**
     * 生成报表表体中的每一行
     */
    void drawTableBodyRows();

    /**
     * 生成报表表尾
     */
    void drawTableFooterRows();

    /**
     * 获取生成的报表对象
     *
     * @return ReportTable
     */
    ReportTable getTable();
}
