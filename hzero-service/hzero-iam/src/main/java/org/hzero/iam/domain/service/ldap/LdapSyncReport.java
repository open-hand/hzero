package org.hzero.iam.domain.service.ldap;

import java.util.Date;

/**
 * @author wuguokai
 */
public class LdapSyncReport {
    private Long insert;
    private Long update;
    private Long error;
    private Long organizationId;
    private Date startTime;
    private Date endTime;
    private Long count;
    private Long ldapId;

    public LdapSyncReport(Long organizationId) {
        this.organizationId = organizationId;
        this.insert = 0L;
        this.update = 0L;
        this.error = 0L;
        this.count = 0L;
    }

    public void setInsert(Long insert) {
        this.insert = insert;
    }

    public void setUpdate(Long update) {
        this.update = update;
    }

    public void setError(Long error) {
        this.error = error;
    }

    public void setCount(Long count) {
        this.count = count;
    }


    public void incrementNewInsert() {
        this.insert++;
    }

    public void incrementError() {
        this.error++;
    }

    public void incrementError(Long count) {
        this.error = this.error + count;
    }

    public void incrementUpdate() {
        this.update++;
    }

    public void incrementUpdate(Long update) {
        this.update = this.update + update;
    }

    public void incrementCount() {
        this.count++;
    }

    public void incrementCount(Long count) {
        this.count = this.count + count;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getInsert() {
        return insert;
    }

    public Long getUpdate() {
        return update;
    }

    public Long getError() {
        return error;
    }

    public Long getCount() {
        return count;
    }

    public Long getLdapId() {
        return ldapId;
    }

    public void setLdapId(Long ldapId) {
        this.ldapId = ldapId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "LdapSyncReport{" +
                "insert=" + insert +
                ", update=" + update +
                ", error=" + error +
                ", organizationId=" + organizationId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", count=" + count +
                ", ldapId=" + ldapId +
                '}';
    }

    public void reduceInsert(Long count) {
        this.insert = this.insert - count;
    }

    public void reduceUpdate(Long count) {
        this.update = this.update - count;
    }
}
