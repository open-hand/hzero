package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.infra.constant.Constants;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色数据权限定义
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
@ApiModel("角色数据权限定义")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_role_authority")
public class RoleAuthority extends AuditDomain {
	public static final String ENCRYPT_KEY = "hiam_role_authority";
	public static final String FIELD_ROLE_AUTH_ID = "roleAuthId";
	public static final String FIELD_ROLE_ID = "roleId";
	public static final String FIELD_AUTH_DOC_TYPE_ID = "authDocTypeId";
	public static final String FIELD_AUTH_SCOPE_CODE = "authScopeCode";
	public static final String FIELD_MSG_FLAG = "msgFlag";
	public static final String FIELD_DATA_SOURCE = "dataSource";

	public interface Insert {
	}

	//
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	public RoleAuthority() {}

	public RoleAuthority(Long roleId,Long authDocTypeId) {
		this.roleId = roleId;
		this.authDocTypeId = authDocTypeId;
	}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long roleAuthId;
    @ApiModelProperty(value = "角色ID，IAM_ROLE.ID",required = true)
    @NotNull
    private Long roleId;
    @ApiModelProperty(value = "单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID",required = true)
    @NotNull
    private Long authDocTypeId;
    private String authScopeCode;
    @ApiModelProperty(value = "消息发送标识",required = true)
    @NotNull
    private Integer msgFlag;
    private String dataSource;
    @NotNull(groups = Insert.class)
	@ApiModelProperty("租户ID")
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

	public RoleAuthority setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	/**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getRoleAuthId() {
		return roleAuthId;
	}

	public void setRoleAuthId(Long roleAuthId) {
		this.roleAuthId = roleAuthId;
	}

	public String getDataSource() {
		return dataSource;
	}

	public RoleAuthority setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	/**
     * @return 角色ID，IAM_ROLE.ID
     */
	public Long getRoleId() {
		return roleId;
	}

	public RoleAuthority setRoleId(Long roleId) {
		this.roleId = roleId;
		return this;
	}
    /**
     * @return 单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID
     */
	public Long getAuthDocTypeId() {
		return authDocTypeId;
	}

	public RoleAuthority setAuthDocTypeId(Long authDocTypeId) {
		this.authDocTypeId = authDocTypeId;
		return this;
	}
    /**
     * @return 权限限制范围，HIAM.AUTHORITY_SCOPE_CODE
     */
	public String getAuthScopeCode() {
		return authScopeCode;
	}

	public RoleAuthority setAuthScopeCode(String authScopeCode) {
		this.authScopeCode = authScopeCode;
		return this;
	}
    /**
     * @return 消息发送标识
     */
	public Integer getMsgFlag() {
		return msgFlag;
	}

	public RoleAuthority setMsgFlag(Integer msgFlag) {
		this.msgFlag = msgFlag;
		return this;
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


}
