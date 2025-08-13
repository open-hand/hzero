package org.hzero.iam.infra.constant;

/**
 * 权限类型，LOV也是一种权限
 *
 * @author bojiangzhou 2019/01/29
 */
public enum PermissionType {

    PERMISSION("permission"),

    LOV("lov")

    ;

    private String code;

    PermissionType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
