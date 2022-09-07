package org.hzero.iam.domain.entity;

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
 * 租户权限
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM 2019-11-28
 */
@ApiModel("租户权限")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hiam_tenant_permission")
public class TenantPermission extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_tenant_permission";
    public static final String FIELD_TENANT_PERMISSION_ID = "tenantPermissionId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PERMISSION_ID = "permissionId";


    public TenantPermission() {
    }

    public TenantPermission(@NotNull Long tenantId, @NotNull Long permissionId) {
        this.tenantId = tenantId;
        this.permissionId = permissionId;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty
    @Id
    @GeneratedValue
    private Long tenantPermissionId;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "权限ID，iam_permission.id", required = true)
    @NotNull
    private Long permissionId;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return
     */
    public Long getTenantPermissionId() {
        return tenantPermissionId;
    }

    public TenantPermission setTenantPermissionId(Long tenantPermissionId) {
        this.tenantPermissionId = tenantPermissionId;
        return this;
    }

    /**
     * @return 租户ID，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public TenantPermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 权限ID，iam_permission.id
     */
    public Long getPermissionId() {
        return permissionId;
    }

    public TenantPermission setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

}
