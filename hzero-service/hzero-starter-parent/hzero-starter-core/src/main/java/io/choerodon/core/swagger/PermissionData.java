package io.choerodon.core.swagger;

import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author flyleft
 * 2018/4/13
 */
public class PermissionData implements Serializable {
    private static final long serialVersionUID = -6503810120492682442L;
    /**
     * 全局权限编码模板：{{serviceName}}.{{resourceCode}}.{{permissionCode}}
     */
    private static final String GLOBAL_PERMISSION_CODE_TEMPLATE = "%s.%s.%s";

    private String path;
    private String method;
    private String description;

    private String code;

    private String action;

    private String permissionLevel;

    private String[] roles;

    private boolean permissionLogin;

    private boolean permissionPublic;

    private boolean permissionWithin;

    private boolean permissionSign;

    private String[] tags;

    private String status;

    private String upgradeApiPath;

    private HttpMethod upgradeApiMethod;

    private String resourceCode;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public boolean isPermissionLogin() {
        return permissionLogin;
    }

    public void setPermissionLogin(boolean permissionLogin) {
        this.permissionLogin = permissionLogin;
    }

    public boolean isPermissionPublic() {
        return permissionPublic;
    }

    public void setPermissionPublic(boolean permissionPublic) {
        this.permissionPublic = permissionPublic;
    }

    public boolean isPermissionWithin() {
        return permissionWithin;
    }

    public void setPermissionWithin(boolean permissionWithin) {
        this.permissionWithin = permissionWithin;
    }

    public boolean isPermissionSign() {
        return permissionSign;
    }

    public void setPermissionSign(boolean permissionSign) {
        this.permissionSign = permissionSign;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpgradeApiPath() {
        return upgradeApiPath;
    }

    public void setUpgradeApiPath(String upgradeApiPath) {
        this.upgradeApiPath = upgradeApiPath;
    }

    public HttpMethod getUpgradeApiMethod() {
        return upgradeApiMethod;
    }

    public void setUpgradeApiMethod(HttpMethod upgradeApiMethod) {
        this.upgradeApiMethod = upgradeApiMethod;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    /**
     * 获取全局权限编码
     *
     * @return 当前权限的全局权限编码
     */
    public String getGlobalPermissionCode(String serviceName) {
        return String.format(GLOBAL_PERMISSION_CODE_TEMPLATE, serviceName, this.resourceCode, this.code);
    }

    @Override
    public String toString() {
        return "PermissionData{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", action='" + action + '\'' +
                ", permissionLevel='" + permissionLevel + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", permissionLogin=" + permissionLogin +
                ", permissionPublic=" + permissionPublic +
                ", permissionWithin=" + permissionWithin +
                ", permissionSign=" + permissionSign +
                ", tags=" + Arrays.toString(tags) +
                ", status='" + status + '\'' +
                ", upgradeApiPath='" + upgradeApiPath + '\'' +
                ", upgradeApiMethod=" + upgradeApiMethod +
                ", resourceCode='" + resourceCode + '\'' +
                '}';
    }
}
