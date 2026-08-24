package org.hzero.platform.api.dto;

import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.CodeRuleDetail;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * 编码规则明细DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/02 16:34
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeRuleDetailDTO extends CodeRuleDetail {


    private String fieldTypeDescription;

    private String resetFrequencyDescription;


    @Override
    public String toString() {
        return "CodeRuleDetailDTO{" +
                ", fieldTypeDescription='" + fieldTypeDescription + '\'' +
                ", resetFrequencyDescription='" + resetFrequencyDescription + '\'' +
                '}';
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    public String getFieldTypeDescription() {
        return fieldTypeDescription;
    }

    public void setFieldTypeDescription(String fieldTypeDescription) {
        this.fieldTypeDescription = fieldTypeDescription;
    }

    public String getResetFrequencyDescription() {
        return resetFrequencyDescription;
    }

    public void setResetFrequencyDescription(String resetFrequencyDescription) {
        this.resetFrequencyDescription = resetFrequencyDescription;
    }

}
