package org.hzero.report.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.Template;
import org.hzero.report.domain.repository.TemplateRepository;
import org.hzero.report.infra.mapper.TemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@Component
public class TemplateRepositoryImpl extends BaseRepositoryImpl<Template> implements TemplateRepository {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public Page<Template> selectTemplates(PageRequest pageRequest, Template template) {
        return PageHelper.doPageAndSort(pageRequest, () -> templateMapper.selectTemplates(template));
    }

    @Override
    public Template selectTemplate(Long templateId) {
        return templateMapper.selectTemplate(templateId);
    }

    @Override
    public int selectReferenceCount(Long templateId) {
        return templateMapper.selectReferenceCount(templateId);
    }

}
