package org.hzero.platform.domain.vo;

import java.util.Date;

/**
 * @author qingsheng.chen@hand-china.com 2019-05-29 10:48
 */
public class CodeRuleDetailVO {
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

    public CodeRuleDetailVO setRuleDetailId(Long ruleDetailId) {
        this.ruleDetailId = ruleDetailId;
        return this;
    }

    public Long getRuleDistId() {
        return ruleDistId;
    }

    public CodeRuleDetailVO setRuleDistId(Long ruleDistId) {
        this.ruleDistId = ruleDistId;
        return this;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public CodeRuleDetailVO setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getFieldType() {
        return fieldType;
    }

    public CodeRuleDetailVO setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public CodeRuleDetailVO setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
        return this;
    }

    public String getDateMask() {
        return dateMask;
    }

    public CodeRuleDetailVO setDateMask(String dateMask) {
        this.dateMask = dateMask;
        return this;
    }

    public Long getSeqLength() {
        return seqLength;
    }

    public CodeRuleDetailVO setSeqLength(Long seqLength) {
        this.seqLength = seqLength;
        return this;
    }

    public Long getStartValue() {
        return startValue;
    }

    public CodeRuleDetailVO setStartValue(Long startValue) {
        this.startValue = startValue;
        return this;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public CodeRuleDetailVO setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public String getResetFrequency() {
        return resetFrequency;
    }

    public CodeRuleDetailVO setResetFrequency(String resetFrequency) {
        this.resetFrequency = resetFrequency;
        return this;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public CodeRuleDetailVO setResetDate(Date resetDate) {
        this.resetDate = resetDate;
        return this;
    }

    public Integer getEncryptedFlag() {
        return encryptedFlag;
    }

    public CodeRuleDetailVO setEncryptedFlag(Integer encryptedFlag) {
        this.encryptedFlag = encryptedFlag;
        return this;
    }
}
