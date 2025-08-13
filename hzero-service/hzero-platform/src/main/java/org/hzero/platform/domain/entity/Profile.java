package org.hzero.platform.domain.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import io.choerodon.mybatis.domain.AuditDomain;
import javax.validation.constraints.NotBlank;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

/**
 * 配置维护
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/05
 */
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_profile")
@ApiModel("配置维护")
public class Profile extends AuditDomain {

    public static final String PROFILE_ID = "profileId";
    public static final String FIELD_DESCRIPTION = "description";


    /**
     * 插入租户id
     */
    public void injectTenantId() {
        // 平台级调用，若租户id为null则传递一个默认租户id
        if (this.tenantId == null) {
            this.tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
    }

    /**
     * 判断更新还是新增
     *
     * @return boolean
     */
    public boolean judgeInsert() {
        return profileId == null;
    }

    /**
     * 根据配置维护和配置维护值获取缓存key
     *
     * @param profile 配置维护
     * @param profileValue 配置维护值
     * @return key
     */
    public static String generateCacheKey(Profile profile, ProfileValue profileValue) {
        return generateCacheKey(profile.getTenantId(), profile.getProfileName(),
                        profileValue.getLevelCode(), profileValue.getLevelValue());
    }

    /**
     * 生成redis缓存key值
     * FIX 2019-04-19 去除缓存key中的维度值
     *
     * @param tenantId 租户id
     * @param profileName 配置维护名
     * @param levelCode 应用层级
     * @param levelValue 应用层级值
     * @return key
     */
    public static String generateCacheKey(Long tenantId, String profileName, String levelCode,
                    String levelValue) {
        StringBuilder sb = new StringBuilder(FndConstants.CacheKey.PROFILE_KEY + ":");
        sb = sb.append(tenantId).append(".").append(profileName).append(".").append(levelCode)
                        .append(".").append(levelValue);
        return sb.toString();
    }

    /**
     * 校验当前对象变量规则
     *
     * @throws IllegalArgumentException 参数校验不通过
     */
    public void validate() {
        if (FndConstants.Level.TENANT.equals(profileLevel)) {
            Assert.notNull(tenantId, "tenantId must not be null when level is TENANT.");
        }
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("配置维护ID")
    @Encrypt
    private Long profileId;

    @NotBlank
    @ApiModelProperty("配置维护名")
    @Length(max = 30)
    private String profileName;

    @ApiModelProperty("描述")
    @Length(max = 240)
    @MultiLanguageField
    private String description;

    @NotBlank
    @ApiModelProperty("应用维度")
    @LovValue(lovCode = "HPFM.LEVEL",meaningField = "profileLevelMeaning")
    private String profileLevel;

    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;

    @ApiModelProperty("版本号")
    private Long objectVersionNumber;

    @Transient
    private String profileLevelMeaning;

    @Transient
    @ApiModelProperty(value = "配置维护值", hidden = true)
    private List<ProfileValue> profileValueList;

    /**
     * @return 主键id
     */
    public Long getProfileId() {
        return profileId;
    }

    /**
     * @return 配置维护名
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 配置维护应用层级，有平台级(ALL)和租户级(TENANT)
     */
    public String getProfileLevel() {
        return profileLevel;
    }

    /**
     * @return 租户id，当levelCode为租户级时才存在
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * @return 配置维护值list
     */
    public List<ProfileValue> getProfileValueList() {
        return profileValueList;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfileLevel(String profileLevel) {
        this.profileLevel = profileLevel;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public void setProfileValueList(List<ProfileValue> profileValueList) {
        this.profileValueList = profileValueList;
    }

    public String getProfileLevelMeaning() {
        return profileLevelMeaning;
    }

    public void setProfileLevelMeaning(String profileLevelMeaning) {
        this.profileLevelMeaning = profileLevelMeaning;
    }
}
