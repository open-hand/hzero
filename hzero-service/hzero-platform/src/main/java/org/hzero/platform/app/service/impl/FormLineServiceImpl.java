package org.hzero.platform.app.service.impl;

import java.util.Collections;
import java.util.List;

import org.hzero.boot.platform.form.domain.repository.BaseFormLineRepository;
import org.hzero.boot.platform.form.domain.vo.FormLineVO;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.app.service.FormLineService;
import org.hzero.platform.domain.entity.FormHeader;
import org.hzero.platform.domain.entity.FormLine;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.repository.FormHeaderRepository;
import org.hzero.platform.domain.repository.FormLineRepository;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.infra.constant.Constants;
import org.springframework.beans.BeanUtils;
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
public class FormLineServiceImpl implements FormLineService {

    private final FormLineRepository formLineRepository;
    private final FormHeaderRepository formHeaderRepository;
    private final BaseFormLineRepository baseFormLineRepository;
    private final LovAdapter lovAdapter;
    private final LovRepository lovRepository;

    @Autowired
    public FormLineServiceImpl(FormLineRepository formLineRepository, FormHeaderRepository formHeaderRepository,
            BaseFormLineRepository baseFormLineRepository, LovAdapter lovAdapter, LovRepository lovRepository) {
        this.formLineRepository = formLineRepository;
        this.formHeaderRepository = formHeaderRepository;
        this.baseFormLineRepository = baseFormLineRepository;
        this.lovAdapter = lovAdapter;
        this.lovRepository = lovRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormLine createFormLine(FormLine formLine) {
        // 先校验参数合法性
        formLineRepository.checkRepeat(formLine);
        this.validValueSetField(formLine);
        // 插入数据
        formLineRepository.insertSelective(formLine);
        // 添加缓存
        saveFormLineCache(formLine);
        return formLine;
    }

    @Override
    public FormLine updateFormLine(FormLine formLine) {
        formLineRepository.updateOptional(formLine, FormLine.FIELD_ORDER_SEQ, FormLine.FIELD_DEFAULT_VALUE,
                        FormLine.FIELD_ITEM_NAME, FormLine.FIELD_ENABLED_FLAG, FormLine.FIELD_ITEM_DESCRIPTION,
                        FormLine.FIELD_REQUIRED_FLAG, FormLine.FIELD_UPDATABLE_FLAG, FormLine.FIELD_VALUE_CONSTRAINT);
        // 更新缓存
        saveFormLineCache(formLine);
        return formLine;
    }

    @Override
    public void deleteFormLine(FormLine formLine) {
        // 单条删除表单行信息
        formLineRepository.deleteByPrimaryKey(formLine);
        // 删除缓存
        FormHeader formHeader = formHeaderRepository.selectByPrimaryKey(formLine.getFormHeaderId());
        baseFormLineRepository.deleteFormLineCache(formHeader.getFormCode(), formLine.getTenantId(),
                        formLine.getItemCode());
    }

    @Override
    public List<FormLine> listFormLineByHeaderCode(String formCode, Long tenantId) {
        FormHeader formHeader =
                        new FormHeader().setFormCode(formCode).setTenantId(tenantId)
                                        .setEnabledFlag(BaseConstants.Flag.YES);
        FormHeader resultHeader = formHeaderRepository.selectOne(formHeader);
        if (resultHeader == null) {
            // 查不到租户数据就查平台
            formHeader.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            resultHeader = formHeaderRepository.selectOne(formHeader);
            if (resultHeader == null) {
                return Collections.emptyList();
            }
        }
        // 查询行数据
        return formLineRepository.selectByCondition(Condition
                        .builder(FormLine.class)
                        .notSelect(FormLine.FIELD_CREATED_BY, FormLine.FIELD_CREATION_DATE, FormLine.FIELD_LAST_UPDATED_BY,
                                FormLine.FIELD_LAST_UPDATE_DATE)
                        .andWhere(Sqls.custom()
                                        .andEqualTo(FormLine.FIELD_FORM_HEADER_ID, resultHeader.getFormHeaderId())
                                        .andEqualTo(FormLine.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)).build());
    }

    @Override
    public List<FormLine> listFormLineByHeaderId(Long formHeaderId, Long tenantId) {
        return formLineRepository.selectByCondition(Condition
                        .builder(FormLine.class)
                        .notSelect(FormLine.FIELD_CREATED_BY, FormLine.FIELD_CREATION_DATE, FormLine.FIELD_LAST_UPDATED_BY,
                        FormLine.FIELD_LAST_UPDATE_DATE)
                        .andWhere(Sqls.custom().andEqualTo(FormLine.FIELD_FORM_HEADER_ID, formHeaderId)
                                        .andEqualTo(FormLine.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)).build());
    }

    @Override
    public List<LovValueDTO> translateValueSet(Long organizationId, Long formLineId, List<String> params) {
        FormLine formLine = formLineRepository.selectByPrimaryKey(formLineId);
        Assert.notNull(formLine, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 判断当前表单是否为值集/值集视图类型，并做对应处理
        switch (formLine.getItemTypeCode()) {
            case Constants.FormLineItemTypeCode.LOV:
                // 独立值集类型，直接调用翻译值集获取数据即可
                return lovAdapter.queryLovValue(formLine.getValueSet(), organizationId, params);
            case Constants.FormLineItemTypeCode.LOV_VIEW:
                // 值集视图类型，获取值集视图对应的值集信息作为翻译条件
                Lov lov = lovRepository.selectLovByViewCodeAndTenant(formLine.getValueSet(), organizationId);
                return lovAdapter.queryLovValue(lov.getLovCode(), lov.getTenantId(), params);
            default:
                // 非值集/值集视图类型无需翻译
                return null;
        }
    }

    /**
     * 添加或更新表单配置行缓存
     */
    private void saveFormLineCache(FormLine formLine) {
        FormHeader formHeader = formHeaderRepository.selectByPrimaryKey(formLine.getFormHeaderId());
        FormLineVO formLineVO = new FormLineVO();
        BeanUtils.copyProperties(formLine, formLineVO);
        baseFormLineRepository.saveFormLineCache(formHeader.getFormCode(), formLineVO);
    }

    /**
     * 表单类型为值集/值集视图时需要校验字段必输
     *
     * @param formLine 需校验的表单
     */
    private void validValueSetField(FormLine formLine) {
        if (Constants.FormLineItemTypeCode.LOV.equals(formLine.getItemTypeCode())
                || Constants.FormLineItemTypeCode.LOV_VIEW.equals(formLine.getItemTypeCode())) {
            // 表单类型为独立值集或值集视图类型，此时需保证lovSet字段值不能为空
            Assert.notNull(formLine.getValueSet(), BaseConstants.ErrorCode.NOT_NULL);
        }
    }
}
