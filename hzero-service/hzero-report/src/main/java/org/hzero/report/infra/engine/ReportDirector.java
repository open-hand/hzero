package org.hzero.report.infra.engine;

/**
 * 报表组装器
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:45:18
 */
class ReportDirector {
    private ReportBuilder builder;

    ReportDirector(ReportBuilder builder) {
        this.builder = builder;
    }

    void build() {
        this.builder.drawTableHeaderRows();
        this.builder.drawTableBodyRows();
        this.builder.drawTableFooterRows();
    }
}
