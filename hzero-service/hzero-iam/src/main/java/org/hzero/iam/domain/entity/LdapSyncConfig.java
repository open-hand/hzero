package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Ldap同步配置表
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-11 10:47:49
 */
@ApiModel("Ldap同步配置表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "oauth_ldap_sync_config")
public class LdapSyncConfig extends AuditDomain {

    public static final String ENCRYPT_KEY = "oauth_ldap_sync_config";

    public static final String FIELD_LDAP_SYNC_CONFIG_ID = "ldapSyncConfigId";
    public static final String FIELD_LDAP_ID = "ldapId";
    public static final String FIELD_SYNC_JOB_ID = "syncJobId";
    public static final String FIELD_SYNC_TYPE = "syncType";
    public static final String FIELD_FREQUENCY = "frequency";
    public static final String FIELD_CUSTOM_FILTER = "customFilter";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long ldapSyncConfigId;
    @ApiModelProperty(value = "oauth_ldap.id", required = true)
    @NotNull
    @Encrypt
    private Long ldapId;
    @ApiModelProperty(value = "定时jobId")
    private Long syncJobId;
    @ApiModelProperty(value = "同步类型", required = true)
    @NotBlank
    private String syncType;
    @ApiModelProperty(value = "同步频率,值集HIAM.LDAP_SYNC_FREQUENCY", required = true)
    @NotBlank
    @LovValue(lovCode = "HIAM.LDAP_SYNC_FREQUENCY", meaningField = "frequencyMeaning")
    private String frequency;
    @ApiModelProperty(value = "同步离职用户自定义筛选条件")
    private String customFilter;
    @ApiModelProperty(value = "开始时间")
    @NotNull
    private Date startDate;
    @ApiModelProperty(value = "结束时间")
    private Date endDate;
    @ApiModelProperty(value = "是否开启Ldap自动同步，默认不开启", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户Id", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String frequencyMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getLdapSyncConfigId() {
        return ldapSyncConfigId;
    }

    public void setLdapSyncConfigId(Long ldapSyncConfigId) {
        this.ldapSyncConfigId = ldapSyncConfigId;
    }

    /**
     * @return oauth_ldap.id
     */
    public Long getLdapId() {
        return ldapId;
    }

    public void setLdapId(Long ldapId) {
        this.ldapId = ldapId;
    }

    /**
     * @return 定时jobId
     */
    public Long getSyncJobId() {
        return syncJobId;
    }

    public void setSyncJobId(Long syncJobId) {
        this.syncJobId = syncJobId;
    }

    /**
     * @return 同步类型
     */
    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    /**
     * @return 同步频率,值集HIAM.LDAP_SYNC_FREQUENCY
     */
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * @return 同步离职用户自定义筛选条件，仅开启同步离职之后生效
     */
    public String getCustomFilter() {
        return customFilter;
    }

    public void setCustomFilter(String customFilter) {
        this.customFilter = customFilter;
    }

    /**
     * @return 开始时间
     */
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return 结束时间
     */
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return 是否开启Ldap自动同步，默认不开启
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 租户Id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getFrequencyMeaning() {
        return frequencyMeaning;
    }

    public void setFrequencyMeaning(String frequencyMeaning) {
        this.frequencyMeaning = frequencyMeaning;
    }

    @Override
    public String toString() {
        return "LdapSyncConfig{" +
                "ldapSyncConfigId=" + ldapSyncConfigId +
                ", ldapId=" + ldapId +
                ", syncJobId=" + syncJobId +
                ", syncType='" + syncType + '\'' +
                ", frequency='" + frequency + '\'' +
                ", customFilter='" + customFilter + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                ", frequencyMeaning='" + frequencyMeaning + '\'' +
                '}';
    }
}
