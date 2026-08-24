package org.hzero.boot.platform.code.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * 减少序列化字段
 * </p>
 *
 * @author qingsheng.chen 2019/4/12 星期五 14:09
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleCodeRuleDetail {
    private Long ruleDetailId;

    private Long ruleDistId;

    private Long orderSeq;

    private String fieldType;

    private String fieldValue;

    private String dateMask;

    private Long seqLength;

    private Long startValue;

    private Long currentValue;

    private String resetFrequency;

    private Date resetDate;

    private Integer encryptedFlag;

    public Long getRuleDetailId() {
        return ruleDetailId;
    }

    public SimpleCodeRuleDetail setRuleDetailId(Long ruleDetailId) {
        this.ruleDetailId = ruleDetailId;
        return this;
    }

    public Long getRuleDistId() {
        return ruleDistId;
    }

    public SimpleCodeRuleDetail setRuleDistId(Long ruleDistId) {
        this.ruleDistId = ruleDistId;
        return this;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public SimpleCodeRuleDetail setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getFieldType() {
        return fieldType;
    }

    public SimpleCodeRuleDetail setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public SimpleCodeRuleDetail setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
        return this;
    }

    public String getDateMask() {
        return dateMask;
    }

    public SimpleCodeRuleDetail setDateMask(String dateMask) {
        this.dateMask = dateMask;
        return this;
    }

    public Long getSeqLength() {
        return seqLength;
    }

    public SimpleCodeRuleDetail setSeqLength(Long seqLength) {
        this.seqLength = seqLength;
        return this;
    }

    public Long getStartValue() {
        return startValue;
    }

    public SimpleCodeRuleDetail setStartValue(Long startValue) {
        this.startValue = startValue;
        return this;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public SimpleCodeRuleDetail setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public String getResetFrequency() {
        return resetFrequency;
    }

    public SimpleCodeRuleDetail setResetFrequency(String resetFrequency) {
        this.resetFrequency = resetFrequency;
        return this;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public SimpleCodeRuleDetail setResetDate(Date resetDate) {
        this.resetDate = resetDate;
        return this;
    }

    public Integer getEncryptedFlag() {
        return encryptedFlag;
    }

    public SimpleCodeRuleDetail setEncryptedFlag(Integer encryptedFlag) {
        this.encryptedFlag = encryptedFlag;
        return this;
    }
}
