package org.hzero.oauth.security.secheck;

import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.oauth.security.util.RequestUtil;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 封装二次校验参数
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckAuthenticationDetails extends WebAuthenticationDetails {
    /**
     * 输入的验证码
     */
    private final String inputCaptcha;
    /**
     * 验证码的key
     */
    private final String captchaKey;
    /**
     * 用户类型
     */
    private final String userType;
    /**
     * 验证码业务范围
     */
    private final String businessScope;

    public SecCheckAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.captchaKey = request.getParameter(CaptchaResult.FIELD_CAPTCHA_KEY);
        this.inputCaptcha = request.getParameter(CaptchaResult.FIELD_CAPTCHA);

        this.userType = RequestUtil.getParameterValueFromRequestOrSession(request, UserType.PARAM_NAME,
                UserType.DEFAULT_USER_TYPE);
        this.businessScope = RequestUtil.getParameterValueFromRequestOrSession(request, CaptchaResult.FIELD_BUSINESS_SCOPE,
                "");
    }

    public String getInputCaptcha() {
        return this.inputCaptcha;
    }

    public String getCaptchaKey() {
        return this.captchaKey;
    }

    public String getUserType() {
        return this.userType;
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

        SecCheckAuthenticationDetails that = (SecCheckAuthenticationDetails) object;

        return Objects.equals(this.inputCaptcha, that.inputCaptcha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.inputCaptcha != null ? this.inputCaptcha.hashCode() : 0);
        return result;
    }
}
