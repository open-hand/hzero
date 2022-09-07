package org.hzero.platform.domain.vo;

import org.hzero.platform.infra.constant.FndConstants;

/**
 * @author yunxiang.zhou01@hand-china.com 2018/6/7 14:38
 */
public class ProfileValueVO {

    /**
     * 获取当前vo对应redis配置的key
     *
     * @return redis的key值
     */
    public String generateRedisKey() {
        if (FndConstants.Level.TENANT.equals(this.getProfileLevel())) {
            // 如果为租户级
            StringBuilder keyBuilder = new StringBuilder(this.getProfileName());
            keyBuilder = keyBuilder.append("_");
            keyBuilder = keyBuilder.append(this.getLevelCode());
            keyBuilder = keyBuilder.append("_");
            keyBuilder = keyBuilder.append(this.getLevelValue());
            return keyBuilder.toString();
        } else {
            // 如果为平台级
            return this.getProfileName();
        }
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * hpfm_fnd_profile_value表主键
     */
    private Long profileValueId;

    /**
     * hpfm_fnd_profile表主键
     */
    private Long profileId;

    /**
     * 配置维护名
     */
    private String profileName;

    /**
     * 配置维护应用层级，有平台级(ALL)和租户级(TENANT)
     */
    private String profileLevel;

    /**
     * 租户id，当levelCode为租户级时才存在
     */
    private Long tenantId;

    /**
     * 配置维护值
     */
    private String value;

    /**
     * 配置维护值应用层级，有全局(ALL)和角色(ROLE)和用户(USER)
     */
    private String levelCode;

    /**
     * 配置维护值应用层级值，当应用层级为全局时，为空，否则表示各个层级的值
     */
    private String levelValue;

    public Long getProfileValueId() {
        return profileValueId;
    }

    public void setProfileValueId(Long profileValueId) {
        this.profileValueId = profileValueId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProfileLevel() {
        return profileLevel;
    }

    public void setProfileLevel(String profileLevel) {
        this.profileLevel = profileLevel;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }

}
