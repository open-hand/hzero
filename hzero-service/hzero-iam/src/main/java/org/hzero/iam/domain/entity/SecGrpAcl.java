package org.hzero.iam.domain.entity;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组访问权限
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组访问权限")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_acl")
public class SecGrpAcl extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_acl";
    public static final String FIELD_SEC_GRP_ACL_ID = "secGrpAclId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PERMISSION_ID = "permissionId";


    public static List<SecGrpAcl> toList(SecGrp secGrp, List<Long> permissionIds) {
        return permissionIds.parallelStream().map((permissionId) -> {
            SecGrpAcl secGrpAcl = new SecGrpAcl(secGrp.getSecGrpId());
            secGrpAcl.setTenantId(secGrp.getTenantId());
            secGrpAcl.setPermissionId(permissionId);
            return secGrpAcl;
        }).collect(Collectors.toList());
    }

    public SecGrpAcl() {

    }

    public SecGrpAcl(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public SecGrpAcl(Long secGrpId, Long tenantId, Long permissionId) {
        this.secGrpId = secGrpId;
        this.tenantId = tenantId;
        this.permissionId = permissionId;
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long secGrpAclId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "访问权限ID", required = true)
    @NotNull
    private Long permissionId;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpAclId() {
        return secGrpAclId;
    }

    public void setSecGrpAclId(Long secGrpAclId) {
        this.secGrpAclId = secGrpAclId;
    }

    /**
     * @return 安全组ID
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public SecGrpAcl setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
        return this;
    }

    /**
     * @return 租户
     */
    public Long getTenantId() {
        return tenantId;
    }

    public SecGrpAcl setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 访问权限ID
     */
    public Long getPermissionId() {
        return permissionId;
    }

    public SecGrpAcl setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

}
