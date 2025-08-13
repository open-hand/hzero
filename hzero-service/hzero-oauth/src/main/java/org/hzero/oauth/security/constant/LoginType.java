package org.hzero.oauth.security.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录类型
 *
 * @author bojiangzhou 2018/09/29
 */
public enum LoginType {

    /**
     * 账户登录，包含 USERNAME/EMAIL/PHONE
     */
    ACCOUNT("account"),
    /**
     * 手机登录
     */
    SMS("sms"),
    /**
     * 三方账号登录
     */
    OPEN_ID("openid")

    ;

    private String code;

    LoginType(String code) {
        this.code = code;
    }

    private static final Map<String, LoginType> MAP;

    static {
        MAP = new HashMap<>(LoginType.values().length);
        for (LoginType value : LoginType.values()) {
            MAP.put(value.code(), value);
        }
    }

    public static LoginType match(String way) {
        return MAP.get(way);
    }

    public String code() {
        return code;
    }

}
