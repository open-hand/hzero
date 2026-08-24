package io.choerodon.core.ldap;

/**
 * Created by superlee on 2018/10/12.
 */
public enum DirectoryType {

    OPEN_LDAP("OpenLDAP"),

    MICROSOFT_ACTIVE_DIRECTORY("Microsoft Active Directory");

    private final String value;

    DirectoryType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
