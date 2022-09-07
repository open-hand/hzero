package org.hzero.boot.platform.form.constraints.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.form.constant.ErrorType;
import org.hzero.boot.platform.form.constant.FormConstants;
import org.hzero.boot.platform.form.constraints.FormConstraintsCheck;
import org.hzero.boot.platform.form.domain.vo.FormLineVO;
import org.hzero.boot.platform.form.result.ConstraintsCheckResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 表单配置值校验
 *
 * @author liufanghan 2019/11/20 9:37
 */
@SuppressWarnings("all")
@Component
public class FormValueConstraintsCheck implements FormConstraintsCheck {

    @Override
    public ConstraintsCheckResult checkConfigValue(String configValue, List<FormLineVO> formLineList) {
        if (CollectionUtils.isEmpty(formLineList)) {
            return new ConstraintsCheckResult();
        }
        Map<String, String> map = splitConfigValue(configValue);
        ConstraintsCheckResult result = new ConstraintsCheckResult(formLineList.size());
        formLineList.forEach(formLine -> checkConfigValue(result, map, formLine));
        return result;
    }

    @Override
    public ConstraintsCheckResult checkConfigValue(String oldConfig, String newConfig, List<FormLineVO> formLineList) {
        if (CollectionUtils.isEmpty(formLineList)) {
            return new ConstraintsCheckResult();
        }
        Map<String, String> oldMap = splitConfigValue(oldConfig);
        Map<String, String> newMap = splitConfigValue(newConfig);

        ConstraintsCheckResult result = new ConstraintsCheckResult(formLineList.size());

        formLineList.forEach(formLine -> {
            // 是否允许更新校验
            if (!FormConstants.UPDATABLE.equals(formLine.getUpdatableFlag())) {
                String oldValue = oldMap.get(formLine.getItemCode());
                String newValue = newMap.get(formLine.getItemCode());
                if (!Objects.equals(oldValue, newValue)) {
                    result.error(formLine.getItemCode(), ErrorType.CONFIG_VALUE_MISMATCH);
                }
            }
            checkConfigValue(result, newMap, formLine);
        });
        return result;
    }

    /**
     * 单个配置值约束校验
     *
     * @param result      校验结果
     * @param map         单个配置<code,value>
     * @param formLineDTO 表单行DTO
     */
    private void checkConfigValue(ConstraintsCheckResult result, Map<String, String> map, FormLineVO formLineDTO) {
        String valueConstraint = formLineDTO.getValueConstraint();
        if (StringUtils.isNotEmpty(valueConstraint)) {
            // 有约束才去校验
            String value = map.get(formLineDTO.getItemCode());
            if (value != null) {
                try {
                    Pattern compile = Pattern.compile(valueConstraint);
                    if (!compile.matcher(value).matches()) {
                        result.error(value, ErrorType.CONFIG_VALUE_MISMATCH);
                    }
                } catch (PatternSyntaxException e) {
                    result.error(valueConstraint, ErrorType.INVALID_REGULAR_EXPRESSION);
                }
            }
        }
    }

    /**
     * 将表单配置JSON文本拆分成 <key,value>
     *
     * @param configValueValue 表单配置JSON文本
     * @return 拆分结果
     */
    private Map<String, String> splitConfigValue(String configValueValue) {
        if (StringUtils.isEmpty(configValueValue)) {
            return Collections.emptyMap();
        }
        return JSONObject.parseObject(configValueValue, Map.class);
    }
}
