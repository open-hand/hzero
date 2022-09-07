package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.helper.AuditHelper;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author wuguokai
 * @author allen modified 2018/06/29
 */
@ModifyAudit
@Table(name = "iam_menu_permission")
public class MenuPermission{
    public static final String ENCRYPT_KEY = "iam_menu_permission";
    public static final String FIELD_MENU_ID = "menuId";
    public static final String FIELD_PERMISSION_CODE = "permissionCode";
    public static final String FIELD_TENANT_ID = "tenantId";
    @Id
    @GeneratedValue
    private Long id;
    private Long menuId;
    private String permissionCode;
    private Long tenantId;
    @ApiModelProperty(hidden = true)
    private Date creationDate;
    @ApiModelProperty(hidden = true)
    private Long createdBy;
    @ApiModelProperty(hidden = true)
    private Date lastUpdateDate;
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;

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

    public MenuPermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
}
