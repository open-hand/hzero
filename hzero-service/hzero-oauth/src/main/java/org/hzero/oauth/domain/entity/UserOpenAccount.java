package org.hzero.oauth.domain.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
@ModifyAudit
@VersionAudit
@Table(name = "hiam_user_open_account")
public class UserOpenAccount extends AuditDomain implements Serializable {
    private static final long serialVersionUID = -5925250911874624340L;

    @Id
    @GeneratedValue
    private Long openAccountId;
    private String username;
    private String openId;
    private String unionId;
    private String openName;
    private String imageUrl;
    private String openAppCode;
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public UserOpenAccount setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getOpenAccountId() {
        return openAccountId;
    }

    public void setOpenAccountId(Long openAccountId) {
        this.openAccountId = openAccountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getOpenName() {
        return openName;
    }

    public void setOpenName(String openName) {
        this.openName = openName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOpenAppCode() {
        return openAppCode;
    }

    public void setOpenAppCode(String openAppCode) {
        this.openAppCode = openAppCode;
    }
}
