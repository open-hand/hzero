package org.hzero.report.infra.repository.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.infra.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表信息 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@Component
public class ReportRepositoryImpl extends BaseRepositoryImpl<Report> implements ReportRepository {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public Page<Report> selectReportDesigners(PageRequest pageRequest, Report report) {
        return PageHelper.doPageAndSort(pageRequest, () -> reportMapper.selectReportDesigners(report));
    }

    @Override
    public Report selectReportById(Long reportId, Long tenantId) {
        return reportMapper.selectReportById(reportId, tenantId, LocalDate.now());
    }

    @Override
    public Page<Report> selectTenantReportDesigners(PageRequest pageRequest, String reportCode, String reportName, String reportTypeCode, Long tenantId, LocalDate nowDate) {
        return PageHelper.doPage(pageRequest, () -> reportMapper.selectTenantReportDesigners(reportCode, reportName, reportTypeCode, tenantId, nowDate));
    }

    @Override
    public Report selectReportDesigner(Long datasetId, Long tenantId) {
        Report report = reportMapper.selectReportDesigner(datasetId, tenantId);
        if (report != null && StringUtils.isNotBlank(report.getExportType())) {
            report.setExportTypeList(Arrays.asList(report.getExportType().split(BaseConstants.Symbol.COMMA)));
        }
        return report;
    }

    @Override
    public Page<Report> selectReports(PageRequest pageRequest, Report report, List<Long> roleIds, Long tenantId) {
        return PageHelper.doPage(pageRequest, () -> reportMapper.selectReports(roleIds, tenantId, report, LocalDateTime.now().toLocalDate()));
    }

    @Override
    public Report selectReport(String reportKey, Long tenantId) {
        List<Long> roleIds = DetailsHelper.getUserDetails().roleMergeIds();
        LocalDate localDate = LocalDateTime.now().toLocalDate();
        // 匹配报表UUID
        Report report = reportMapper.selectReport(reportKey, null, tenantId, null, roleIds, localDate);
        if (report == null) {
            // 匹配报表编码
            report = reportMapper.selectReport(null, reportKey, tenantId, tenantId, roleIds, localDate);
        }
        return report;
    }

    @Override
    public Report selectReportIgnorePermission(Long tenantId, String reportKey) {
        // 匹配UUID
        Report report = reportMapper.selectReportIgnorePermission(reportKey, null, null);
        if (report == null) {
            // 匹配编码
            report = reportMapper.selectReportIgnorePermission(null, reportKey, tenantId);
        }
        return report;
    }

    @Override
    public Report selectReportMateData(String reportKey, Long tenantId) {
        List<Long> roleIds = DetailsHelper.getUserDetails().roleMergeIds();
        LocalDate localDate = LocalDateTime.now().toLocalDate();
        // 匹配报表UUID
        Report report = reportMapper.selectReportMateData(reportKey, null, tenantId, null, roleIds, localDate);
        if (report == null) {
            // 匹配报表编码
            report = reportMapper.selectReportMateData(null, reportKey, tenantId, tenantId, roleIds, localDate);
        }
        return report;
    }

    @Override
    public Page<Report> pageReportByDataSet(PageRequest pageRequest, Long datasetId, Long tenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> reportMapper.listReportByDataSet(datasetId, tenantId));
    }
}
