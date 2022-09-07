package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.infra.constant.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组数据权限维度行
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组数据权限维度行")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_dcl_dim_line")
public class SecGrpDclDimLine extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_dcl_dim_line";
    public static final String FIELD_SEC_GRP_DCL_DIM_LINE_ID = "secGrpDclDimLineId";
    public static final String FIELD_SEC_GRP_DCL_DIM_ID = "secGrpDclDimId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_AUTH_TYPE_CODE = "authTypeCode";
    public static final String FIELD_AUTO_ASSIGN_FLAG = "authTypeCode";
    public static final String FIELD_ASSIGN_TYPE_CODE = "assignTypeCode";

    public SecGrpDclDimLine() {
    }

    public SecGrpDclDimLine(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    @ApiModelProperty(value = "权限分配类型，Code：HAIM.SEC_GRP.ASSIGN_TYPE_CODE ([SELF/自己创建]、[PARENT/父类分配]、[SELF_PARENT/自己创建之后，父类也创建])")
    private String assignTypeCode;

    public static SecGrpDclDimLine buildDimLine(SecGrpDclDim dim,
                                                String authTypeCode,
                                                Long secGrpDclDimLineId,
                                                Integer checkedFlag,
                                                String assignTypeCode) {
        SecGrpDclDimLine secGrpDclDimLine = new SecGrpDclDimLine();
        secGrpDclDimLine.setAuthTypeCode(authTypeCode);
        secGrpDclDimLine.setSecGrpDclDimLineId(secGrpDclDimLineId);
        secGrpDclDimLine.setSecGrpDclDimId(dim.getSecGrpDclDimId());
        secGrpDclDimLine.setSecGrpId(dim.getSecGrpId());
        secGrpDclDimLine.setTenantId(dim.getTenantId());
        secGrpDclDimLine.setCheckedFlag(checkedFlag);
        secGrpDclDimLine.setAssignTypeCode(assignTypeCode);
        return secGrpDclDimLine;
    }

    @JsonIgnore
    @Transient
    public boolean isChecked() {
        return BaseConstants.Flag.YES.equals(this.checkedFlag);
    }

    @JsonIgnore
    @Transient
    public boolean isUnchecked() {
        return BaseConstants.Flag.NO.equals(this.checkedFlag);
    }

    public static SecGrpDclDimLine buildDimLine(SecGrpDclDim dim, String authTypeCode, String assignTypeCode) {
        return buildDimLine(dim, authTypeCode, null, null, assignTypeCode);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long secGrpDclDimLineId;
    @ApiModelProperty(value = "安全组数据权限维度ID", required = true)
    @NotNull
    private Long secGrpDclDimId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "权限类型代码，HIAM.AUTHORITY_TYPE_CODE", required = true)
    @NotBlank
    private String authTypeCode;
    @Transient
    @JsonIgnore
    private Long roleId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @JsonIgnore
    private Integer checkedFlag;

    /**
     * 安全组数据权限维度行可操作性
     *
     * @return true 可操作    false 不可操作
     */
    @JsonIgnore
    public boolean operability() {
        return Constants.SecGrpAssignTypeCode.SELF.equals(this.assignTypeCode);
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpDclDimLineId() {
        return secGrpDclDimLineId;
    }

    public void setSecGrpDclDimLineId(Long secGrpDclDimLineId) {
        this.secGrpDclDimLineId = secGrpDclDimLineId;
    }

    /**
     * @return 安全组数据权限维度ID
     */
    public Long getSecGrpDclDimId() {
        return secGrpDclDimId;
    }

    public void setSecGrpDclDimId(Long secGrpDclDimId) {
        this.secGrpDclDimId = secGrpDclDimId;
    }

    /**
     * @return 安全组ID
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
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
     * @return 权限类型代码，HIAM.AUTHORITY_TYPE_CODE
     */
    public String getAuthTypeCode() {
        return authTypeCode;
    }

    public void setAuthTypeCode(String authTypeCode) {
        this.authTypeCode = authTypeCode;
    }

    public String getAssignTypeCode() {
        return assignTypeCode;
    }

    public void setAssignTypeCode(String assignTypeCode) {
        this.assignTypeCode = assignTypeCode;
    }

    public void setCheckedFlag(Integer checkedFlag) {
        this.checkedFlag = checkedFlag;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
