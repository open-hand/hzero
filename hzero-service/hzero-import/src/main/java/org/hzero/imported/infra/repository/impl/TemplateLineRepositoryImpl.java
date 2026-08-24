package org.hzero.imported.infra.repository.impl;

import java.util.List;

import org.hzero.imported.domain.entity.TemplateLine;
import org.hzero.imported.domain.entity.TemplateLineTl;
import org.hzero.imported.domain.repository.TemplateLineRepository;
import org.hzero.imported.infra.mapper.TemplateLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 模板行 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/29 11:07
 */
@Component
public class TemplateLineRepositoryImpl extends BaseRepositoryImpl<TemplateLine> implements TemplateLineRepository {

    private final TemplateLineMapper templateLineMapper;

    @Autowired
    public TemplateLineRepositoryImpl(TemplateLineMapper templateLineMapper) {
        this.templateLineMapper = templateLineMapper;
    }

    @Override
    public int deleteByHeaderId(Long templateId) {
        return templateLineMapper.deleteByHeaderId(templateId);
    }

    @Override
    public int deleteByTargetId(Long targetId) {
        return templateLineMapper.deleteByTargetId(targetId);
    }

    @Override
    public List<TemplateLine> listTemplateLine(Long templateHeaderId) {
        return templateLineMapper.listTemplateLine(templateHeaderId);
    }

    @Override
    public List<TemplateLineTl> getColumnNameTl(Long templateLineId) {
        return templateLineMapper.getColumnNameTl(templateLineId);
    }

    @Override
    public List<TemplateLine> listTemplateLineByTargetId(Long targetId, String columnCode, String columnName) {
        return templateLineMapper.listTemplateLineByTargetId(targetId, columnCode, columnName);
    }

    @Override
    public TemplateLine getTemplateLine(Long templateLineId) {
        return templateLineMapper.getTemplateLine(templateLineId);
    }
}
