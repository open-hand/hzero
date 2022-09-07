package org.hzero.report.infra.repository.impl;

import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.api.dto.TemplateDTO;
import org.hzero.report.domain.entity.ReportTemplate;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.repository.ReportTemplateRepository;
import org.hzero.report.infra.mapper.ReportTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板关系 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
@Component
public class ReportTemplateRepositoryImpl extends BaseRepositoryImpl<ReportTemplate> implements ReportTemplateRepository {

    private final ReportTemplateMapper reportTemplateMapper;

    @Autowired
    public ReportTemplateRepositoryImpl(ReportTemplateMapper reportTemplateMapper) {
        this.reportTemplateMapper = reportTemplateMapper;
    }

    @Override
    public Page<ReportTemplate> selectReportTemplatesByReportId(PageRequest pageRequest, Long reportId) {
        return PageHelper.doPageAndSort(pageRequest, () -> reportTemplateMapper.selectReportTemplatesByReportId(reportId));
    }

    @Override
    public Page<ReportTemplate> selectReportTemplate(PageRequest pageRequest, ReportTemplate reportTemplate) {
        return PageHelper.doPageAndSort(pageRequest, () -> reportTemplateMapper.selectReportTemplate(reportTemplate));
    }

    @Override
    public void resetReportTemplateDefaultFlag(Long reportId, Long reportTemplateId, Long tenantId) {
        reportTemplateMapper.resetReportTemplateDefaultFlag(reportId, reportTemplateId, tenantId);
    }

    @Override
    public List<TemplateDtl> selectTemplateDtls(Long tenantId, Long reportId, String templateCode, String lang) {
        return reportTemplateMapper.selectTemplateDtls(tenantId, reportId, templateCode, lang);
    }

    @Override
    public Page<TemplateDTO> getTemplatesByReport(Long reportId, String templateCode, String templateName, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> reportTemplateMapper.getTemplatesByReport(reportId, templateCode, templateName));
    }

}
