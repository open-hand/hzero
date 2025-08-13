package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组权限回收
 *
 * @author xingxing.wu@hand-china.com  2019-10-31 14:00:03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组权限回收")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_revoke")
public class SecGrpRevoke extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_revoke";
    public static final String FIELD_SEC_GRP_REVOKE_ID = "secGrpRevokeId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_REVOKE_TYPE = "revokeType";
    public static final String FIELD_AUTHORITY_TYPE = "authorityType";
    public static final String FIELD_AUTHORITY_ID = "authorityId";
    public static final String FIELD_SHIELD_ROLE_ID = "shieldRoleId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    public static SecGrpRevoke build(SecGrp secGrp, Role role, Long authorityId,
                                     SecGrpAuthorityRevokeType revokeType, SecGrpAuthorityType authorityType) {
        SecGrpRevoke secGrpRevoke = new SecGrpRevoke();
        secGrpRevoke.setSecGrpId(secGrp.getSecGrpId());
        secGrpRevoke.setTenantId(secGrp.getTenantId());
        secGrpRevoke.setRevokeType(revokeType.value());
        secGrpRevoke.setAuthorityType(authorityType.value());
        secGrpRevoke.setAuthorityId(authorityId);
        if (role != null) {
            secGrpRevoke.setShieldRoleId(role.getId());
        }
        return secGrpRevoke;
    }

    public static SecGrpRevoke build(Long secGrpId, Long tenantId, Long authorityId,
                                     SecGrpAuthorityRevokeType revokeType, SecGrpAuthorityType authorityType) {
        SecGrpRevoke secGrpRevoke = new SecGrpRevoke();
        secGrpRevoke.setSecGrpId(secGrpId);
        secGrpRevoke.setTenantId(tenantId);
        secGrpRevoke.setRevokeType(revokeType.value());
        secGrpRevoke.setAuthorityType(authorityType.value());
        secGrpRevoke.setAuthorityId(authorityId);
        return secGrpRevoke;
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long secGrpRevokeId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "回收类型，HIAM.SEC_GRP_REVOKE_TYPE，REVOKE-回收/SHIELD-屏蔽", required = true)
    @NotBlank
    private String revokeType;
    @ApiModelProperty(value = "权限类型，HIAM.SEC_GRP_AUTHORITY_TYPE", required = true)
    @NotBlank
    private String authorityType;
    @ApiModelProperty(value = "安全组权限ID", required = true)
    @NotNull
    private Long authorityId;
    @ApiModelProperty(value = "屏蔽角色ID")
    private Long shieldRoleId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpRevokeId() {
        return secGrpRevokeId;
    }

    public void setSecGrpRevokeId(Long secGrpRevokeId) {
        this.secGrpRevokeId = secGrpRevokeId;
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
     * @return 回收类型，HIAM.SEC_GRP_REVOKE_TYPE，REVOKE-回收/SHIELD-屏蔽
     */
    public String getRevokeType() {
        return revokeType;
    }

    public void setRevokeType(String revokeType) {
        this.revokeType = revokeType;
    }

    /**
     * @return 权限类型，HIAM.SEC_GRP_AUTHORITY_TYPE
     */
    public String getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(String authorityType) {
        this.authorityType = authorityType;
    }

    /**
     * @return 权限ID
     */
    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * @return 屏蔽角色ID
     */
    public Long getShieldRoleId() {
        return shieldRoleId;
    }

    public void setShieldRoleId(Long shieldRoleId) {
        this.shieldRoleId = shieldRoleId;
    }
}
