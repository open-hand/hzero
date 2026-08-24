package org.hzero.iam.infra.constant;

import java.util.HashMap;
import java.util.Map;

import io.choerodon.core.exception.CommonException;

/**
 * 菜单类型
 */
public enum HiamMenuType {

    /**
     * 根目录 root
     */
    ROOT("root"),
    /**
     * 目录 dir
     */
    DIR("dir"),
    /**
     * 菜单 menu
     */
    MENU("menu"),
    /**
     * 外部链接
     */
    LINK("link"),
    /**
     * 内部链接
     */
    INNER_LINK("inner-link"),
    /**
     * 窗口
     */
    WINDOW("window"),
    /**
     * 权限集 ps
     */
    PS("ps"),

    ;

    private static final Map<String, HiamMenuType> TYPES = new HashMap<>(4);

    static {
        for (HiamMenuType item : HiamMenuType.values()) {
            TYPES.put(item.value(), item);
        }
    }

    private String value;

    HiamMenuType(String value) {
        this.value = value;
    }

    public static HiamMenuType match(String type) {
        return TYPES.get(type);
    }

    public static void throwExceptionNotMatch(String type) {
        if (TYPES.get(type) == null) {
            throw new CommonException("error.menuType.illegal");
        }
    }

    public String value() {
        return this.value;
    }
}