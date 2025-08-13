package org.hzero.platform.domain.entity;

import java.util.List;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.collections4.CollectionUtils;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hibernate.validator.constraints.Range;
import org.hzero.platform.api.dto.PermissionRelDTO;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.platform.domain.repository.PermissionRuleRepository;
import org.hzero.platform.infra.constant.FndConstants;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 屏蔽范围规则关系
 *
 * @author yunxiang.zhou01@hand-china.com 2018-08-29 15:19:45
 */
@ApiModel("屏蔽范围规则关系")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_permission_rel")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionRel extends AuditDomain {

    public static final String FIELD_PERMISSION_REL_ID = "permissionRelId";
    public static final String FIELD_RANGE_ID = "rangeId";
    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_EDITABLE_FLAG = "editableFlag";


    public interface Insert{}

    /**
     * 校验数据合法性
     * 
     * @param permissionRelRepository permissionRelRepository
     * @param permissionRuleRepository permissionRuleRepository
     */
    public void judgeDataLegality(PermissionRelRepository permissionRelRepository,
                    PermissionRuleRepository permissionRuleRepository) {
        if (rangeId != null && ruleId != null) {
            List<PermissionRelDTO> permissionRelDTOList =
                            permissionRelRepository.selectPermissionRuleByRangeId(rangeId);
            PermissionRule permissionRule = permissionRuleRepository.selectByPrimaryKey(ruleId);
            if (CollectionUtils.isNotEmpty(permissionRelDTOList)) {
                permissionRelDTOList.forEach(permissionRelDTO -> {
                    if (Objects.equals(permissionRelDTO.getRuleTypeCode(), permissionRule.getRuleTypeCode())
                                    && !FndConstants.PermissionRuleTypeCode.SQL
                                                    .equals(permissionRule.getRuleTypeCode())) {
                        throw new CommonException(HpfmMsgCodeConstants.ERROR_PERMISSION_REF_RULE_TYPE_CODE_REPEAT);
                    }
                });
            }
        }
    }

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long permissionRelId;
    @ApiModelProperty(value = "范围ID，hpfm_permission_range.range_id")
    @NotNull
    @Encrypt
    private Long rangeId;
    @ApiModelProperty(value = "屏蔽规则id，hpfm_permission_rule.rule_id")
    @NotNull
    @Encrypt
    private Long ruleId;
    @ApiModelProperty("编辑标识")
    @Range(max = 1)
    private Integer editableFlag;

    @NotNull(groups = Insert.class)
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getPermissionRelId() {
        return permissionRelId;
    }

    public void setPermissionRelId(Long permissionRelId) {
        this.permissionRelId = permissionRelId;
    }

    /**
     * @return 范围ID，hpfm_permission_range.range_id
     */
    public Long getRangeId() {
        return rangeId;
    }

    public PermissionRel setRangeId(Long rangeId) {
        this.rangeId = rangeId;
        return this;
    }

    /**
     * @return 屏蔽规则id，hpfm_permission_rule.rule_id
     */
    public Long getRuleId() {
        return ruleId;
    }

    public PermissionRel setRuleId(Long ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public PermissionRel setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
    }
}
