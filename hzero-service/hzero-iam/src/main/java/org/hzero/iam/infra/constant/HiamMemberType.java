package org.hzero.iam.infra.constant;


public enum HiamMemberType {
    /**
     * 用户类型
     */
    USER("user"),

    /**
     * 客户端
     */
    CLIENT("client");

    private final String value;

    HiamMemberType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
