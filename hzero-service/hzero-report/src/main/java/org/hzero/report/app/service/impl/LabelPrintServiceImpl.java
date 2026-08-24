package org.hzero.report.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.app.service.LabelPrintService;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.LabelPrintRepository;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.report.domain.service.ILabelGenerateService;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.meta.form.FormElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 标签打印应用服务默认实现
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Service
public class LabelPrintServiceImpl implements LabelPrintService {

    private LabelPrintRepository labelPrintRepository;
    private ILabelGenerateService labelGenerateService;
    private IReportMetaService reportMetaService;
    private LabelTemplateRepository labelTemplateRepository;

    public LabelPrintServiceImpl(LabelPrintRepository labelPrintRepository,
                                 ILabelGenerateService labelGenerateService,
                                 IReportMetaService reportMetaService,
                                 LabelTemplateRepository labelTemplateRepository) {
        this.labelPrintRepository = labelPrintRepository;
        this.labelGenerateService = labelGenerateService;
        this.reportMetaService = reportMetaService;
        this.labelTemplateRepository = labelTemplateRepository;
    }

    @Override
    public LabelPrint detailLabelPrint(Long tenantId, String labelTemplateCode) {
        if (tenantId == null || StringUtils.isEmpty(labelTemplateCode)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        return labelPrintRepository.selectOne(new LabelPrint().setTenantId(tenantId).setLabelTemplateCode(labelTemplateCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelPrint createOrUpdatePrint(LabelPrint labelPrint) {
        if (labelPrint.getLabelPrintId() == null) {
            labelPrintRepository.insertSelective(labelPrint);
        } else {
            labelPrintRepository.updateOptional(labelPrint,
                    LabelPrint.FIELD_PAPER_SIZE,
                    LabelPrint.FIELD_PAPER_WIDTH,
                    LabelPrint.FIELD_PAPER_HIGH,
                    LabelPrint.FIELD_PRINT_DIRECTION,
                    LabelPrint.FIELD_MARGIN_TOP,
                    LabelPrint.FIELD_MARGIN_BOTTOM,
                    LabelPrint.FIELD_MARGIN_LEFT,
                    LabelPrint.FIELD_MARGIN_RIGHT,
                    LabelPrint.FIELD_WIDE_QTY,
                    LabelPrint.FIELD_HIGH_QTY,
                    LabelPrint.FIELD_HIGH_SPACE);
        }
        return labelPrint;
    }

    @Override
    public void getLabelData(String labelTemplateCode, JSONObject data, HttpServletRequest request) {
        LabelTemplate labelTemplate = labelTemplateRepository.selectLabelTemplate(labelTemplateCode,
                DetailsHelper.getUserDetails().getRoleId(),
                DetailsHelper.getUserDetails().getTenantId(),
                LocalDateTime.now().toLocalDate());
        if (labelTemplate == null) {
            data.put(HrptConstants.LABEL, org.apache.commons.lang3.StringUtils.EMPTY);
        } else {
            data.put(HrptConstants.LABEL, labelTemplate.getTemplateContent());
        }
    }

    @Override
    public Map<String, Object> getBuildInParameters(Map<String, String[]> parameterMap) {
        return reportMetaService.getBuildInParameters(parameterMap);
    }

    @Override
    public LabelTemplate selectLabel(String labelTemplateCode, Map<String, Object> buildInParams) {
        LabelTemplate labelTemplate = labelTemplateRepository.selectLabelTemplate(labelTemplateCode,
                DetailsHelper.getUserDetails().getRoleId(),
                DetailsHelper.getUserDetails().getTenantId(),
                LocalDateTime.now().toLocalDate());
        if (labelTemplate != null) {
            List<FormElement> formElements = reportMetaService.getQueryParamFormElements(labelTemplate.getDatasetId(), buildInParams);
            labelTemplate.setFormElements(formElements);
        }
        return labelTemplate;
    }

    @Override
    public LabelTemplate selectLabel(Long tenantId, String labelTemplateCode, Map<String, Object> buildInParams) {
        LabelTemplate labelTemplate = labelTemplateRepository.selectLabelTemplate(labelTemplateCode,
                DetailsHelper.getUserDetails().getRoleId(),
                tenantId,
                LocalDateTime.now().toLocalDate());
        if (labelTemplate != null) {
            List<FormElement> formElements = reportMetaService.getQueryParamFormElements(labelTemplate.getDatasetId(), buildInParams);
            labelTemplate.setFormElements(formElements);
        }
        return labelTemplate;
    }

    @Override
    public JSONObject getLabelHtmlData(String labelTemplateCode, HttpServletRequest request) {
        final JSONObject data = new JSONObject();
        data.put(HrptConstants.LABEL, labelGenerateService.generateLabel(labelTemplateCode, request));
        return data;
    }

    @Override
    public void getLabelData(Long tenantId, String labelTemplateCode, JSONObject data, HttpServletRequest request) {
        LabelTemplate labelTemplate = labelTemplateRepository.selectLabelTemplate(labelTemplateCode,
                DetailsHelper.getUserDetails().getRoleId(),
                tenantId == null ? DetailsHelper.getUserDetails().getTenantId() : tenantId,
                LocalDateTime.now().toLocalDate());
        if (labelTemplate == null) {
            data.put(HrptConstants.LABEL, org.apache.commons.lang3.StringUtils.EMPTY);
        } else {
            data.put(HrptConstants.LABEL, labelTemplate.getTemplateContent());
        }
    }

    @Override
    public JSONObject getLabelHtmlData(Long tenantId, String labelTemplateCode, HttpServletRequest request) {
        final JSONObject data = new JSONObject();
        data.put(HrptConstants.LABEL, labelGenerateService.generateLabel(tenantId, labelTemplateCode, request));
        return data;
    }
}
