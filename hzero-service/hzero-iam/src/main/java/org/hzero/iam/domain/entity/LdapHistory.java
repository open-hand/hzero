package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体：LdapHistory
 *
 * @author bojiangzhou 2019/08/02
 */
@ApiModel("Ldap")
@VersionAudit
@ModifyAudit
@Table(name = "oauth_ldap_history")
public class LdapHistory extends AuditDomain {

    public static final String ENCRYPT_KEY = "LdapHistory";

    public static final String FIELD_LDAP_ID = "ldapId";
    public static final String FIELD_SYNC_END_TIME = "syncEndTime";

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "主键")
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "LDAP 主键")
    @Encrypt
    private Long ldapId;
    @ApiModelProperty(value = "上次同步新增用户个数")
    private Long newUserCount;
    @ApiModelProperty(value = "同步成功用户个数")
    private Long updateUserCount;
    @ApiModelProperty(value = "同步失败用户个数")
    private Long errorUserCount;
    @ApiModelProperty(value = "上次同步开始时间")
    private Date syncBeginTime;
    @ApiModelProperty(value = "上次同步结束时间")
    private Date syncEndTime;
    @ApiModelProperty(value = "同步类型")
    private String syncType;
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public LdapHistory setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLdapId() {
        return ldapId;
    }

    public void setLdapId(Long ldapId) {
        this.ldapId = ldapId;
    }

    public Long getNewUserCount() {
        return newUserCount;
    }

    public void setNewUserCount(Long newUserCount) {
        this.newUserCount = newUserCount;
    }

    public Long getUpdateUserCount() {
        return updateUserCount;
    }

    public void setUpdateUserCount(Long updateUserCount) {
        this.updateUserCount = updateUserCount;
    }

    public Long getErrorUserCount() {
        return errorUserCount;
    }

    public void setErrorUserCount(Long errorUserCount) {
        this.errorUserCount = errorUserCount;
    }

    public Date getSyncBeginTime() {
        return syncBeginTime;
    }

    public void setSyncBeginTime(Date syncBeginTime) {
        this.syncBeginTime = syncBeginTime;
    }

    public Date getSyncEndTime() {
        return syncEndTime;
    }

    public void setSyncEndTime(Date syncEndTime) {
        this.syncEndTime = syncEndTime;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }
}
