package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色数据权限行定义
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:51:40
 */
@ApiModel("角色数据权限行定义")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_role_authority_line")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleAuthorityLine extends AuditDomain {
	public static final String ENCRYPT_KEY = "hiam_role_authority_line";
	public static final String ROLE_AUTHORITY_LINE_NOT_NULL = "error.roleAuthorityLine.info.not.null";
	public static final String AUTH_DATA_ID_REQUIRED = "error.roleAuthorityLine.authDataId.required";
	public static final String DATA_ID_REQUIRED = "error.roleAuthorityLine.dataId.required";
	public static final String TENANT_ID_REQUIRED = "error.roleAuthorityLine.tenantId.required";

	public static final String FIELD_ROLE_AUTH_LINE_ID = "roleAuthLineId";
	public static final String FIELD_ROLE_AUTH_ID = "roleAuthId";
	public static final String FIELD_ROLE_ID = "roleId";
	public static final String FIELD_AUTH_TYPE_CODE = "authTypeCode";
	public static final String FIELD_DATA_SOURCE = "dataSource";

	public interface Insert{}

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	public RoleAuthorityLine(){}

	public RoleAuthorityLine(Long roleId, String authTypeCode) {
		this.roleId = roleId;
		this.authTypeCode = authTypeCode;
	}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
	@Encrypt
    private Long roleAuthLineId;
    @ApiModelProperty(value = "角色数据权限ID，hiam_role_authority.role_auth_id",required = true)
    @NotNull
	@Encrypt
    private Long roleAuthId;
    @ApiModelProperty(value = "角色ID，iam_role.id",required = true)
    @NotNull
	@Encrypt
    private Long roleId;
    @ApiModelProperty(value = "权限类型代码",required = true)
	@NotBlank
    private String authTypeCode;
    private String dataSource;

	@NotNull(groups = Insert.class)
	@ApiModelProperty("租户ID")
	private Long tenantId;

	//
	// 非数据库字段
	// 以下字段为docTypeAuthDim的字段，docTypeAuthDim与UserAuthorityLine是一对一关系
	// ------------------------------------------------------------------------------
	@Transient
	@Encrypt
	private Long authDimId;
	@Transient
	private String sourceMatchField;
	@Transient
	private Long enabledFlag;
	@Transient
	@Encrypt
	private Long docTypeId;
	@Transient
	private String authTypeMeaning;
	@Transient
	private String dimensionType;

	//
	// getter/setter
	// ------------------------------------------------------------------------------

	public Long getTenantId() {
		return tenantId;
	}

	public RoleAuthorityLine setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	public String getDataSource() {
		return dataSource;
	}

	public RoleAuthorityLine setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public String getAuthTypeMeaning() {
		return authTypeMeaning;
	}

	public void setAuthTypeMeaning(String authTypeMeaning) {
		this.authTypeMeaning = authTypeMeaning;
	}

	public Long getAuthDimId() {
		return authDimId;
	}

	public void setAuthDimId(Long authDimId) {
		this.authDimId = authDimId;
	}

	public String getSourceMatchField() {
		return sourceMatchField;
	}

	public void setSourceMatchField(String sourceMatchField) {
		this.sourceMatchField = sourceMatchField;
	}

	public Long getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(Long enabledFlag) {
		this.enabledFlag = enabledFlag;
	}

	public Long getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
	}

	/**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getRoleAuthLineId() {
		return roleAuthLineId;
	}

	public void setRoleAuthLineId(Long roleAuthLineId) {
		this.roleAuthLineId = roleAuthLineId;
	}
    /**
     * @return 角色数据权限ID，hiam_role_authority.role_auth_id
     */
	public Long getRoleAuthId() {
		return roleAuthId;
	}

	public RoleAuthorityLine setRoleAuthId(Long roleAuthId) {
		this.roleAuthId = roleAuthId;
		return this;
	}
    /**
     * @return 角色ID，iam_role.id
     */
	public Long getRoleId() {
		return roleId;
	}

	public RoleAuthorityLine setRoleId(Long roleId) {
		this.roleId = roleId;
		return this;
	}
    /**
     * @return 权限类型代码，HIAM.AUTHORITY_TYPE_CODE
     */
	public String getAuthTypeCode() {
		return authTypeCode;
	}

	public RoleAuthorityLine setAuthTypeCode(String authTypeCode) {
		this.authTypeCode = authTypeCode;
		return this;
	}

	/**
	 * @return 维度类型
	 */
	public String getDimensionType() {
		return dimensionType;
	}

	public void setDimensionType(String dimensionType) {
		this.dimensionType = dimensionType;
	}

	/**
	 * 包含默认数据权限
	 *
	 * @return true: 包含
	 */
	public boolean containDefaultDataSource() {
		return StringUtils.contains(dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
	}

	/**
	 * 包含安全组的数据权限
	 *
	 * @return true:包含
	 */
	public boolean containSecGrpDataSource() {
		return StringUtils.contains(dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
	}

	/**
	 * 等于安全组的数据权限
	 *
	 * @return true:相等
	 */
	public boolean equalSecGrpDataSource() {
		return StringUtils.equals(dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
	}


	/**
	 * 等于默认的数据权限
	 *
	 * @return true:相等
	 */
	public boolean equalDefaultDataSource() {
		return StringUtils.equals(dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
	}


	/**
	 * 等于默认和安全组的数据权限
	 *
	 * @return true:相等
	 */
	public boolean equalDefaultSecGrpDataSource() {
		return StringUtils.equals(dataSource, Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
	}

}
