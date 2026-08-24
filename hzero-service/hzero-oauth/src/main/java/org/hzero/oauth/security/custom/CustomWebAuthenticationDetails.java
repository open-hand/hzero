package org.hzero.oauth.security.custom;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.oauth.security.constant.LoginSource;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.oauth.security.util.RequestUtil;

/**
 * 封装验证码
 *
 * @author bojiangzhou 2019/02/26
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private static final long serialVersionUID = 8396440657058238247L;

    public static final String FIELD_CACHE_CAPTCHA = "cacheCaptcha";

    private String inputCaptcha;
    private String cacheCaptcha;
    private String loginField;
    private String userType;
    private String sourceType;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        cacheCaptcha = (String) request.getAttribute(FIELD_CACHE_CAPTCHA);
        inputCaptcha = request.getParameter(CaptchaResult.FIELD_CAPTCHA);
        loginField = RequestUtil.getParameterValueFromRequestOrSession(request, LoginUtil.FIELD_LOGIN_FIELD, null);
        userType = RequestUtil.getParameterValueFromRequestOrSession(request, UserType.PARAM_NAME, UserType.DEFAULT_USER_TYPE);
        sourceType = RequestUtil.getParameterValueFromRequestOrSession(request, LoginUtil.FIELD_SOURCE_TYPE, LoginSource.WEB.value());
    }

    public String getInputCaptcha() {
        return inputCaptcha;
    }

    public String getCacheCaptcha() {
        return cacheCaptcha;
    }

    public String getLoginField() {
        return loginField;
    }

    public String getUserType() {
        return userType;
    }

    public String getSourceType() {
        return sourceType;
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

        CustomWebAuthenticationDetails that = (CustomWebAuthenticationDetails) object;

        return Objects.equals(inputCaptcha, that.inputCaptcha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (inputCaptcha != null ? inputCaptcha.hashCode() : 0);
        return result;
    }
}
