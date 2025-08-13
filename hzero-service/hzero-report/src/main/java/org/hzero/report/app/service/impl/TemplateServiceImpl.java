package org.hzero.report.app.service.impl;

import org.hzero.report.app.service.TemplateService;
import org.hzero.report.domain.entity.Template;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.repository.TemplateDtlRepository;
import org.hzero.report.domain.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报表模版应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateDtlRepository templateDtlRepository;

    @Override
    public boolean validateTemplateRepeat(Template template) {
        Template temp = new Template();
        temp.setTenantId(template.getTenantId());
        temp.setTemplateCode(template.getTemplateCode());
        int i = templateRepository.selectCount(temp);
        return i <= 0;
    }

    @Override
    public boolean existReference(Long templateId) {
        int cnt = templateRepository.selectReferenceCount(templateId);
        return cnt > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long templateId) {
        // 删除多语言表
        templateDtlRepository.delete(new TemplateDtl().setTemplateId(templateId));
        // 删除
        templateRepository.deleteByPrimaryKey(templateId);
    }
}
