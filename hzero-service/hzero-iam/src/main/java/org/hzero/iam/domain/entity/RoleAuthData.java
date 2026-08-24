package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色单据权限管理
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@ApiModel("角色单据权限管理")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_role_auth_data")
public class RoleAuthData extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_role_auth_data";

    public static final String ROLE_AUTH_DATA_NOT_NULL = "error.roleAuthData.info.not.null";
    public static final String ROLE_ID_REQUIRED = "error.roleAuthData.roleId.required";
    public static final String TENATN_ID_REQUIRED = "error.roleAuthData.tenantId.required";
    public static final String AUTHORITY_TYPE_CODE_REQUIRED = "error.roleAuthData.authorityTypeCode.required";

    public static final String FIELD_AUTH_DATA_ID = "authDataId";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_AUTHORITY_TYPE_CODE = "authorityTypeCode";
    public static final String FIELD_INCLUDE_ALL_FLAG = "includeAllFlag";
    public static final String FIELD_DATASOURCE = "dataSource";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------



    public RoleAuthData() {
    }

    public RoleAuthData(Long roleId, Long tenantId, String authorityTypeCode) {
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.authorityTypeCode = authorityTypeCode;
    }

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long authDataId;
    @ApiModelProperty(value = "用户ID，iam_role.role_id", required = true)
    @NotNull
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "租户ID，HPFM.HPFM_TENANT", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "权限类型代码，HIAM.AUTHORITY_TYPE_CODE", required = true)
    @NotBlank
    @Length(max = 30)
    private String authorityTypeCode;
    @ApiModelProperty(value = "是否包含所有标识", required = true)
    @NotNull
    @Range(max = 1)
    private Integer includeAllFlag;

    private String dataSource;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String roleCode;
    @Transient
    private String roleName;
    @Transient
    private String parentRoleName;
    @Transient
    private String tenantName;
    @Transient
    private Long organizationId;
    @Transient
    private Long userId;
    @Transient
    private Long roleAuthLineId;


    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getAuthDataId() {
        return authDataId;
    }

    public RoleAuthData setAuthDataId(Long authDataId) {
        this.authDataId = authDataId;
        return this;
    }

    /**
     * @return 用户ID，iam_role.role_id
     */
    public Long getRoleId() {
        return roleId;
    }

    public RoleAuthData setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    /**
     * @return 租户ID，HPFM.HPFM_TENANT
     */
    public Long getTenantId() {
        return tenantId;
    }

    public RoleAuthData setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 权限类型代码，HIAM.AUTHORITY_TYPE_CODE
     */
    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public RoleAuthData setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
        return this;
    }

    /**
     * @return 是否包含所有标识
     */
    public Integer getIncludeAllFlag() {
        return includeAllFlag;
    }

    public RoleAuthData setIncludeAllFlag(Integer includeAllFlag) {
        this.includeAllFlag = includeAllFlag;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getParentRoleName() {
        return parentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    /**
     * 包含默认数据权限
     *
     * @return true: 包含
     */
    public boolean containDefaultDataSource() {
        return StringUtils.contains(this.dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }

    /**
     * 包含安全组的数据权限
     *
     * @return true:包含
     */
    public boolean containSecGrpDataSource() {
        return StringUtils.contains(this.dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }

    /**
     * 等于安全组的数据权限
     *
     * @return true:相等
     */
    public boolean equalSecGrpDataSource() {
        return StringUtils.equals(this.dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }


    /**
     * 等于默认的数据权限
     *
     * @return true:相等
     */
    public boolean equalDefaultDataSource() {
        return StringUtils.equals(this.dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }


    /**
     * 等于默认和安全组的数据权限
     *
     * @return true:相等
     */
    public boolean equalDefaultSecGrpDataSource() {
        return StringUtils.equals(this.dataSource, Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
    }

    public Long getRoleAuthLineId() {
        return roleAuthLineId;
    }

    public void setRoleAuthLineId(Long roleAuthLineId) {
        this.roleAuthLineId = roleAuthLineId;
    }
}
