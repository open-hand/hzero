package org.hzero.report.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.report.infra.mapper.LabelTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 标签模板 资源库实现
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Component
public class LabelTemplateRepositoryImpl extends BaseRepositoryImpl<LabelTemplate> implements LabelTemplateRepository {

    @Autowired
    private LabelTemplateMapper labelTemplateMapper;

    @Override
    public Page<LabelTemplate> listLabelTemplate(Long tenantId, String templateCode, String templateName, String datasetName, Integer enabledFlag, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> labelTemplateMapper.listLabelTemplate(tenantId, templateCode, templateName, datasetName, enabledFlag));
    }

    @Override
    public Page<LabelTemplate> listTenantLabelTemplate(Long tenantId, String templateCode, String templateName, String datasetName, Integer enabledFlag, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> labelTemplateMapper
                .listTenantLabelTemplate(tenantId, templateCode, templateName, datasetName, enabledFlag, LocalDate.now()));
    }

    @Override
    public LabelTemplate getLabelTemplateById(Long labelTemplateId) {
        return labelTemplateMapper.getLabelTemplateById(labelTemplateId);
    }

    @Override
    public LabelTemplate selectLabelTemplate(String labelTemplateCode, Long roleId, Long tenantId, LocalDate date) {
        return labelTemplateMapper.selectLabelTemplate(labelTemplateCode, roleId, tenantId, date);
    }
}
