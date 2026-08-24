package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.api.dto.TemplateAssignDTO;
import org.hzero.platform.domain.entity.TemplateAssign;
import org.hzero.platform.domain.repository.TemplateAssignRepository;
import org.hzero.platform.infra.mapper.TemplateAssignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分配模板 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
@Component
public class TemplateAssignRepositoryImpl extends BaseRepositoryImpl<TemplateAssign> implements
                TemplateAssignRepository {

    @Autowired
    private TemplateAssignMapper templateAssignMapper;

    @Override
    public List<TemplateAssignDTO> listTemplateAssigns(String sourceType, String sourceKey, Long tenantId) {
        return templateAssignMapper.selectTemplateAssigns(sourceType, sourceKey, tenantId);
    }

    @Override
    public Page<TemplateAssignDTO> selectAssignableTemplates(PageRequest pageRequest, TemplateAssignDTO templates) {
        return PageHelper.doPageAndSort(pageRequest, () -> templateAssignMapper.selectAssignableTemplates(templates));
    }

    @Override
    public boolean checkDefaultTpl(Long templateAssignId) {
        TemplateAssign templateAssign = new TemplateAssign();
        templateAssign.setTemplateAssignId(templateAssignId);
        templateAssign.setDefaultFlag(BaseConstants.Flag.YES);
        int count = this.selectCount(templateAssign);
        return count > 0;
    }

    @Override
    public boolean checkDefaultTpl(String sourceKey, String sourceType, Long tenantId) {
        List<TemplateAssign> dbTemplates = this.selectByCondition(Condition.builder(TemplateAssign.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(TemplateAssign.FIELD_SOURCE_KEY, sourceKey)
                        .andEqualTo(TemplateAssign.FIELD_SOURCE_TYPE, sourceType)
                        .andEqualTo(TemplateAssign.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(TemplateAssign.FIELD_DEFAULT_FLAG, BaseConstants.Flag.YES)
                ).build());
        return CollectionUtils.isEmpty(dbTemplates);
    }
}
