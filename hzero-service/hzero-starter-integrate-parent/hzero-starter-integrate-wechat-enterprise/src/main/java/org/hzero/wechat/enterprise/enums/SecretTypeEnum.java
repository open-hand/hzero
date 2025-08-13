package org.hzero.wechat.enterprise.enums;

/**
 * @Author J
 * @Date 2019/10/22
 */
public enum SecretTypeEnum {
    /**
     * 获取企业的secret
     */
    CORP("CORP"),
    /**
     * 获取应用到secret
     */
    APP("APP");

    private String code;

    SecretTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
