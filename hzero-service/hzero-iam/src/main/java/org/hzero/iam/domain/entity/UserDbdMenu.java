package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 工作台用户级常用功能
 *
 * @author zhixiang.huang@hand-china.com 2019-02-18 10:59:52
 */
@ApiModel("工作台用户级常用功能")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_user_dbd_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDbdMenu extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_user_dbd_menu";
    public static final String FIELD_DBD_MENU_ID = "dbdMenuId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_MENU_ID = "menuId";

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
    private Long dbdMenuId;
    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull
    @Encrypt
    private Long userId;
    @ApiModelProperty(value = "角色ID", required = true)
    @NotNull
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "菜单代码", required = true)
    @NotBlank
    @Encrypt
    private Long menuId;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String title;
    @Transient
    private String menuRoute;

    public UserDbdMenu() {

    }

    public UserDbdMenu(Long tenantId, Long userId, Long roleId) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.roleId = roleId;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMenuRoute() {
        return menuRoute;
    }

    public void setMenuRoute(String menuRoute) {
        this.menuRoute = menuRoute;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getDbdMenuId() {
        return dbdMenuId;
    }

    public void setDbdMenuId(Long dbdMenuId) {
        this.dbdMenuId = dbdMenuId;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 角色ID
     */
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * @return 菜单ID
     */
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

}
