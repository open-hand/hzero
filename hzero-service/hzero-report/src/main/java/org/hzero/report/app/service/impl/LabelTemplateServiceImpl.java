package org.hzero.report.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.report.app.service.LabelPermissionService;
import org.hzero.report.app.service.LabelTemplateService;
import org.hzero.report.domain.entity.LabelParameter;
import org.hzero.report.domain.entity.LabelPermission;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.LabelParameterRepository;
import org.hzero.report.domain.repository.LabelPermissionRepository;
import org.hzero.report.domain.repository.LabelPrintRepository;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 标签模板应用服务默认实现
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Service
public class LabelTemplateServiceImpl implements LabelTemplateService {

    private LabelTemplateRepository labelTemplateRepository;
    private LabelParameterRepository labelParameterRepository;
    private LabelPrintRepository labelPrintRepository;
    private LabelPermissionRepository labelPermissionRepository;
    private LabelPermissionService labelPermissionService;

    public LabelTemplateServiceImpl(LabelTemplateRepository labelTemplateRepository,
                                    LabelParameterRepository labelParameterRepository,
                                    LabelPrintRepository labelPrintRepository,
                                    LabelPermissionRepository labelPermissionRepository,
                                    LabelPermissionService labelPermissionService) {
        this.labelTemplateRepository = labelTemplateRepository;
        this.labelParameterRepository = labelParameterRepository;
        this.labelPrintRepository = labelPrintRepository;
        this.labelPermissionRepository = labelPermissionRepository;
        this.labelPermissionService = labelPermissionService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelTemplate createLabelTemplate(LabelTemplate labelTemplate) {
        // 唯一校验
        Assert.isTrue(UniqueHelper.valid(labelTemplate), BaseConstants.ErrorCode.DATA_EXISTS);
        // 建模板
        labelTemplateRepository.insertSelective(labelTemplate);
        // 为本租户分配权限
        labelPermissionService.createLabelPermission(new LabelPermission().setLabelTemplateId(labelTemplate.getLabelTemplateId()).setTenantId(labelTemplate.getTenantId()));
        // 创建默认打印配置
        LabelPrint labelPrint = new LabelPrint();
        labelPrint.setTenantId(labelTemplate.getTenantId()).setPaperHigh(HrptConstants.LabelAttribute.DEFAULT_HEIGHT)
                .setPaperWidth(HrptConstants.LabelAttribute.DEFAULT_WIDTH).setPaperSize(HrptConstants.DEFAULT_PAPER)
                .setPrintDirection(HrptConstants.LabelPrintSetting.VERTICAL).setMarginTop(HrptConstants.LabelPrintSetting.DEFAULT_MARGIN_TOP)
                .setMarginBottom(HrptConstants.LabelPrintSetting.DEFAULT_MARGIN_BOTTOM).setMarginLeft(HrptConstants.LabelPrintSetting.DEFAULT_MARGIN_LEFT)
                .setMarginRight(HrptConstants.LabelPrintSetting.DEFAULT_MARGIN_RIGHT).setWideQty(HrptConstants.LabelPrintSetting.DEFAULT_WITH_QTY)
                .setLabelTemplateCode(labelTemplate.getTemplateCode());
        labelPrintRepository.insertSelective(labelPrint);
        return labelTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelTemplate updateLabelTemplate(LabelTemplate labelTemplate) {
        // 修改模板
        labelTemplateRepository.updateOptional(labelTemplate,
                LabelTemplate.FIELD_TEMPLATE_NAME,
                LabelTemplate.FIELD_DATASET_ID,
                LabelTemplate.FIELD_TEMPLATE_WIDTH,
                LabelTemplate.FIELD_TEMPLATE_HIGH,
                LabelTemplate.FIELD_TEMPLATE_CONTENT,
                LabelTemplate.FIELD_ENABLED_FLAG);
        List<LabelParameter> parameters = labelTemplate.getLabelParameterList();
        if (!CollectionUtils.isEmpty(parameters)) {
            parameters.forEach(params -> {
                // 创建或者修改参数
                if (params.getLabelParameterId() == null) {
                    params.setLabelTemplateId(labelTemplate.getLabelTemplateId());
                    labelParameterRepository.insertSelective(params);
                } else {
                    labelParameterRepository.updateOptional(params,
                            LabelParameter.FIELD_PARAMETER_NAME,
                            LabelParameter.FIELD_BAR_CODE_TYPE,
                            LabelParameter.FIELD_CHARACTER_ENCODING,
                            LabelParameter.FIELD_TEXT_LENGTH,
                            LabelParameter.FIELD_MAX_ROWS);
                }
            });
        }
        return labelTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLabelTemplate(LabelTemplate labelTemplate) {
        // 删除标签参数
        labelParameterRepository.delete(new LabelParameter().setLabelTemplateId(labelTemplate.getLabelTemplateId()));
        // 删除打印配置
        labelPrintRepository.delete(new LabelPrint().setTenantId(labelTemplate.getTenantId()).setLabelTemplateCode(labelTemplate.getTemplateCode()));
        // 删除权限
        labelPermissionRepository.delete(new LabelPermission().setLabelTemplateId(labelTemplate.getLabelTemplateId()));
        // 删除标签
        labelTemplateRepository.deleteByPrimaryKey(labelTemplate.getLabelTemplateId());
    }

    @Override
    public LabelTemplate detailLabelTemplate(Long labelTemplateId) {
        LabelTemplate labelTemplate = labelTemplateRepository.getLabelTemplateById(labelTemplateId);
        Assert.notNull(labelTemplate, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        List<LabelParameter> labelParameters = labelParameterRepository.selectByCondition(Condition.builder(LabelParameter.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelTemplate.FIELD_LABEL_TEMPLATE_ID, labelTemplateId))
                .build());
        labelTemplate.setLabelParameterList(labelParameters);
        return labelTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelTemplate copyLabelTemplate(LabelTemplate labelTemplate, Long tenantId) {
        LabelTemplate template = labelTemplateRepository.selectByPrimaryKey(labelTemplate.getLabelTemplateId());
        // 复制模板
        labelTemplate.setLabelTemplateId(null).setTenantId(tenantId).setDatasetId(template.getDatasetId()).setTemplateContent(template.getTemplateContent());
        // 唯一校验
        Assert.isTrue(UniqueHelper.valid(labelTemplate), HrptMessageConstants.ERROR_REPEAT_CODE);
        labelTemplateRepository.insertSelective(labelTemplate);
        // 复制参数
        List<LabelParameter> labelParameters = labelParameterRepository.select(new LabelParameter().setLabelTemplateId(template.getLabelTemplateId()));
        labelParameters.forEach(labelParameter -> {
            LabelParameter parameter = new LabelParameter();
            BeanUtils.copyProperties(labelParameter, parameter, LabelParameter.FIELD_OBJECT_VERSION_NUMBER,
                    LabelParameter.FIELD_CREATED_BY, LabelParameter.FIELD_CREATED_BY, LabelParameter.FIELD_LAST_UPDATE_DATE,
                    LabelParameter.FIELD_LAST_UPDATED_BY);
            parameter.setLabelParameterId(null).setLabelTemplateId(labelTemplate.getLabelTemplateId()).setTenantId(tenantId);
            labelParameterRepository.insertSelective(parameter);
        });
        // 复制打印配置
        LabelPrint print = labelPrintRepository.selectLabelPrintAttribute(template.getTenantId(), template.getTemplateCode());
        if (print != null) {
            print.setLabelTemplateCode(labelTemplate.getTemplateCode()).setTenantId(tenantId).setLabelPrintId(null);
            labelPrintRepository.insertSelective(print);
        }
        // 为本租户分配权限
        labelPermissionService.createLabelPermission(new LabelPermission().setLabelTemplateId(labelTemplate.getLabelTemplateId()).setTenantId(tenantId));
        return labelTemplate;
    }
}
