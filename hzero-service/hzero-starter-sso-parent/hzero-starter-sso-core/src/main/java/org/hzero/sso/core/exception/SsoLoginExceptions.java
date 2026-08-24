package org.hzero.sso.core.exception;

/**
 * @author bojiangzhou
 */
public enum SsoLoginExceptions {

    /**
     * 单点认证服务器响应异常
     */
    SSO_SERVER_ERROR("hoth.warn.sso.serverError"),

    /**
     * 获取服务端用户失败
     */
    SSO_SERVER_USER_NOT_FOUND("hoth.warn.sso.serverUserNotFound"),

    /**
     * 客户端登录用户不存在
     */
    SSO_CLIENT_USER_NOT_FOUND("hoth.warn.sso.clientUserNotFound"),

    /**
     * 不支持的单点登录方式
     */
    SSO_NOT_SUPPORTED("hoth.warn.sso.notSupportedSso"),



    ;

    private final String value;

    SsoLoginExceptions(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
