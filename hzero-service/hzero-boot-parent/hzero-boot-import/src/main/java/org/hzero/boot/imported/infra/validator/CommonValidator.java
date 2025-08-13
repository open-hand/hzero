package org.hzero.boot.imported.infra.validator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.TemplateColumn;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.validator.annotation.ImportCommonValidator;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.Regexs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 常规校验
 *
 * @author chunqiang.bai@hand-china.com
 */
@ImportCommonValidator
public class CommonValidator extends ValidatorHandler {

    private final LovAdapter lovAdapter;
    private final ObjectMapper objectMapper;
    private final ImportDataRepository dataRepository;

    /**
     * 是否更新数据标识
     */
    private boolean updateFlag = false;

    @Autowired
    public CommonValidator(LovAdapter lovAdapter,
                           ObjectMapper objectMapper,
                           ImportDataRepository dataRepository) {
        this.lovAdapter = lovAdapter;
        this.objectMapper = objectMapper;
        this.dataRepository = dataRepository;
    }

    @Override
    public boolean validate(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        List<TemplateColumn> templateColumnList = getTemplatePage().getTemplateColumnList();
        Long dataId = getContext().getId();
        if (CollectionUtils.isEmpty(templateColumnList)) {
            return true;
        }
        boolean valFlag = true;

        List<String> multi = new ArrayList<>();
        // 将多语言结构反转回来，方便下面数据校验
        convert(jsonObject, multi);

        for (TemplateColumn column : templateColumnList) {
            // 校验标识或启用标识为否，不进行校验
            if (Objects.equals(column.getEnabledFlag(), BaseConstants.Flag.NO) || Objects.equals(column.getValidateFlag(), BaseConstants.Flag.NO)) {
                continue;
            }

            valFlag = validateNull(jsonObject, valFlag, column);
            if (!valFlag) {
                clearMulti(jsonObject, multi);
                return false;
            }
            switch (column.getColumnType()) {
                case HimpBootConstants.ColumnType.STRING:
                case HimpBootConstants.ColumnType.MULTI:
                    valFlag = validateStr(jsonObject, column);
                    break;
                case HimpBootConstants.ColumnType.DECIMAL:
                    valFlag = validateDecimal(jsonObject, column);
                    break;
                case HimpBootConstants.ColumnType.DATE:
                    valFlag = validateDate(jsonObject, column);
                    break;
                case HimpBootConstants.ColumnType.LONG:
                    valFlag = validateLong(jsonObject, column);
                    break;
                default:
                    addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_FIELD).desc());
                    break;
            }
        }
        // 更新数据
        if (updateFlag) {
            try {
                String jsonData = objectMapper.writeValueAsString(jsonObject);
                dataRepository.updateOptional(new ImportData().setId(dataId).setData(jsonData), ImportData.FIELD_DATA);
                getContext().setData(jsonData);
            } catch (JsonProcessingException e) {
                throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
            }
        }
        clearMulti(jsonObject, multi);
        return valFlag;
    }

    private boolean validateLong(JSONObject jsonObject, TemplateColumn column) {
        boolean valFlag = true;
        // 最大最小值校验
        try {
            Object node = jsonObject.get(column.getColumnCode());
            if (ObjectUtils.isEmpty(node)) {
                return true;
            }
            long valLong = Long.parseLong(String.valueOf(node));
            if (column.getMaxValue() != null && checkNumberMax(BigDecimal.valueOf(valLong), column.getMaxValue())) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_MORE_THAN, new Object[]{column.getColumnName(), column.getMaxValue()}).desc());
                valFlag = false;
            }
            if (column.getMinValue() != null && checkNumberMin(BigDecimal.valueOf(valLong), column.getMinValue())) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_LESS_THAN, new Object[]{column.getColumnName(), column.getMinValue()}).desc());
                valFlag = false;
            }
        } catch (NumberFormatException numberformatexception) {
            addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_NOT_NUMBER, new Object[]{column.getColumnName()}).desc());
            valFlag = false;
        }
        return valFlag;
    }

    private boolean validateDate(JSONObject jsonObject, TemplateColumn column) {
        boolean valFlag = true;
        // 最大最小值校验
        try {
            Object node = jsonObject.get(column.getColumnCode());
            if (ObjectUtils.isEmpty(node)) {
                return true;
            }
            String valStr = String.valueOf(node);
            String format = StringUtils.isBlank(column.getFormatMask()) ? HimpBootConstants.DATE_FORMAT : column.getFormatMask();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date valDate = sdf.parse(valStr);
            if (column.getMaxValue() != null && checkDateMax(valDate, sdf, column.getMaxValue())) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_MORE_THAN, new Object[]{column.getColumnName(), column.getMaxValue()}).desc());
                valFlag = false;
            }
            if (column.getMinValue() != null && checkDateMin(valDate, sdf, column.getMinValue())) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_LESS_THAN, new Object[]{column.getColumnName(), column.getMinValue()}).desc());
                valFlag = false;
            }
        } catch (Exception e) {
            addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_NOT_DATE, new Object[]{column.getColumnName()}).desc());
            valFlag = false;
        }
        return valFlag;
    }

    private boolean validateDecimal(JSONObject jsonObject, TemplateColumn column) {
        boolean valFlag = true;
        // 最大最小值校验
        try {
            Object node = jsonObject.get(column.getColumnCode());
            if (ObjectUtils.isEmpty(node)) {
                return true;
            }
            BigDecimal val = new BigDecimal(String.valueOf(node));
            if (column.getMaxValue() != null && checkNumberMax(val, column.getMaxValue())) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_MORE_THAN, new Object[]{column.getColumnName(), column.getMaxValue()}).desc());
                valFlag = false;
            }
            if (column.getMinValue() != null && checkNumberMin(val, column.getMinValue())) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_LESS_THAN, new Object[]{column.getColumnName(), column.getMinValue()}).desc());
                valFlag = false;
            }
        } catch (NumberFormatException numberformatexception) {
            addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_NOT_DECIMAL, new Object[]{column.getColumnName()}).desc());
            valFlag = false;
        }
        return valFlag;
    }

    private boolean validateStr(JSONObject jsonObject, TemplateColumn column) {
        boolean valFlag = true;
        Object node = jsonObject.get(column.getColumnCode());
        if (ObjectUtils.isEmpty(node)) {
            return true;
        }
        String str = String.valueOf(node);
        // 值集校验及值集转换
        if (StringUtils.isNotBlank(column.getValidateSet())) {
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            Long tenantId = column.getTenantId();
            if (userDetails != null) {
                tenantId = userDetails.getTenantId();
            }
            List<LovValueDTO> list = lovAdapter.queryLovValue(column.getValidateSet(), tenantId);
            Map<String, String> lov = list.stream().collect(Collectors.toMap(LovValueDTO::getMeaning, LovValueDTO::getValue, (key1, key2) -> key2));
            if (!lov.containsValue(str)) {
                if (lov.containsKey(str)) {
                    // 是否开启数据转换
                    if (Objects.equals(column.getChangeDataFlag(), BaseConstants.Flag.YES)) {
                        // 若输入的值是meaning，自动转换为code
                        updateFlag = true;
                        jsonObject.put(column.getColumnCode(), lov.get(str));
                    }
                } else {
                    addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_LOV_MISMATCH, new Object[]{column.getColumnName()}).desc());
                    valFlag = false;
                }
            }
        }
        // 长度校验
        if (column.getLength() != null && str.length() > column.getLength()) {
            addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_LENGTH_EXCEEDS_LIMIT, new Object[]{column.getColumnName()}).desc());
            valFlag = false;
        }
        // 正则校验
        if (StringUtils.isNotBlank(column.getRegularExpression())) {
            Set<String> result = Regexs.matchString(column.getRegularExpression(), str);
            if (!(CollectionUtils.isNotEmpty(result) && Objects.equals(new ArrayList<>(result).get(BaseConstants.Digital.ZERO), str))) {
                addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_REGULAR_MISMATCH, new Object[]{column.getColumnName()}).desc());
                valFlag = false;
            }
        }
        return valFlag;
    }

    private boolean validateNull(JSONObject jsonObject, boolean valFlag, TemplateColumn column) {
        // 非空校验
        if (Objects.equals(column.getNullableFlag(), BaseConstants.Flag.NO) && ObjectUtils.isEmpty(jsonObject.get(column.getColumnCode()))) {
            addErrorMsg(MessageAccessor.getMessage(HimpBootConstants.ErrorCode.ERROR_NOT_NULL, new Object[]{column.getColumnName()}).desc());
            valFlag = false;
        }
        return valFlag;
    }

    private void convert(JSONObject jsonObject, List<String> multi) {
        if (!jsonObject.containsKey(HimpBootConstants.TLS)) {
            return;
        }
        Map<String, Map<String, String>> tls = JSON.parseObject(JSON.toJSONString(jsonObject.get(HimpBootConstants.TLS)), new TypeReference<Map<String, Map<String, String>>>() {
        });
        for (Map.Entry<String, Map<String, String>> entry : tls.entrySet()) {
            String code = entry.getKey();
            for (Map.Entry<String, String> item : entry.getValue().entrySet()) {
                String lang = item.getKey();
                String value = item.getValue();
                String field = code + BaseConstants.Symbol.COLON + lang;
                jsonObject.put(field, value);
                multi.add(field);
            }
        }
    }

    private void clearMulti(JSONObject jsonObject, List<String> multi) {
        for (String code : multi) {
            jsonObject.remove(code);
        }
    }


    private boolean checkNumberMax(BigDecimal val, String max) {
        if (StringUtils.isEmpty(max)) {
            return false;
        }
        BigDecimal maxVal = new BigDecimal(max);
        return maxVal.compareTo(val) < BaseConstants.Digital.ZERO;
    }

    private boolean checkNumberMin(BigDecimal val, String min) {
        if (StringUtils.isEmpty(min)) {
            return false;
        }
        BigDecimal minVal = new BigDecimal(min);
        return minVal.compareTo(val) > BaseConstants.Digital.ZERO;
    }

    private boolean checkDateMax(Date val, SimpleDateFormat sdf, String max) throws ParseException {
        if (StringUtils.isEmpty(max)) {
            return false;
        }
        Date maxVal = sdf.parse(max);
        return !maxVal.after(val);
    }

    private boolean checkDateMin(Date val, SimpleDateFormat sdf, String min) throws ParseException {
        if (StringUtils.isEmpty(min)) {
            return false;
        }
        Date minVal = sdf.parse(min);
        return !minVal.before(val);
    }
}