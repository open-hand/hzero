package org.hzero.iam.domain.service.secgrp.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全组权限类型
 *
 * @author xingxingwu.hand-china.com 2019/12/09 11:32
 */
public enum SecGrpAuthorityType {
    ACL("ACL", "访问权限"),
    ACL_FIELD("ACL_FIELD", "字段权限"),
    ACL_DASHBOARD("ACL_DASHBOARD", "工作台"),
    DCL_DIM("DCL_DIM", "数据维度"),
    DCL_DIM_LINE("DCL_DIM_LINE", "数据维度行"),
    DCL("DCL", "数据权限");

    private String value;
    private String desc;

    SecGrpAuthorityType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private static final Map<String, SecGrpAuthorityType> MAP = new HashMap<>(8);

    static {
        for (SecGrpAuthorityType type : SecGrpAuthorityType.values()) {
            MAP.put(type.value, type);
        }
    }

    public static SecGrpAuthorityType typeOf(String type) {
        SecGrpAuthorityType authorityType = MAP.get(type);
        if (null == authorityType) {
            throw new IllegalArgumentException("secGrp authority type illegal.");
        }
        return authorityType;
    }

    public String value() {
        return this.value;
    }

    public String desc() {
        return this.desc;
    }
}
