package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 用户权限管理行表
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
@VersionAudit
@ModifyAudit
@Table(name = "hiam_user_authority_line")
public class UserAuthorityLine extends AuditDomain implements Cloneable {

	public static final String ENCRYPT_KEY = "hiam_user_authority_line";

	public static final String USER_AUTHORITY_LINE_NOT_NULL = "error.userAuthorityLine.info.not.null";
	public static final String AUTHORITY_ID_REQUIRED = "error.userAuthorityLine.authorityId.required";
	public static final String DATA_ID_REQUIRED = "error.userAuthorityLine.dataId.required";

	public static final String FIELD_AUTHORITY_LINE_ID = "authorityLineId";
	public static final String FIELD_AUTHORITY_ID = "authorityId";
	public static final String FIELD_TENANT_ID = "tenantId";
	public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_DATA_CODE = "dataCode";
    public static final String FIELD_DATA_NAME = "dataName";
    public static final String FIELD_DATA_SOURCE = "dataSource";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
	@Encrypt
    private Long authorityLineId;
    @NotNull
	@Encrypt
    private Long authorityId;
    @NotNull
    private Long tenantId;
    @NotNull
	@Encrypt
    private Long dataId;
    private String dataCode;
    private String dataName;
    private String dataSource;

	public UserAuthorityLine() {
	}

	public UserAuthorityLine(Long authorityId,Long dataId) {
		this.authorityId = authorityId;
		this.dataId = dataId;
	}

	public UserAuthorityLine(Long authorityId, Long tenantId, Long dataId, String dataCode, String dataName, String dataSource) {
		this.authorityId = authorityId;
		this.tenantId = tenantId;
		this.dataId = dataId;
		this.dataCode = dataCode;
		this.dataName = dataName;
		this.dataSource = dataSource;
	}


	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

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
	public Long getAuthorityLineId() {
		return authorityLineId;
	}

	public void setAuthorityLineId(Long authorityLineId) {
		this.authorityLineId = authorityLineId;
	}
    /**
     * @return 权限ID，HIAM_USER_AUTHORITY.AUTHORITY_ID
     */
	public Long getAuthorityId() {
		return authorityId;
	}

	public UserAuthorityLine setAuthorityId(Long authorityId) {
		this.authorityId = authorityId;
		return this;
	}

	/**
     * @return 租户ID，HPFM.HPFM_TENANT
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
    /**
     * @return 数据ID
     */
	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
    /**
     * @return 数据代码/编码
     */
	public String getDataCode() {
		return dataCode;
	}

	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}
    /**
     * @return 数据名称
     */
	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
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

	@Override
	public UserAuthorityLine clone() {
		UserAuthorityLine userAuthorityLine = null;
		try {
			userAuthorityLine = (UserAuthorityLine) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new CommonException("Clone Failure: " + UserAuthorityLine.class.getName());
		}

		return userAuthorityLine;
	}

	@Override
	public String toString() {
		return "UserAuthorityLine{" +
				"authorityLineId=" + authorityLineId +
				", authorityId=" + authorityId +
				", tenantId=" + tenantId +
				", dataId=" + dataId +
				", dataCode='" + dataCode + '\'' +
				", dataName='" + dataName + '\'' +
				", dataSource='" + dataSource + '\'' +
				'}';
	}
}
