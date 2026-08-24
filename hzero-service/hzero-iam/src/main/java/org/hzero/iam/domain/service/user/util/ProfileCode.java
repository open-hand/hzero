package org.hzero.iam.domain.service.user.util;

/**
 * 用户相关 配置常量编码
 *
 * @author bojiangzhou 2019/10/22
 */
public enum ProfileCode {

    /**
     * 修改密码成功：是否发送消息
     */
    IF_SEND_MODIFY_PASSWORD("HIAM.IF_SEND_MODIFY_PASSWORD", "1", "hzero.send-message.send-modify-password-success"),
    /**
     * 消息代码：修改密码成功
     */
    MSG_CODE_MODIFY_PASSWORD(null, "HIAM.MODIFY_PASSWORD_SUCCESS", "hzero.send-message.modify-password-success"),
    /**
     * 消息代码：修改密码成功(消息中带有新密码)
     */
    MSG_CODE_MODIFY_PWD_WITH_PWD(null, "HIAM.MOD_PWD_SUCC_WITH_PWD", "hzero.send-message.modify-pwd-success-with-pwd"),

    /**
     * 创建用户：是否发送消息
     */
    IF_SEND_CREATE_USER("HIAM.IF_SEND_CREATE_USER", "1", "hzero.send-message.send-create-user"),
    /**
     * 消息代码：平台创建用户
     */
    MSG_CODE_CREATE_USER(null, "HIAM.CREATE_USER", "hzero.send-message.create-user"),
    /**
     * 消息代码：C端创建用户
     */
    MSG_CODE_CREATE_USER_APP(null, "HIAM.CREATE_USER_APP", "hzero.send-message.create-user-app"),
    /**
     * 消息代码：用户注册
     */
    MSG_CODE_REGISTER_USER(null, "HIAM.REGISTER_USER", "hzero.send-message.register-user"),
    /**
     * 消息代码：找回密码
     */
    MSG_CODE_FIND_PASSWORD(null, "HIAM.FIND_PASSWORD", "hzero.send-message.find-password"),
    /**
     * 消息代码：验证码手机
     */
    MSG_CODE_VALIDATE_PHONE(null, "HIAM.VALIDATE_PHONE", "hzero.send-message.validate-phone"),
    /**
     * 消息代码：验证码邮箱
     */
    MSG_CODE_VALIDATE_EMAIL(null, "HIAM.VALIDATE_EMAIL", "hzero.send-message.validate-email"),
    /**
     * 消息代码：修改手机
     */
    MSG_CODE_MODIFY_PHONE(null, "HIAM.MODIFY_PHONE", "hzero.send-message.modify-phone"),
    /**
     * 消息代码：修改邮箱
     */
    MSG_CODE_MODIFY_EMAIL(null, "HIAM.MODIFY_EMAIL", "hzero.send-message.modify-email"),
    /**
     * 消息参数：首页地址
     */
    MSG_PARAM_INDEX_URL("HIAM.INDEX_URL", "http://front.hzero.org", "hzero.send-message.index-url"),

    ;

    private String profileKey;
    private String defaultValue;
    private String configKey;

    ProfileCode (String profileKey, String defaultValue, String configKey) {
        this.profileKey = profileKey;
        this.defaultValue = defaultValue;
        this.configKey = configKey;
    }

    public String profileKey() {
        return profileKey;
    }

    public String defaultValue() {
        return defaultValue;
    }

    public String configKey() {
        return configKey;
    }

}
