package org.hzero.oauth.security.sms;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.oauth.security.util.RequestUtil;

/**
 * 封装短信验证码
 *
 * @author bojiangzhou 2019/02/25
 */
public class SmsAuthenticationDetails extends WebAuthenticationDetails {
    private static final long serialVersionUID = 3660248343605392800L;

    private String inputCaptcha;
    private String captchaKey;
    private String userType;
    /**
     * 验证码业务范围
     */
    private String businessScope;

    public SmsAuthenticationDetails(HttpServletRequest request) {
        super(request);
        captchaKey = request.getParameter(CaptchaResult.FIELD_CAPTCHA_KEY);
        inputCaptcha = request.getParameter(CaptchaResult.FIELD_CAPTCHA);
        this.businessScope = RequestUtil.getParameterValueFromRequestOrSession(request, CaptchaResult.FIELD_BUSINESS_SCOPE, "");
        this.userType = RequestUtil.getParameterValueFromRequestOrSession(request, UserType.PARAM_NAME, UserType.DEFAULT_USER_TYPE);
    }

    public String getInputCaptcha() {
        return inputCaptcha;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public String getUserType() {
        return userType;
    }

    public String getBusinessScope() {
        return this.businessScope;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        SmsAuthenticationDetails that = (SmsAuthenticationDetails) object;

        return Objects.equals(inputCaptcha, that.inputCaptcha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (inputCaptcha != null ? inputCaptcha.hashCode() : 0);
        return result;
    }
}
