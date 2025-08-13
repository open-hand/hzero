package org.hzero.report.app.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.report.api.dto.TemplateDTO;
import org.hzero.report.app.service.ReportTemplateService;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.ReportTemplate;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.domain.repository.ReportTemplateRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.enums.ReportTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板关系应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
@Service
public class ReportTemplateServiceImpl implements ReportTemplateService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ReportTemplateRepository reportTemplateRepository;

    @Override
    public boolean validateReportTemplateRepeat(List<ReportTemplate> reportTemplates) {
        for (ReportTemplate reportTemplate : reportTemplates) {
            if (!reportTemplate.validateRepeat(reportTemplateRepository)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证报表信息
     */
    @Override
    public void validateReportType(Long reportId) {
        Report record = new Report();
        record.setReportId(reportId);
        Report report = reportRepository.selectOne(record);
        Assert.notNull(report, HrptMessageConstants.ERROR_REPORT_NOT_EXIST);
        Assert.isTrue(StringUtils.endsWith(ReportTypeEnum.DOCUMENT.getValue(), report.getReportTypeCode()),
                HrptMessageConstants.ERROR_REPORT_TYPE_DOCUMENT);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertReportTemplate(List<ReportTemplate> reportTemplates) {
        // 验证报表信息合法性
        if (CollectionUtils.isEmpty(reportTemplates)) {
            return;
        }
        // 验证报表信息
        validateReportType(reportTemplates.get(0).getReportId());
        // 验证模板重复性质
        Assert.isTrue(this.validateReportTemplateRepeat(reportTemplates), BaseConstants.ErrorCode.DATA_EXISTS);
        // 插入
        reportTemplateRepository.batchInsert(reportTemplates);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void defaultTemplate(ReportTemplate reportTemplate) {
        // 情况其他模板默认状态
        reportTemplateRepository.resetReportTemplateDefaultFlag(reportTemplate.getReportId(), reportTemplate.getReportTemplateId(), reportTemplate.getTenantId());
        // 设置默认标识
        reportTemplateRepository.updateOptional(reportTemplate, ReportTemplate.FIELD_DEFAULT_FLAG);
    }

    @Override
    public Page<TemplateDTO> getTemplatesByReport(Long reportId, String templateCode, String templateName, PageRequest pageRequest) {
        Page<TemplateDTO> list = reportTemplateRepository.getTemplatesByReport(reportId, templateCode, templateName, pageRequest);
        if (CollectionUtils.isNotEmpty(list)) {
            Long tenantId = DetailsHelper.getUserDetails().getTenantId();
            for (TemplateDTO dto : list.getContent()) {
                if (Objects.equals(tenantId, dto.getTenantId())) {
                    dto.setType(MessageAccessor.getMessage(HrptConstants.CUSTOMIZE).desc());
                } else {
                    dto.setType(MessageAccessor.getMessage(HrptConstants.PREDEFINED).desc());
                }
            }
        }
        return list;
    }
}
