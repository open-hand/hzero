package org.hzero.report.infra.enums;

/**
 * SQL类型
 *
 * @author xianzhi.chen@hand-china.com 2018年11月20日下午4:01:15
 */
public enum SqlTypeEnum {
    /**
     * Simple
     */
    SIMPLE("S"),

    /**
     * complex
     */
    COMPLEX("C");

    private final String value;

    SqlTypeEnum(final String value) {
        this.value = value;
    }

    public static SqlTypeEnum valueOf2(String arg) {
        switch (arg) {
            case "S":
                return SIMPLE;
            case "C":
                return COMPLEX;
            default:
                return SIMPLE;
        }
    }

    public static boolean isInEnum(String value) {
        for (SqlTypeEnum databaseType : SqlTypeEnum.values()) {
            if (databaseType.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value;
    }
}
