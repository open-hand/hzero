package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色单据权限管理行
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@ApiModel("角色单据权限管理行")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_role_auth_data_line")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleAuthDataLine extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_role_auth_data_line";
    public static final String ENCRYPT_KEY_DATA_ID = "data_id";

    public static final String FIELD_AUTH_DATA_LINE_ID = "authDataLineId";
    public static final String FIELD_AUTH_DATA_ID = "authDataId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_DATA_CODE = "dataCode";
    public static final String FIELD_DATA_NAME = "dataName";

    public RoleAuthDataLine(){}

    public  RoleAuthDataLine(Long authDataId, Long dataId) {
        this.authDataId = authDataId;
        this.dataId = dataId;
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
    private Long authDataLineId;
    @ApiModelProperty(value = "权限ID，hiam_role_auth_data.auth_data_id", required = true)
    @NotNull
    @Encrypt
    private Long authDataId;
    @ApiModelProperty(value = "租户ID，HPFM.HPFM_TENANT", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "数据ID", required = true)
    @NotNull
    @Encrypt
    private Long dataId;
    @ApiModelProperty(value = "数据代码/编码")
    @Length(max = 80)
    private String dataCode;
    @ApiModelProperty(value = "数据名称")
    @Length(max = 360)
    private String dataName;

    private String dataSource;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String tenantName;
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
    public Long getAuthDataLineId() {
        return authDataLineId;
    }

    public RoleAuthDataLine setAuthDataLineId(Long authDataLineId) {
        this.authDataLineId = authDataLineId;
        return this;
    }

    /**
     * @return 权限ID，hiam_role_auth_data.auth_data_id
     */
    public Long getAuthDataId() {
        return authDataId;
    }

    public RoleAuthDataLine setAuthDataId(Long authDataId) {
        this.authDataId = authDataId;
        return this;
    }

    /**
     * @return 租户ID，HPFM.HPFM_TENANT
     */
    public Long getTenantId() {
        return tenantId;
    }

    public RoleAuthDataLine setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 数据ID
     */
    public Long getDataId() {
        return dataId;
    }

    public RoleAuthDataLine setDataId(Long dataId) {
        this.dataId = dataId;
        return this;
    }

    /**
     * @return 数据代码/编码
     */
    public String getDataCode() {
        return dataCode;
    }

    public RoleAuthDataLine setDataCode(String dataCode) {
        this.dataCode = dataCode;
        return this;
    }

    public String getDataName() {
        return dataName;
    }

    public RoleAuthDataLine setDataName(String dataName) {
        this.dataName = dataName;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public RoleAuthDataLine setTenantName(String tenantName) {
        this.tenantName = tenantName;
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
