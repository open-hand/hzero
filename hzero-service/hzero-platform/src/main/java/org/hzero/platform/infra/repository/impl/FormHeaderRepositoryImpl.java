package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.FormHeader;
import org.hzero.platform.domain.repository.FormHeaderRepository;
import org.hzero.platform.infra.mapper.FormHeaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 表单配置头 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
@Component
public class FormHeaderRepositoryImpl extends BaseRepositoryImpl<FormHeader> implements FormHeaderRepository {

    private final FormHeaderMapper mapper;

    @Autowired
    public FormHeaderRepositoryImpl(FormHeaderMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @ProcessLovValue
    public Page<FormHeader> pageFormHeaders(PageRequest pageRequest, FormHeader formHeader) {
        return PageHelper.doPageAndSort(pageRequest, () -> mapper.selectFormHeaders(formHeader));
    }

    @Override
    @ProcessLovValue
    public FormHeader selectFormHeaderDetails(Long formHeaderId) {
        return mapper.selectOneFormHeaderById(formHeaderId);
    }

    @Override
    public void checkRepeat(FormHeader formHeader) {
        int count = this.selectCountByCondition(Condition.builder(FormHeader.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(FormHeader.FIELD_FORM_CODE, formHeader.getFormCode())
                        .andEqualTo(FormHeader.FIELD_TENANT_ID, formHeader.getTenantId())).build());
        if (count > 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
    }

    @Override
    public List<FormHeader> listEnabledFormHeaders(Long tenantId) {
        return this.selectByCondition(Condition.builder(FormHeader.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(FormHeader.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                        .andEqualTo(FormHeader.FIELD_TENANT_ID, tenantId)
                )
                .build());
    }
}
