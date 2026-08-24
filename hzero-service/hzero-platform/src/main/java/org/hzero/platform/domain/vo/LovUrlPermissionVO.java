package org.hzero.platform.domain.vo;

/**
 * URL 值集对应的接口权限信息。
 *
 * @author 25287
 */
public class LovUrlPermissionVO {

    private String lovCode;
    private String serviceName;
    private String path;
    private String method;
    private String permissionCode;
    private Boolean publicAccess;
    private Boolean loginAccess;

    public String getLovCode() {
        return lovCode;
    }

    public LovUrlPermissionVO setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LovUrlPermissionVO setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getPath() {
        return path;
    }

    public LovUrlPermissionVO setPath(String path) {
        this.path = path;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public LovUrlPermissionVO setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public LovUrlPermissionVO setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
        return this;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public LovUrlPermissionVO setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
        return this;
    }

    public Boolean getLoginAccess() {
        return loginAccess;
    }

    public LovUrlPermissionVO setLoginAccess(Boolean loginAccess) {
        this.loginAccess = loginAccess;
        return this;
    }
}
