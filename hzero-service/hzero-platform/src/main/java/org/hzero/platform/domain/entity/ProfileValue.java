package org.hzero.platform.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.redis.RedisHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 配置维护值
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/05
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_profile_value")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileValue extends AuditDomain {

    public static final String PROFILE_VALUE_ID = "profileValueId";


    public interface Insert{}

    /**
     * 新增redis缓存
     *
     * @param redisHelper redisHelper
     * @param key key
     * @param value value
     */
    public static void initCache(RedisHelper redisHelper,String key,String value){
        redisHelper.strSet(key,value);
    }

    /**
     * 删除redis缓存
     *
     * @param redisHelper redisHelper
     * @param key key
     */
    public static void deleteCache(RedisHelper redisHelper,String key){
        redisHelper.delKey(key);
    }

    /**
     * 刷新cache
     *
     * @param redisHelper redisHelper
     * @param key key
     * @param value 值
     * @param oldKey 需要删除的key
     */
    public static void refreshCache(RedisHelper redisHelper,String key, String value, String oldKey){
        redisHelper.delKey(oldKey);
        redisHelper.strSet(key, value);
    }

    /**
     * 判断新增还是更新
     *
     * @return boolean
     */
    public boolean judgeInsert(){
        return profileValueId == null;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long profileValueId;

    @Encrypt
    private Long profileId;

    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Length(max = 30)
    private String levelCode;

    @NotBlank
    @Encrypt(ignoreValue = "GLOBAL")
    private String levelValue;

    @Length(max = 480)
    private String value;

    @NotNull(groups = Insert.class)
    private Long tenantId;

    private Long objectVersionNumber;

    public Long getTenantId() {
        return tenantId;
    }

    public ProfileValue setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 主键id
     */
    public Long getProfileValueId() {
        return profileValueId;
    }

    /**
     * @return 配置维护名id
     */
    public Long getProfileId() {
        return profileId;
    }

    /**
     * @return 配置维护值应用层级，有全局(GLOBAL)和角色(ROLE)和用户(USER)
     */
    public String getLevelCode() {
        return levelCode;
    }

    /**
     * @return 配置维护值应用层级值，当应用层级为全局时，表示租户id，否则表示各个层级的值
     */
    public String getLevelValue() {
        return levelValue;
    }

    /**
     * @return 配置维护值
     */
    public String getValue() {
        return value;
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

    public void setValue(String value) {
        this.value = value;
    }

    public void setProfileValueId(Long profileValueId) {
        this.profileValueId = profileValueId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }


}
