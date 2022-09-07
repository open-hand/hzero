package org.hzero.iam.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 安全组
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp")
@MultiLanguage
public class SecGrp extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_SEC_GRP_CODE = "secGrpCode";
    public static final String FIELD_SEC_GRP_NAME = "secGrpName";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_SEC_GRP_LEVEL = "secGrpLevel";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_DRAFT_FLAG = "draftFlag";
    public static final String FIELD_ROLE_ID = "roleId";

    private static final String ASSIGNED_FLAG = "parent";
    private static final String SELF_BUILD_FLAG = "self";
    private static final String CHILD_FLAG = "children";

    /**
     * 检查当前角色是否可操作此安全组
     *
     * @throws CommonException 无操作权限将抛出异常
     */
    public void checkOperable() {
        CustomUserDetails self = UserUtils.getUserDetails();

        // 检查是否属于当前租户的角色
        boolean siteLevel = self.getSiteRoleIds().contains(this.roleId);
        boolean tenantLevel = self.getTenantRoleIds().contains(this.roleId);

        if (!siteLevel && !tenantLevel) {
            throw new CommonException("hiam.error.secgrp.noAuthority");
        }
    }

    @ApiModelProperty(value = "层级，引用HIAM.SECURITY_GROUP_LEVEL", required = true)
    @NotBlank
    @LovValue(lovCode = "HIAM.SECURITY_GROUP_LEVEL", meaningField = "secGrpLevelMeaning")
    private String secGrpLevel;

    @JsonIgnore
    public boolean isParentAssigned() {
        return ASSIGNED_FLAG.equals(this.secGrpSource);
    }

    @JsonIgnore
    public boolean isSelfBuild() {
        return SELF_BUILD_FLAG.equals(this.secGrpSource);
    }

    @JsonIgnore
    public boolean isChildren() {
        return CHILD_FLAG.equalsIgnoreCase(this.secGrpSource);
    }

    public void setupEditable(Long currentRoleId) {
        this.editableFlag = Objects.equals(currentRoleId, this.roleId) ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
    }

    @JsonIgnore
    public boolean isEnabled(SecGrp secGrp) {
        return BaseConstants.Flag.NO.equals(this.getEnabledFlag()) && BaseConstants.Flag.YES.equals(secGrp.getEnabledFlag());
    }

    @JsonIgnore
    public boolean isDisabled(SecGrp secGrp) {
        return BaseConstants.Flag.YES.equals(this.getEnabledFlag()) && BaseConstants.Flag.NO.equals(secGrp.getEnabledFlag());
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long secGrpId;
    @ApiModelProperty(value = "安全组代码", required = true)
    @NotBlank
    private String secGrpCode;
    @ApiModelProperty(value = "安全组名称", required = true)
    @NotBlank
    @MultiLanguageField
    private String secGrpName;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "角色", required = true)
    @NotNull
    @Encrypt
    private Long roleId;

    public void setupCurrentRole(Long roleId) {
        CustomUserDetails self = UserUtils.getUserDetails();
        if (Objects.isNull(roleId)) {
            if (Objects.isNull(this.roleId)) {
                this.roleId = self.getRoleId();
            }
        } else {
            this.roleId = roleId;
        }

        // 检查是否属于当前租户的角色
        boolean siteLevel = self.getSiteRoleIds().contains(this.roleId);
        boolean tenantLevel = self.getTenantRoleIds().contains(this.roleId);

        if (siteLevel) {
            this.secGrpLevel = HiamResourceLevel.SITE.value();
        } else if (tenantLevel) {
            this.secGrpLevel = HiamResourceLevel.ORGANIZATION.value();
        }
    }

    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    @MultiLanguageField
    private String remark;
    @ApiModelProperty(value = "是否草稿状态，1草稿，0非草稿，草稿状态不可分配至角色")
    private Integer draftFlag;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    /**
     * 租户名称
     */
    @Transient
    private String tenantName;
    /**
     * 是否能编辑
     */
    @Transient
    private Integer editableFlag;

    /**
     * 安全组来源
     */
    @Transient
    private String secGrpSource;
    /**
     * 安全组层级含义
     */
    @Transient
    private String secGrpLevelMeaning;

    /**
     * 安全组创建角色
     */
    @Transient
    private String createRoleName;


    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    /**
     * @return 安全组代码
     */
    public String getSecGrpCode() {
        return secGrpCode;
    }

    public void setSecGrpCode(String secGrpCode) {
        this.secGrpCode = secGrpCode;
    }

    /**
     * @return 安全组名称
     */
    public String getSecGrpName() {
        return secGrpName;
    }

    public void setSecGrpName(String secGrpName) {
        this.secGrpName = secGrpName;
    }

    /**
     * @return 租户
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 层级，引用HIAM.SECURITY_GROUP_LEVEL
     */
    public String getSecGrpLevel() {
        return secGrpLevel;
    }

    public void setSecGrpLevel(String secGrpLevel) {
        this.secGrpLevel = secGrpLevel;
    }

    /**
     * @return 是否启用。1启用，0未启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public void setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
    }

    public Integer getDraftFlag() {
        return draftFlag;
    }

    public void setDraftFlag(Integer draftFlag) {
        this.draftFlag = draftFlag;
    }

    public String getSecGrpSource() {
        return secGrpSource;
    }

    public void setSecGrpSource(String secGrpSource) {
        this.secGrpSource = secGrpSource;
    }

    public String getSecGrpLevelMeaning() {
        return secGrpLevelMeaning;
    }

    public void setSecGrpLevelMeaning(String secGrpLevelMeaning) {
        this.secGrpLevelMeaning = secGrpLevelMeaning;
    }

    public String getCreateRoleName() {
        return createRoleName;
    }

    public void setCreateRoleName(String createRoleName) {
        this.createRoleName = createRoleName;
    }
}
