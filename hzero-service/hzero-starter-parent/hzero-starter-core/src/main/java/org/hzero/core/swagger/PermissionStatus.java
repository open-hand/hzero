package org.hzero.core.swagger;

/**
 * 权限状态
 *
 * @author bojiangzhou 2018/11/22
 */
public enum PermissionStatus {

    /**
     * 未变更，默认状态
     */
    NONE("NONE"),
    /**
     * 升级
     */
    UPGRADE("UPGRADE"),
    /**
     * 返回参数变更
     */
    RETURN_PARAM_MODIFY("RETURN_PARAM_MODIFY"),

    ;

    private String code;

    PermissionStatus(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
