package org.hzero.report.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.api.dto.TemplateDTO;
import org.hzero.report.domain.entity.ReportTemplate;
import org.hzero.report.domain.entity.TemplateDtl;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表模板关系Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
public interface ReportTemplateMapper extends BaseMapper<ReportTemplate> {

    /**
     * 获取报表模板列表
     *
     * @param reportId 报表Id
     * @return 模板分页
     */
    List<ReportTemplate> selectReportTemplatesByReportId(Long reportId);

    /**
     * 获取报表模板明细
     *
     * @param reportTemplate 模板
     * @return 模板分页
     */
    List<ReportTemplate> selectReportTemplate(ReportTemplate reportTemplate);

    /**
     * 获取被引用次数
     *
     * @param reportTemplateId 模板Id
     * @return 数量
     */
    int selectReferenceCount(Long reportTemplateId);

    /**
     * 重置报表模板默认标识
     *
     * @param reportId         报表Id
     * @param reportTemplateId 模板Id
     * @param tenantId         租户Id
     */
    void resetReportTemplateDefaultFlag(@Param("reportId") Long reportId,
                                        @Param("reportTemplateId") Long reportTemplateId,
                                        @Param("tenantId") Long tenantId);

    /**
     * 选择模板
     *
     * @param tenantId     租户Id
     * @param reportId     报表Id
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 模板列表
     */
    List<TemplateDtl> selectTemplateDtls(@Param("tenantId") Long tenantId,
                                         @Param("reportId") Long reportId,
                                         @Param("templateCode") String templateCode,
                                         @Param("lang") String lang);


    /**
     * 查询报表的模板
     *
     * @param reportId     报表Id
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @return 模板列表
     */
    List<TemplateDTO> getTemplatesByReport(@Param("reportId") Long reportId,
                                           @Param("templateCode") String templateCode,
                                           @Param("templateName") String templateName);
}
