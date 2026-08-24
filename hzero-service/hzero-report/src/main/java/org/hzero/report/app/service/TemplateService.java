package org.hzero.report.app.service;

import org.hzero.report.domain.entity.Template;

/**
 * 报表模版应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
public interface TemplateService {

    /**
     * 验证模板唯一性
     *
     * @param template 报表模板
     * @return 是否唯一
     */
    boolean validateTemplateRepeat(Template template);

    /**
     * 是否存在引用
     *
     * @param templateId 模板Id
     * @return 是否存在引用
     */
    boolean existReference(Long templateId);

    /**
     * 删除模板
     *
     * @param templateId 模板ID
     */
    void deleteTemplate(Long templateId);

}
