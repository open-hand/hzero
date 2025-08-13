package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.FormLine;
import org.hzero.platform.domain.repository.FormLineRepository;
import org.hzero.platform.infra.mapper.FormLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 表单配置行 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
@Component
public class FormLineRepositoryImpl extends BaseRepositoryImpl<FormLine> implements FormLineRepository {

    private final FormLineMapper formLineMapper;

    @Autowired
    public FormLineRepositoryImpl(FormLineMapper formLineMapper) {
        this.formLineMapper = formLineMapper;
    }

    @Override
    @ProcessLovValue
    public Page<FormLine> pageFormLines(PageRequest pageRequest, FormLine formLine) {
        return PageHelper.doPageAndSort(pageRequest, () -> formLineMapper.selectFormLines(formLine));
    }

    @Override
    public void checkRepeat(FormLine formLine) {
        int count = this.selectCountByCondition(Condition.builder(FormLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(FormLine.FIELD_FORM_HEADER_ID, formLine.getFormHeaderId())
                        .andEqualTo(FormLine.FIELD_ITEM_CODE, formLine.getItemCode())
                        .andEqualTo(FormLine.FIELD_TENANT_ID, formLine.getTenantId())
                )
                .build());
        if (count > 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
    }
}
