package org.hzero.iam.domain.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hzero.iam.infra.constant.Operation;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.helper.AuditHelper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色权限集关系，permission_id 用来表示权限集，Choerodon表示的是权限，注意区分。
 */
@ApiModel("角色权限关系")
@ModifyAudit
@Table(name = "iam_role_permission")
public class RolePermission {

    public static final String ENCRYPT_KEY = "iam_role_permission";

    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_CREATE_FLAG = "createFlag";
    public static final String FIELD_INHERIT_FLAG = "inheritFlag";
    public static final String FIELD_PERMISSION_SET_ID = "permissionSetId";
    public static final String FIELD_TYPE = "type";

    /**
     * 默认为权限集类型
     */
    public static final RolePermissionType DEFAULT_TYPE = RolePermissionType.PS;

    public RolePermission() {}

    /**
     *
     * @param roleId 角色ID
     * @param permissionSetId 权限集ID
     * @param inheritFlag 继承标识
     * @param createFlag 创建标识
     */
    public RolePermission(Long roleId, Long permissionSetId, String inheritFlag, String createFlag, String type) {
        this.roleId = roleId;
        this.permissionSetId = permissionSetId;
        this.inheritFlag = inheritFlag;
        this.createFlag = createFlag;
        this.type = type;
    }

    /**
     *
     * @param roleId 角色ID
     * @param permissionSetId 权限集ID
     * @param inheritFlag 继承标识
     * @param createFlag 创建标识
     * @param tenantId 租户ID
     */
    public RolePermission(Long roleId, Long permissionSetId, String inheritFlag, String createFlag, String type, Long tenantId) {
        this.roleId = roleId;
        this.permissionSetId = permissionSetId;
        this.inheritFlag = inheritFlag;
        this.createFlag = createFlag;
        this.type = type;
        this.tenantId = tenantId;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty("角色ID")
    private Long roleId;
    /**
     * 该权限ID表示权限集ID
     */
    @Column(name = "permission_id")
    @ApiModelProperty("权限ID")
    private Long permissionSetId;
    @Column(name = "type")
    @ApiModelProperty("权限类型")
    private String type;
    @Column(name = "h_create_flag")
    @ApiModelProperty("创建标识")
    private String createFlag;
    @Column(name = "h_inherit_flag")
    @ApiModelProperty("继承标识")
    private String inheritFlag;
    @NotNull
    private Long tenantId;

    @Transient
    @ApiModelProperty("角色编码")
    private String roleCode;
    @Transient
    @ApiModelProperty("角色唯一路径")
    private String levelPath;
    @Transient
    @ApiModelProperty("权限集合")
    private Set<Long> permissionSetIds;
    @Transient
    @JsonIgnore
    @ApiModelProperty("角色ID")
    private Set<Long> roleIds;
    @Transient
    @ApiModelProperty("权限层级")
    private String level;
    @Transient
    private boolean bothCreateAndInheritFlag;

    @ApiModelProperty(hidden = true)
    private Date creationDate;
    @ApiModelProperty(hidden = true)
    private Long createdBy;
    @ApiModelProperty(hidden = true)
    private Date lastUpdateDate;
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;

    @Transient
    @JsonIgnore
    private Operation operation;

    /**
     * 是否为租户管理员标识
     */
    @Transient
    @JsonIgnore
    private Boolean tenantAdmin;

    public Boolean getTenantAdmin() {
        return tenantAdmin;
    }

    public void setTenantAdmin(Boolean tenantAdmin) {
        this.tenantAdmin = tenantAdmin;
    }

    public Date getCreationDate() {
        if (creationDate == null) {
            return AuditHelper.audit().getNow();
        } else {
            return creationDate;
        }
    }

    public void setCreationDate(Date creationDate) {
        if (creationDate == null) {
            creationDate = AuditHelper.audit().getNow();
        }
        this.creationDate = creationDate;
    }

    public Long getCreatedBy() {
        if (createdBy == null) {
            return AuditHelper.audit().getUser();
        } else {
            return createdBy;
        }
    }

    public void setCreatedBy(Long createdBy) {
        if (createdBy == null) {
            createdBy = AuditHelper.audit().getUser();
        }
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        if (lastUpdateDate == null) {
            return AuditHelper.audit().getNow();
        } else {
            return lastUpdateDate;
        }
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        if (lastUpdateDate == null) {
            lastUpdateDate = AuditHelper.audit().getNow();
        }
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getLastUpdatedBy() {
        if (lastUpdatedBy == null) {
            return AuditHelper.audit().getUser();
        } else {
            return lastUpdatedBy;
        }
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        if (lastUpdatedBy == null) {
            lastUpdatedBy = AuditHelper.audit().getUser();
        }
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public RolePermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public RolePermission setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public RolePermission setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public Long getPermissionSetId() {
        return permissionSetId;
    }

    public RolePermission setPermissionSetId(Long permissionSetId) {
        this.permissionSetId = permissionSetId;
        return this;
    }

    public String getType() {
        return type;
    }

    public RolePermission setType(String type) {
        this.type = type;
        return this;
    }

    public String getCreateFlag() {
        return createFlag;
    }

    public RolePermission setCreateFlag(String createFlag) {
        this.createFlag = createFlag;
        return this;
    }

    public String getInheritFlag() {
        return inheritFlag;
    }

    public RolePermission setInheritFlag(String inheritFlag) {
        this.inheritFlag = inheritFlag;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public RolePermission setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public Set<Long> getPermissionSetIds() {
        return permissionSetIds;
    }

    public RolePermission setPermissionSetIds(Set<Long> permissionSetIds) {
        this.permissionSetIds = permissionSetIds;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public RolePermission setLevel(String level) {
        this.level = level;
        return this;
    }

    public boolean isBothCreateAndInheritFlag() {
        return bothCreateAndInheritFlag;
    }

    public RolePermission setBothCreateAndInheritFlag(boolean bothCreateAndInheritFlag) {
        this.bothCreateAndInheritFlag = bothCreateAndInheritFlag;
        return this;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", permissionSetId=" + permissionSetId +
                ", type='" + type + '\'' +
                ", createFlag='" + createFlag + '\'' +
                ", inheritFlag='" + inheritFlag + '\'' +
                '}';
    }
}
