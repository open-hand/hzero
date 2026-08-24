package org.hzero.common;

/**
 * 用户来源
 *
 * @author bojiangzhou 2018/11/15
 */
public enum UserSource {

    /**
     * 由管理员创建
     */
    ADMIN_CREATE(0),

    /**
     * 用户自己注册
     */
    SELF_REGISTER(1),

    ;

    private Integer code;

    UserSource(Integer code) {
        this.code = code;
    }

    public Integer code() {
        return code;
    }
}
