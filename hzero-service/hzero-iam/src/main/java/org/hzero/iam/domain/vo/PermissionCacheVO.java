package org.hzero.iam.domain.vo;

import java.io.Serializable;
import java.util.StringJoiner;

import org.hzero.iam.domain.entity.Permission;

/**
 * 权限缓存VO
 *
 * @author bojiangzhou 2019/01/30
 */
public class PermissionCacheVO implements Serializable {
    private static final long serialVersionUID = -5846037872098555317L;

    private Long id;

    private String code;

    private String path;

    private String method;

    private Boolean publicAccess;

    private Boolean loginAccess;

    private Boolean signAccess;

    private Boolean within;

    private String fdLevel;

    private String tag;

    public PermissionCacheVO(Permission permission) {
        this.id = permission.getId();
        this.code = permission.getCode();
        this.path = permission.getPath();
        this.method = permission.getMethod();
        this.publicAccess = permission.getPublicAccess();
        this.loginAccess = permission.getLoginAccess();
        this.signAccess = permission.getSignAccess();
        this.within = permission.getWithin();
        this.fdLevel = permission.getLevel();
        this.tag = permission.getTag();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public Boolean getLoginAccess() {
        return loginAccess;
    }

    public void setLoginAccess(Boolean loginAccess) {
        this.loginAccess = loginAccess;
    }

    public Boolean getSignAccess() {
        return signAccess;
    }

    public void setSignAccess(Boolean signAccess) {
        this.signAccess = signAccess;
    }

    public Boolean getWithin() {
        return within;
    }

    public void setWithin(Boolean within) {
        this.within = within;
    }

    public String getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(String fdLevel) {
        this.fdLevel = fdLevel;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PermissionCacheVO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("code='" + code + "'")
                .add("path='" + path + "'")
                .add("method='" + method + "'")
                .add("publicAccess=" + publicAccess)
                .add("loginAccess=" + loginAccess)
                .add("signAccess=" + signAccess)
                .add("within=" + within)
                .add("fdLevel='" + fdLevel + "'")
                .toString();
    }
}
