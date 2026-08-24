package org.hzero.report.domain.service.impl;

import java.util.List;
import java.util.Map;

import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.domain.service.ITableReportService;
import org.hzero.report.infra.engine.ReportGenerator;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 表格报表生成服务类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月22日下午5:48:41
 */
@Service
public class TableReportServiceImpl implements ITableReportService {

    @Autowired
    private IReportMetaService reportMetaService;

    @Override
    public ReportTable getReportTable(long reportId, Map<String, Object> formParams) {
        Report report = this.reportMetaService.getReportById(reportId);
        return this.getReportTable(report, formParams);
    }

    @Override
    public ReportTable getSimpleReportTable(Report report, Map<String, Object> formParams) {
        ReportDataSource reportDataSource = this.reportMetaService.getReportDataSource(report.getTenantId(), report.getDatasourceCode());
        return ReportGenerator.generateSimple(reportDataSource, this.reportMetaService.createSimpleReportParameter(report, formParams));
    }

    @Override
    public ReportTable getReportTable(Report report, Map<String, Object> formParams) {
        ReportDataSource reportDataSource = this.reportMetaService.getReportDataSource(report.getTenantId(), report.getDatasourceCode());
        return ReportGenerator.generate(reportDataSource, this.reportMetaService.createReportParameter(report, formParams));
    }

    @Override
    public ReportTable getReportTable(Query queryer, ReportParameter reportParameter) {
        return ReportGenerator.generate(queryer, reportParameter);
    }

    @Override
    public ReportTable getReportTable(MetaDataSet metaDataSet, ReportParameter reportParameter) {
        return ReportGenerator.generate(metaDataSet, reportParameter);
    }

    @Override
    public List<MetaDataRow> getReportTableRows(Report report, Map<String, Object> formParams) {
        ReportDataSource reportDataSource = this.reportMetaService.getReportDataSource(report.getTenantId(), report.getDatasourceCode());
        return ReportGenerator.getMetaDataRows(reportDataSource, this.reportMetaService.createReportParameter(report, formParams));
    }

    @Override
    public ReportDataSet getReportDataSet(Report report, Map<String, Object> parameters) {
        ReportDataSource reportDs = this.reportMetaService.getReportDataSource(report.getTenantId(), report.getDatasourceCode());
        return ReportGenerator.getDataSet(reportDs, this.reportMetaService.createReportParameter(report, parameters));
    }
}
