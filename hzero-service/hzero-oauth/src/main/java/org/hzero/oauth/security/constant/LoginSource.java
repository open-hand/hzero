package org.hzero.oauth.security.constant;

/**
 * 登录来源
 *
 * @author bojiangzhou 2019/06/03
 */
public enum  LoginSource {

    /**
     * web端
     */
    WEB("web"),

    /**
     * 移动端
     */
    APP("app")

    ;

    private String value;

    LoginSource(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
