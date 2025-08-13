package org.hzero.boot.platform.code.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.core.redis.RedisHelper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 编码规则明细
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 13:43
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeRuleDetail {
    public static final String RULE_DIST_ID = "ruleDistId";

    /**
     * 获取编码规则的list缓存
     *
     * @param redisHelper redisHelper
     * @param key         key
     * @return list
     */
    public static List<CodeRuleDetail> getCodeRuleListFromCache(RedisHelper redisHelper, String key) {
        Map<String, String> value = redisHelper.hshGetAll(key);
        if (value == null) {
            return new ArrayList<>();
        }
        return generateListFromJson(value, CodeRuleDetail.class);
    }

    private static <T> List<T> generateListFromJson(Map<String, String> value, Class<T> tClass) {
        return value.values().stream().map(item -> {
            try {
                return RedisHelper.getObjectMapper().readValue(item, tClass);
            } catch (IOException e) {
                throw new CommonException(e);
            }
        }).collect(Collectors.toList());
    }

    private Date creationDate;
    private Long createdBy;
    private Date lastUpdateDate;
    private Long lastUpdatedBy;
    private Long objectVersionNumber;

    // ===============================================================================
    // getter/setter
    // ===============================================================================

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

    private DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey;

    /**
     * @return 规则详细id
     */
    public Long getRuleDetailId() {
        return ruleDetailId;
    }

    /**
     * @return 规则分配id
     */
    public Long getRuleDistId() {
        return ruleDistId;
    }

    /**
     * @return 序号，决定编码规则生成的顺序
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    /**
     * @return 段类型
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * @return 段值
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @return 日期格式
     */
    public String getDateMask() {
        return dateMask;
    }

    /**
     * @return 位数
     */
    public Long getSeqLength() {
        return seqLength;
    }

    /**
     * @return '开始值
     */
    public Long getStartValue() {
        return startValue;
    }

    /**
     * @return 当前值
     */
    public Long getCurrentValue() {
        return currentValue;
    }

    /**
     * @return 重置频率
     */
    public String getResetFrequency() {
        return resetFrequency;
    }

    /**
     * @return 上次重置日期
     */
    public Date getResetDate() {
        return resetDate;
    }


    public CodeRuleDetail setRuleDetailId(Long ruleDetailId) {
        this.ruleDetailId = ruleDetailId;
        return this;
    }

    public CodeRuleDetail setRuleDistId(Long ruleDistId) {
        this.ruleDistId = ruleDistId;
        return this;
    }

    public CodeRuleDetail setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public CodeRuleDetail setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public CodeRuleDetail setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
        return this;
    }

    public CodeRuleDetail setDateMask(String dateMask) {
        this.dateMask = dateMask;
        return this;
    }

    public CodeRuleDetail setSeqLength(Long seqLength) {
        this.seqLength = seqLength;
        return this;
    }

    public CodeRuleDetail setStartValue(Long startValue) {
        this.startValue = startValue;
        return this;
    }

    public CodeRuleDetail setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
        return this;
    }

    public CodeRuleDetail setResetFrequency(String resetFrequency) {
        this.resetFrequency = resetFrequency;
        return this;
    }

    public CodeRuleDetail setResetDate(Date resetDate) {
        this.resetDate = resetDate;
        return this;
    }

    public DefaultCodeRuleBuilder.CodeRuleKey getCodeRuleKey() {
        return codeRuleKey;
    }

    public CodeRuleDetail setCodeRuleKey(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey) {
        this.codeRuleKey = codeRuleKey;
        return this;
    }

    public Integer getEncryptedFlag() {
        return encryptedFlag;
    }

    public CodeRuleDetail setEncryptedFlag(Integer encryptedFlag) {
        this.encryptedFlag = encryptedFlag;
        return this;
    }

    @JsonIgnore
    public Date getCreationDate() {
        return this.creationDate;
    }

    @JsonIgnore
    public Long getCreatedBy() {
        return this.createdBy;
    }

    @JsonIgnore
    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    @JsonIgnore
    public Long getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    @JsonIgnore
    public Long getObjectVersionNumber() {
        return this.objectVersionNumber;
    }
}
