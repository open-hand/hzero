package org.hzero.iam.infra.constant;

/**
 * Ldap 用户同步频率
 *
 * @author yuqing.zhang@hand-china.com 2020/05/11 16:42
 */
public enum LdapSyncFrequency {
    /**
     * 每天一次
     */
    DAY("DAY"),
    /**
     * 每周一次
     */
    WEEK("WEEK"),
    /**
     * 每月一次
     */
    MONTH("MONTH");

    private final String value;

    LdapSyncFrequency(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
