package org.hzero.report.app.service;

import org.hzero.report.domain.entity.LabelTemplate;

/**
 * 标签模板应用服务
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
public interface LabelTemplateService {

    /**
     * 创建标签模板
     *
     * @param labelTemplate 标签模板
     * @return 标签模板
     */
    LabelTemplate createLabelTemplate(LabelTemplate labelTemplate);

    /**
     * 修改标签模板
     *
     * @param labelTemplate 标签模板
     * @return labelTemplate 标签模板
     */
    LabelTemplate updateLabelTemplate(LabelTemplate labelTemplate);

    /**
     * 删除标签模板
     *
     * @param labelTemplate 标签模板
     */
    void deleteLabelTemplate(LabelTemplate labelTemplate);

    /**
     * 查询标签模板明细
     *
     * @param labelTemplateId 标签模板ID
     * @return 查询结果
     */
    LabelTemplate detailLabelTemplate(Long labelTemplateId);

    /**
     * 复制标签模板
     *
     * @param labelTemplate 标签模板
     * @param tenantId      租户ID
     * @return 查询结果
     */
    LabelTemplate copyLabelTemplate(LabelTemplate labelTemplate, Long tenantId);
}
