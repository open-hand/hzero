package org.hzero.core.captcha;

/**
 * 验证码结果封装
 *
 * @author bojiangzhou 2018/08/10
 */
public class CaptchaResult {

    public static final String FIELD_CAPTCHA = "captcha";
    public static final String FIELD_INTERVAL = "interval";
    public static final String FIELD_CAPTCHA_KEY = "captchaKey";
    public static final String FIELD_LAST_CHECK_KEY = "lastCheckKey";
    /**
     * 字段名：业务范围
     */
    public static final String FIELD_BUSINESS_SCOPE = "businessScope";

    public static final CaptchaResult SUCCESS = new CaptchaResult(true);

    public CaptchaResult() {
    }

    public CaptchaResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 验证码
     */
    private String captcha;
    /**
     * 缓存KEY
     */
    private String captchaKey;
    /**
     * 前置验证KEY
     */
    private String lastCheckKey;
    /**
     * 消息
     */
    private String message;
    /**
     * 消息编码
     */
    private String code;
    /**
     * 是否成功
     */
    private boolean isSuccess;

    /**
     * 间隔时间(秒)
     */
    private long interval;

    /**
     * 错误次数
     */
    private int errorTimes;

    /**
     * 剩余次数
     */
    private int surplusTimes;

    private Object[] messageParams;

    /**
     * 清除验证码
     */
    public void clearCaptcha() {
        this.captcha = null;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getLastCheckKey() {
        return lastCheckKey;
    }

    public void setLastCheckKey(String lastCheckKey) {
        this.lastCheckKey = lastCheckKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }

    public int getSurplusTimes() {
        return surplusTimes;
    }

    public void setSurplusTimes(int surplusTimes) {
        this.surplusTimes = surplusTimes;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(Object[] messageParams) {
        this.messageParams = messageParams;
    }
}
