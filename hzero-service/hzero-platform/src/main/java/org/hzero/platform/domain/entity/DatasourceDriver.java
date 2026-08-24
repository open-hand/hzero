package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据源驱动配置
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
@ApiModel("数据源驱动配置")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@MultiLanguage
@Table(name = "hpfm_datasource_driver")
public class DatasourceDriver extends AuditDomain {

    public static final String FIELD_DRIVER_ID = "driverId";
    public static final String FIELD_DRIVER_NAME = "driverName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DATABASE_TYPE = "databaseType";
    public static final String FIELD_DRIVER_VERSION = "driverVersion";
    public static final String FIELD_DRIVER_PATH = "driverPath";
    public static final String FIELD_MAIN_CLASS = "mainClass";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String MAIN_CLASS_LOV_CODE = "HPFM.DATASOURCE_DRIVER_MAINCLASS";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
	@Encrypt
    private Long driverId;
    @ApiModelProperty(value = "驱动名称")
    @NotBlank
	@Length(max = 255)
	@MultiLanguageField
    private String driverName;
    @ApiModelProperty(value = "驱动描述")
	@Length(max = 480)
    private String description;
    @ApiModelProperty(value = "数据库类型，值集:HPFM.DATABASE_TYPE")
    @NotBlank
	@LovValue(lovCode = "HPFM.DATABASE_TYPE", meaningField = "databaseTypeMeaning")
    private String databaseType;
    @ApiModelProperty(value = "驱动版本号")
    @NotBlank
	@Pattern(regexp = Regexs.CODE)
	@Length(max = 50)
    private String driverVersion;
    @ApiModelProperty(value = "驱动包路径")
    @NotBlank
    private String driverPath;
    @ApiModelProperty(value = "主类入口")
	@Length(max = 255)
    private String mainClass;
    @ApiModelProperty(value = "是否启用，1启用、0禁用")
    @NotNull
	@Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
	@MultiLanguageField
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
	@Transient
	@ApiModelProperty("数据库类型")
	private String databaseTypeMeaning;
	@Transient
	@ApiModelProperty("租户名称")
	private String tenantName;
	@Transient
	@ApiModelProperty("文件名称")
	private String fileName;
	@Transient
	@ApiModelProperty("租户查询标识")
	private Boolean orgQueryFlag;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

	public Boolean getOrgQueryFlag() {
		return orgQueryFlag;
	}

	public void setOrgQueryFlag(Boolean orgQueryFlag) {
		this.orgQueryFlag = orgQueryFlag;
	}

	public String getFileName() {
		if (driverPath != null) {
			return FileUtils.getFileName(driverPath);
		}
		return null;
	}

	public DatasourceDriver setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getDatabaseTypeMeaning() {
		return databaseTypeMeaning;
	}

	public DatasourceDriver setDatabaseTypeMeaning(String databaseTypeMeaning) {
		this.databaseTypeMeaning = databaseTypeMeaning;
		return this;
	}

	public String getTenantName() {
		return tenantName;
	}

	public DatasourceDriver setTenantName(String tenantName) {
		this.tenantName = tenantName;
		return this;
	}

	/**
     * @return 
     */
	public Long getDriverId() {
		return driverId;
	}

	public DatasourceDriver setDriverId(Long driverId) {
		this.driverId = driverId;
		return this;
	}
    /**
     * @return 驱动名称
     */
	public String getDriverName() {
		return driverName;
	}

	public DatasourceDriver setDriverName(String driverName) {
		this.driverName = driverName;
		return this;
	}
    /**
     * @return 驱动描述
     */
	public String getDescription() {
		return description;
	}

	public DatasourceDriver setDescription(String description) {
		this.description = description;
		return this;
	}
    /**
     * @return 数据库类型，值集:HPFM.DATABASE_TYPE
     */
	public String getDatabaseType() {
		return databaseType;
	}

	public DatasourceDriver setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
		return this;
	}
    /**
     * @return 驱动版本号
     */
	public String getDriverVersion() {
		return driverVersion;
	}

	public DatasourceDriver setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
		return this;
	}
    /**
     * @return 驱动包路径
     */
	public String getDriverPath() {
		return driverPath;
	}

	public DatasourceDriver setDriverPath(String driverPath) {
		this.driverPath = driverPath;
		return this;
	}
    /**
     * @return 主类入口
     */
	public String getMainClass() {
		return mainClass;
	}

	public DatasourceDriver setMainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}
    /**
     * @return 是否启用，1启用、0禁用
     */
	public Integer getEnabledFlag() {
		return enabledFlag;
	}

	public DatasourceDriver setEnabledFlag(Integer enabledFlag) {
		this.enabledFlag = enabledFlag;
		return this;
	}
    /**
     * @return 租户ID,hpfm_tenant.tenant_id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public DatasourceDriver setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

}
