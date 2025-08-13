package org.hzero.plugin.platform.hr.infra.enums;

public enum HrSyncTypeEnum {
    WX("WX"), DD("DD"), ALL("ALL");

    private String value;

    private HrSyncTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}