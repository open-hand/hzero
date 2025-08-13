package org.hzero.report.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.api.dto.TemplateDTO;
import org.hzero.report.domain.entity.ReportTemplate;
import org.hzero.report.domain.entity.TemplateDtl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板关系资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
public interface ReportTemplateRepository extends BaseRepository<ReportTemplate> {

    /**
     * 查询报表模板列表
     *
     * @param pageRequest 分页
     * @param reportId    报表Id
     * @return 分页信息
     */
    Page<ReportTemplate> selectReportTemplatesByReportId(PageRequest pageRequest, Long reportId);

    /**
     * 查询报表模板明细
     *
     * @param pageRequest    分页
     * @param reportTemplate 报表模板参数
     * @return 分页信息
     */
    Page<ReportTemplate> selectReportTemplate(PageRequest pageRequest, ReportTemplate reportTemplate);

    /**
     * 重置报表模板默认标识
     *
     * @param reportId         报表Id
     * @param reportTemplateId 报表模板Id
     * @param tenantId         租户Id
     */
    void resetReportTemplateDefaultFlag(Long reportId, Long reportTemplateId, Long tenantId);

    /**
     * 获取报表模板列表
     *
     * @param tenantId     租户Id
     * @param reportId     报表Id
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 模板列表
     */
    List<TemplateDtl> selectTemplateDtls(Long tenantId, Long reportId, String templateCode, String lang);

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
