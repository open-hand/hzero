package org.hzero.iam.api.dto;


import java.util.List;

import org.hzero.iam.infra.constant.PermissionType;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 将权限添加到指定的权限集
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
public class MenuPermissionSetDTO {

    @Encrypt
    private List<Long> menuIds;
    private PermissionType permissionType;
    String[] permissionCodes;
    private String level;

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public String[] getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(String[] permissionCodes) {
        this.permissionCodes = permissionCodes;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
