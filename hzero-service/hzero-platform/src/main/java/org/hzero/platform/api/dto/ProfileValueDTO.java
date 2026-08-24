package org.hzero.platform.api.dto;

import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author yunxiang.zhou01@hand-china.com 2018/06/05
 */
public class ProfileValueDTO extends ProfileValue {

    @Encrypt
    private Long profileValueId;

    @Encrypt
    private Long profileId;

    private String levelCode;

    private String levelCodeDescription;

    @Encrypt(ignoreValue = "GLOBAL")
    private String levelValue;

    private String levelValueDescription;

    private String value;

    private Long objectVersionNumber;

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public Long getProfileValueId() {
        return profileValueId;
    }

    @Override
    public void setProfileValueId(Long profileValueId) {
        this.profileValueId = profileValueId;
    }

    @Override
    public Long getProfileId() {
        return profileId;
    }

    @Override
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    @Override
    public String getLevelCode() {
        return levelCode;
    }

    @Override
    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelCodeDescription() {
        return levelCodeDescription;
    }

    public void setLevelCodeDescription(String levelCodeDescription) {
        this.levelCodeDescription = levelCodeDescription;
    }

    @Override
    public String getLevelValue() {
        return levelValue;
    }

    @Override
    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }

    public String getLevelValueDescription() {
        return levelValueDescription;
    }

    public void setLevelValueDescription(String levelValueDescription) {
        this.levelValueDescription = levelValueDescription;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
