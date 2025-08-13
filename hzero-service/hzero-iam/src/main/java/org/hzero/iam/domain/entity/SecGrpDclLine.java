package org.hzero.iam.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 安全组数据权限行
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组数据权限行")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_dcl_line")
public class SecGrpDclLine extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_dcl_line";
    public static final String FIELD_SEC_GRP_DCL_LINE_ID = "secGrpDclLineId";
    public static final String FIELD_SEC_GRP_DCL_ID = "secGrpDclId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_DATA_CODE = "dataCode";
    public static final String FIELD_DATA_NAME = "dataName";

    public SecGrpDclLine() {
    }

    public SecGrpDclLine(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public String buildUniqueKey() {
        return String.format("%s-%s", this.authorityTypeCode, this.dataId);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long secGrpDclLineId;
    @ApiModelProperty(value = "安全组数据权限ID", required = true)
    @NotNull
    @Encrypt
    private Long secGrpDclId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    @Encrypt
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "数据ID", required = true)
    @NotNull
    @Encrypt
    private Long dataId;
    @ApiModelProperty(value = "数据代码/编码")
    private String dataCode;
    @ApiModelProperty(value = "数据名称")
    private String dataName;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private Integer checkedFlag;
    @Transient
    private String tenantName;
    @Transient
    private String authorityTypeCode;
    /**
     * 用于标记处理是否被屏蔽  1-是 0-否
     */
    @Transient
    private Integer shieldFlag;
    @Transient
    private String typeCode;


    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpDclLineId() {
        return secGrpDclLineId;
    }

    public SecGrpDclLine setSecGrpDclLineId(Long secGrpDclLineId) {
        this.secGrpDclLineId = secGrpDclLineId;
        return this;
    }

    /**
     * @return 安全组数据权限ID
     */
    public Long getSecGrpDclId() {
        return secGrpDclId;
    }

    public SecGrpDclLine setSecGrpDclId(Long secGrpDclId) {
        this.secGrpDclId = secGrpDclId;
        return this;
    }

    /**
     * @return 安全组ID
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public SecGrpDclLine setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
        return this;
    }

    /**
     * @return 租户
     */
    public Long getTenantId() {
        return tenantId;
    }

    public SecGrpDclLine setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 数据ID
     */
    public Long getDataId() {
        return dataId;
    }

    public SecGrpDclLine setDataId(Long dataId) {
        this.dataId = dataId;
        return this;
    }

    /**
     * @return 数据代码/编码
     */
    public String getDataCode() {
        return dataCode;
    }

    public SecGrpDclLine setDataCode(String dataCode) {
        this.dataCode = dataCode;
        return this;
    }

    /**
     * @return 数据名称
     */
    public String getDataName() {
        return dataName;
    }

    public SecGrpDclLine setDataName(String dataName) {
        this.dataName = dataName;
        return this;
    }

    public Integer getCheckedFlag() {
        return checkedFlag;
    }

    public SecGrpDclLine setCheckedFlag(Integer checkedFlag) {
        this.checkedFlag = checkedFlag;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public SecGrpDclLine setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
        return this;
    }

    public Integer getShieldFlag() {
        return shieldFlag;
    }

    public void setShieldFlag(Integer shieldFlag) {
        this.shieldFlag = shieldFlag;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
