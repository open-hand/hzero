package org.hzero.report.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.util.FileUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.repository.TemplateDtlRepository;
import org.hzero.report.infra.mapper.TemplateDtlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板明细 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
@Component
public class TemplateDtlRepositoryImpl extends BaseRepositoryImpl<TemplateDtl> implements TemplateDtlRepository {

    @Autowired
    private TemplateDtlMapper templateDtlMapper;

    @Override
    public Page<TemplateDtl> selectTemplateDtlsByTemplateId(PageRequest pageRequest, Long templateId) {
        Page<TemplateDtl> list = PageHelper.doPageAndSort(pageRequest, () -> templateDtlMapper.selectTemplateDtlsByTemplateId(templateId));
        if (CollectionUtils.isNotEmpty(list.getContent())){
            list.getContent().forEach(item -> item.setTemplateFileName(FileUtils.getFileName(item.getTemplateUrl())));
        }
        return list;
    }

    @Override
    public TemplateDtl selectTemplateDtl(Long templateDtlId) {
        TemplateDtl templateDtl = this.selectOneOptional(new TemplateDtl().setTemplateDtlId(templateDtlId),
                new Criteria().select(TemplateDtl.FIELD_TEMPLATE_DTL_ID, TemplateDtl.FIELD_TEMPLATE_ID,
                        TemplateDtl.FIELD_TEMPLATE_URL, TemplateDtl.FIELD_TENANT_ID,
                        TemplateDtl.FIELD_TEMPLATE_CONTENT, TemplateDtl.FIELD_LANG,
                        TemplateDtl.FIELD_OBJECT_VERSION_NUMBER));
        if (templateDtl != null) {
            templateDtl.setTemplateFileName(FileUtils.getFileName(templateDtl.getTemplateUrl()));
        }
        return templateDtl;
    }
}
