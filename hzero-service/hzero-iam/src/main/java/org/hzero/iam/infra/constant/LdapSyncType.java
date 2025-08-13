package org.hzero.iam.infra.constant;

/**
 * Ldap 同步用户类型
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/11 11:01
 */
public enum LdapSyncType {

    /**
     * 同步离职用户
     */
    SYNC_LEAVE("SYNC_LEAVE"),
    /**
     * 同步用户
     */
    SYNC_USER("SYNC_USER");

    private final String value;

    LdapSyncType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
