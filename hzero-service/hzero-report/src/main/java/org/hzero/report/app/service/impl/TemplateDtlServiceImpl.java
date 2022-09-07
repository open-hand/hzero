package org.hzero.report.app.service.impl;

import java.util.List;

import org.hzero.core.util.EscapeUtils;
import org.hzero.report.app.service.TemplateDtlService;
import org.hzero.report.domain.entity.Template;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.repository.TemplateDtlRepository;
import org.hzero.report.domain.repository.TemplateRepository;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.enums.TemplateTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 报表模板明细应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
@Service
public class TemplateDtlServiceImpl implements TemplateDtlService {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateDtlRepository templateDtlRepository;

    @Override
    public void deleteTemplateDtl(List<TemplateDtl> templateDtls) {
        templateDtlRepository.batchDelete(templateDtls);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertTemplateDtl(TemplateDtl templateDtl) {
        // 验证类型
        this.validateTemplateType(templateDtl);
        templateDtlRepository.insertSelective(templateDtl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateDtl(TemplateDtl templateDtl) {
        // 验证类型
        this.validateTemplateType(templateDtl);
        // 更新
        templateDtlRepository.updateOptional(templateDtl, TemplateDtl.FIELD_TEMPLATE_URL,
                TemplateDtl.FIELD_TEMPLATE_CONTENT, TemplateDtl.FIELD_LANG);
    }

    /**
     * 验证模板类型
     */
    private void validateTemplateType(TemplateDtl templateDtl) {
        Template template = templateRepository.selectByPrimaryKey(templateDtl.getTemplateId());
        Assert.notNull(template, HrptMessageConstants.ERROR_TEMPLATE_NOT_EXIST);
        if (TemplateTypeEnum.HTML.getValue().equals(template.getTemplateTypeCode())) {
            Assert.notNull(templateDtl.getTemplateContent(), HrptMessageConstants.ERROR_TEMPLATE_CONTENT_NULL);
            // 防范XSS攻击
            templateDtl.setTemplateContent(EscapeUtils.preventScript(templateDtl.getTemplateContent()));
        } else {
            Assert.notNull(templateDtl.getTemplateUrl(), HrptMessageConstants.ERROR_TEMPLATE_FILE_NULL);
        }
    }
}