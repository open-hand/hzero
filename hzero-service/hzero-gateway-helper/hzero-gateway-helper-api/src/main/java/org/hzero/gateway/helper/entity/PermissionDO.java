package org.hzero.gateway.helper.entity;

import java.io.Serializable;
import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Table(name = "iam_permission")
public class PermissionDO implements Serializable {

    private static final long serialVersionUID = -4108102602163313984L;

    /**
     * 缓存key: permissions:serviceName:method
     */
    public static String generateKey(String serviceName, String method) {
        return String.format("gateway:permissions:" + "%s:%s", serviceName, method);
    }

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String path;

    private String method;

    private Boolean publicAccess;

    private Boolean loginAccess;

    private Boolean signAccess;

    @Column(name = "is_within")
    private Boolean within;

    private String fdLevel;

    private String tag;

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

    public PermissionDO() {
    }

    public PermissionDO(String path) {
        this.path = path;
    }

    public PermissionDO(String path, String method, Boolean publicAccess, Boolean loginAccess, Boolean within, String fdLevel) {
        this.path = path;
        this.method = method;
        this.publicAccess = publicAccess;
        this.loginAccess = loginAccess;
        this.within = within;
        this.fdLevel = fdLevel;
    }

    @Override
    public String toString() {
        return "PermissionDO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", publicAccess=" + publicAccess +
                ", loginAccess=" + loginAccess +
                ", signAccess=" + signAccess +
                ", within=" + within +
                ", fdLevel='" + fdLevel + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    public String toJSONString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}