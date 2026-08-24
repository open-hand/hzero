package org.hzero.oauth.security.constant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可支持的登录字段
 *
 * @author bojiangzhou 2019/02/26
 */
public enum LoginField {

    USERNAME("username"),

    PHONE("phone"),

    EMAIL("email");

    private String code;

    LoginField(String code) {
        this.code = code;
    }

    public static Set<String> codes() {
        return Arrays.stream(LoginField.values()).map(LoginField::code).collect(Collectors.toSet());
    }

    public String code() {
        return code;
    }
}
