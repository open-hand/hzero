package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户默认配置
 *
 * @author zhiying.dong@hand-china.com 2018-09-14 10:46:53
 */
@ApiModel("用户默认配置")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_user_config")
public class UserConfig extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_user_config";
    public static final String FIELD_USER_CONFIG_ID = "userConfigId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DEFAULT_COMPANY_ID = "defaultCompanyId";
    public static final String FIELD_DEFAULT_ROLE_ID = "defaultRoleId";
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long userConfigId;
    @ApiModelProperty(value = "用户ID，iam_user.id", required = true)
    @NotNull
    @Encrypt
    private Long userId;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "默认公司ID，hpfm_company.company_id")
    @Encrypt
    private Long defaultCompanyId;
    @ApiModelProperty(value = "默认角色ID，iam_role.id")
    @Encrypt
    private Long defaultRoleId;
    @ApiModelProperty(value = "菜单布局，包含左右布局和水平布局两种")
    private String menuLayout;
    @ApiModelProperty(value = "菜单布局主题")
    private String menuLayoutTheme;
    @ApiModelProperty(value = "角色合并标识")
    private Integer roleMergeFlag;
    @ApiModelProperty(value = "弹框提醒标识")
    private Integer popoutReminderFlag;
    @Transient
    private String userName;
    @Transient
    private String tenantName;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String companyName;
    @Transient
    private String roleName;
    public UserConfig() {
    }
    public UserConfig(Long userId, Long tenantId) {
        this.userId = userId;
        this.tenantId = tenantId;
    }
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Integer getRoleMergeFlag() {
        return roleMergeFlag;
    }

    public void setRoleMergeFlag(Integer roleMergeFlag) {
        this.roleMergeFlag = roleMergeFlag;
    }

    public String getMenuLayout() {
        return menuLayout;
    }

    public void setMenuLayout(String menuLayout) {
        this.menuLayout = menuLayout;
    }

    public String getMenuLayoutTheme() {
        return menuLayoutTheme;
    }

    public void setMenuLayoutTheme(String menuLayoutTheme) {
        this.menuLayoutTheme = menuLayoutTheme;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getUserConfigId() {
        return userConfigId;
    }

    public void setUserConfigId(Long userConfigId) {
        this.userConfigId = userConfigId;
    }

    /**
     * @return 用户ID，iam_user.id
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 租户ID，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 默认公司ID，hpfm_company.company_id
     */
    public Long getDefaultCompanyId() {
        return defaultCompanyId;
    }

    public void setDefaultCompanyId(Long defaultCompanyId) {
        this.defaultCompanyId = defaultCompanyId;
    }

    /**
     * @return 默认角色ID，iam_role.id
     */
    public Long getDefaultRoleId() {
        return defaultRoleId;
    }

    public void setDefaultRoleId(Long defaultRoleId) {
        this.defaultRoleId = defaultRoleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getPopoutReminderFlag() {
        return popoutReminderFlag;
    }

    public void setPopoutReminderFlag(Integer popoutReminderFlag) {
        this.popoutReminderFlag = popoutReminderFlag;
    }
}
