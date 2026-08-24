package org.hzero.platform.domain.entity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.vo.CodeRuleDetailVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 编码规则明细
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 13:43
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_code_rule_detail")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ApiModel("编码规则详情")
public class CodeRuleDetail extends AuditDomain {

    public static final String FIELD_RULE_DIST_ID = "ruleDistId";

    public interface SequenceGroup{}

    /**
     * 判断新增还是更新
     *
     * @return boolean
     */
    public boolean judgeInsert() {
        return ruleDetailId == null;
    }

    /**
     * 编码规则明细序号重新校验
     *
     * @param codeRuleDetailRepository 编码规则明细资源
     */
    public void validate(CodeRuleDetailRepository codeRuleDetailRepository) {
        List<CodeRuleDetail> codeRuleDetailList =
                codeRuleDetailRepository.select(CodeRuleDist.FIELD_RULE_DIST_ID, ruleDistId);
        if (CollectionUtils.isNotEmpty(codeRuleDetailList)) {
            codeRuleDetailList.forEach(detail -> {
                if (orderSeq.equals(detail.getOrderSeq())) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                if (fieldType.equals(detail.getFieldType()) && fieldType.equals(FndConstants.FieldType.SEQUENCE)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
                }
            });
        }
    }

    /**
     * 新增缓存
     *
     * @param redisHelper        redisHelper
     * @param key                key
     * @param codeRuleDetailList 需要存入的list
     */
    public static void initCache(RedisHelper redisHelper, String key, List<CodeRuleDetail> codeRuleDetailList) {
        redisHelper.hshPutAll(key,
                codeRuleDetailList
                        .stream()
                        .collect(HashMap::new,
                                (map, element) -> map.put(String.valueOf(element.getOrderSeq()),
                                        redisHelper.toJson(CommonConverter.beanConvert(CodeRuleDetailVO.class, element))), HashMap::putAll));
    }

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

    /**
     * 将json数据转换成list集合
     *
     * @param value  参数
     * @param tClass 泛型集合class
     * @param <T>    泛型
     * @return 泛型list
     */
    private static <T> List<T> generateListFromJson(Map<String, String> value, Class<T> tClass) {
        return value.values().stream().map(item -> {
            try {
                return RedisHelper.getObjectMapper().readValue(item, tClass);
            } catch (IOException e) {
                throw new CommonException(e);
            }
        }).collect(Collectors.toList());
    }

    // ===============================================================================
    // getter/setter
    // ===============================================================================

    @Id
    @GeneratedValue
    @ApiModelProperty("编码规则详情ID")
    @Encrypt
    private Long ruleDetailId;

    @NotNull
    @ApiModelProperty("编码规则分配ID")
    @Encrypt
    private Long ruleDistId;

    @NotNull
    @ApiModelProperty("序号")
    private Long orderSeq;

    @NotBlank
    @ApiModelProperty("段类型")
    @Length(max = 30)
    private String fieldType;

    @ApiModelProperty("段值")
    @Length(max = 240)
    private String fieldValue;

    @ApiModelProperty("日期格式")
    @Length(max = 30)
    private String dateMask;

    @Max(groups = SequenceGroup.class, value = 20)
    @ApiModelProperty("序列长度")
    private Long seqLength;

    @ApiModelProperty("开始值")
    private Long startValue;

    @ApiModelProperty("当前值")
    private Long currentValue;

    @ApiModelProperty("重置频率")
    private String resetFrequency;

    @ApiModelProperty("重置日期")
    private Date resetDate;

    @ApiModelProperty("加密标记")
    private Integer encryptedFlag;
    private Long tenantId;

    @Transient
    private DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey;
    @Transient
    private Integer enabledFlag;

    public Long getTenantId() {
        return tenantId;
    }

    public CodeRuleDetail setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

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

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    public void setRuleDetailId(Long ruleDetailId) {
        this.ruleDetailId = ruleDetailId;
    }

    public CodeRuleDetail setRuleDistId(Long ruleDistId) {
        this.ruleDistId = ruleDistId;
        return this;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setDateMask(String dataMask) {
        this.dateMask = dataMask;
    }

    public void setSeqLength(Long seqLength) {
        this.seqLength = seqLength;
    }

    public void setStartValue(Long startValue) {
        this.startValue = startValue;
    }

    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }

    public void setResetFrequency(String resetFrequency) {
        this.resetFrequency = resetFrequency;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate;
    }

    public DefaultCodeRuleBuilder.CodeRuleKey getCodeRuleKey() {
        return codeRuleKey;
    }

    public CodeRuleDetail setCodeRuleKey(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey) {
        this.codeRuleKey = codeRuleKey;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public CodeRuleDetail setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Integer getEncryptedFlag() {
        return encryptedFlag;
    }

    public CodeRuleDetail setEncryptedFlag(Integer encryptedFlag) {
        this.encryptedFlag = encryptedFlag;
        return this;
    }
}
