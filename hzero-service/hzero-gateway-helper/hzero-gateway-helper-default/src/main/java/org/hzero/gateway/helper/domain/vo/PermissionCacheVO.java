package org.hzero.gateway.helper.domain.vo;

import java.io.Serializable;

import org.hzero.gateway.helper.entity.PermissionDO;

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

    private Boolean within;

    private String fdLevel;

    public PermissionCacheVO(PermissionDO permission) {
        this.id = permission.getId();
        this.code = permission.getCode();
        this.path = permission.getPath();
        this.method = permission.getMethod();
        this.publicAccess = permission.getPublicAccess();
        this.loginAccess = permission.getLoginAccess();
        this.within = permission.getWithin();
        this.fdLevel = permission.getFdLevel();
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
}
