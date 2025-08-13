package org.hzero.sso.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bojiangzhou 2020/08/18
 */
public enum SsoEnum {

    CAS("cas"),

    CAS2("cas2"),

    CAS3("cas3"),

    SAML("saml"),

    IDM("idm"),

    AUTH("auth"),

    AZURE("azure"),

    STANDARD("standard"),

    NULL("null")
    ;

    private static final Map<String, SsoEnum> map;

    static {
        map = new HashMap<>();
        for (SsoEnum item : SsoEnum.values()) {
            map.put(item.code, item);
        }
    }

    private final String code;

    SsoEnum(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static SsoEnum match(String code) {
        return map.get(code);
    }
}
