package org.hzero.platform.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.FormHeaderService;
import org.hzero.platform.domain.entity.FormHeader;
import org.hzero.platform.domain.entity.FormLine;
import org.hzero.platform.domain.repository.FormHeaderRepository;
import org.hzero.platform.domain.repository.FormLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 表单配置头应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
@Service
public class FormHeaderServiceImpl implements FormHeaderService {

    private final FormHeaderRepository headerRepository;
    private final FormLineRepository lineRepository;

    @Autowired
    public FormHeaderServiceImpl(FormHeaderRepository headerRepository, FormLineRepository lineRepository) {
        this.headerRepository = headerRepository;
        this.lineRepository = lineRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormHeader(FormHeader formHeader) {
        // 先删除表单头，再删除表单行
        Assert.notNull(formHeader.getFormHeaderId(), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        headerRepository.deleteByPrimaryKey(formHeader);
        FormLine formLine = new FormLine();
        formLine.setFormHeaderId(formHeader.getFormHeaderId());
        formLine.setTenantId(formHeader.getTenantId());
        // 根据表单头Id和租户Id删除表单行
        lineRepository.delete(formLine);
    }

    @Override
    public FormHeader createFormHeader(FormHeader formHeader) {
        // 校验参数是否存在
        headerRepository.checkRepeat(formHeader);
        // 插入数据
        headerRepository.insertSelective(formHeader);
        return formHeader;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormHeader updateFormHeader(FormHeader formHeader) {
        // 更新参数
        headerRepository.updateOptional(formHeader, FormHeader.FIELD_FORM_DESCRIPTION, FormHeader.FIELD_ENABLED_FLAG,
                        FormHeader.FIELD_FORM_NAME);
        return formHeader;
    }
}
