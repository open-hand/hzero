package org.hzero.iam.domain.entity;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.mybatis.common.query.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组数据权限
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组数据权限")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_dcl")
public class SecGrpDcl extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_dcl";
    public static final String FIELD_SEC_GRP_DCL_ID = "secGrpDclId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_AUTHORITY_TYPE_CODE = "authorityTypeCode";
    public static final String FIELD_INCLUDE_ALL_FLAG = "includeAllFlag";

    public static final String ROOT_ID = "-1";

    public SecGrpDcl() {
    }

    public SecGrpDcl(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public static SecGrpDcl initFrom(SecGrp secGrp, String authorityTypeCode) {
        return new SecGrpDcl()
                .setSecGrpId(secGrp.getSecGrpId())
                .setAuthorityTypeCode(authorityTypeCode)
                .setTenantId(secGrp.getTenantId());
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long secGrpDclId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    @Where
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "权限类型代码，HIAM.AUTHORITY_TYPE_CODE", required = true)
    @NotBlank
    @Where
    private String authorityTypeCode;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @JsonIgnore
    @Transient
    private List<SecGrpDclLine> dclLineList;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpDclId() {
        return secGrpDclId;
    }

    public void setSecGrpDclId(Long secGrpDclId) {
        this.secGrpDclId = secGrpDclId;
    }

    /**
     * @return 安全组ID
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public SecGrpDcl setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
        return this;
    }

    /**
     * @return 租户
     */
    public Long getTenantId() {
        return tenantId;
    }

    public SecGrpDcl setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 权限类型代码，HIAM.AUTHORITY_TYPE_CODE
     */
    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public SecGrpDcl setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
        return this;
    }

    public List<SecGrpDclLine> getDclLineList() {
        return dclLineList;
    }

    public SecGrpDcl setDclLineList(List<SecGrpDclLine> dclLineList) {
        this.dclLineList = dclLineList;
        return this;
    }
}
