package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/20 14:12
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_profile")
public class Profile extends AuditDomain {
    public static final String ENCRYPT_KEY = "hpfm_profile";
    @Id
    @GeneratedValue
    @Encrypt
    private Long profileId;
    private Long profileValueId;
    private String levelCode;
    private String levelValue;
    private String value;

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

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
