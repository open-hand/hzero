package org.hzero.iam.domain.vo;

import java.io.Serializable;

import org.hzero.iam.domain.entity.User;

/**
 * User缓存数据
 *
 * @author bojiangzhou 2018/08/16
 */
public class UserCacheVO implements Serializable {
    private static final long serialVersionUID = -8354548764054921664L;

    private Long id;
    private String loginName;
    private String realName;
    private String userType;
    private String language;
    private Long organizationId;
    private String tenantNum;
    private String imageUrl;

    public UserCacheVO() {
    }

    public UserCacheVO(User user) {
        this.id = user.getId();
        this.loginName = user.getLoginName();
        this.realName = user.getRealName();
        this.userType = user.getUserType();
        this.language = user.getLanguage();
        this.organizationId = user.getOrganizationId();
        this.tenantNum = user.getTenantNum();
        this.imageUrl = user.getImageUrl();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }
}
