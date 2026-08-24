package org.hzero.report.app.service;

import java.util.List;

import org.hzero.report.api.dto.TemplateDTO;
import org.hzero.report.domain.entity.ReportTemplate;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板关系应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
public interface ReportTemplateService {

    /**
     * 验证报表合法性
     *
     * @param reportId 报表Id
     */
    void validateReportType(Long reportId);

    /**
     * 验证模板唯一性
     *
     * @param reportTemplates 报表模板
     * @return 是否唯一
     */
    boolean validateReportTemplateRepeat(List<ReportTemplate> reportTemplates);

    /**
     * 批量插入报表模板关系
     *
     * @param reportTemplates 报表模板
     */
    void insertReportTemplate(List<ReportTemplate> reportTemplates);

    /**
     * 设置模板默认标识
     *
     * @param reportTemplate 报表模板
     */
    void defaultTemplate(ReportTemplate reportTemplate);

    /**
     * 查询报表的模板
     *
     * @param reportId     报表Id
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param pageRequest  分页
     * @return 模板列表
     */
    Page<TemplateDTO> getTemplatesByReport(Long reportId, String templateCode, String templateName, PageRequest pageRequest);
}
