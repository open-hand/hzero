package org.hzero.oauth.domain.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.exception.MessageException;
import org.hzero.core.user.UserType;
import org.hzero.oauth.domain.utils.ConfigGetter;
import org.hzero.oauth.domain.utils.ProfileCode;

/**
 * 用户验证码相关服务
 *
 * @author bojiangzhou 2018/07/03
 */
@Component
public class UserCaptchaService {

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;
    @Autowired
    private MessageClient messageClient;
    @Autowired
    private ConfigGetter configGetter;

    // ===============================================================================
    // 手机验证码
    // ===============================================================================

    /**
     * 向手机发送验证码，缓存验证码和手机号，返回验证码KEY。
     *
     * @param internationalTelCode 国冠码
     * @param phone                phone number
     * @param userType             用户类型
     * @param businessScope        验证码业务范围
     * @return captcha key
     */
    public CaptchaResult sendPhoneCaptcha(String internationalTelCode, String phone, UserType userType, String businessScope) {
        CaptchaResult captchaResult = captchaMessageHelper.generateMobileCaptcha(phone, userType,
                businessScope, HZeroService.Oauth.CODE);

        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }

        Map<String, String> params = new HashMap<>(2);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        messageClient
                .async()
                .sendMessage(
                        BaseConstants.DEFAULT_TENANT_ID,
                        configGetter.getValue(ProfileCode.MSG_CODE_MODIFY_PASSWORD),
                        null,
                        Collections.singletonList(new Receiver().setPhone(phone).setIdd(internationalTelCode)),
                        params,
                        Collections.singletonList("SMS")
                );

        captchaResult.clearCaptcha();

        return captchaResult;
    }

    /**
     * 向手机发送验证码，缓存验证码和手机号，返回验证码KEY
     *
     * @param email         email
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     * @return captcha key
     */
    public CaptchaResult sendEmailCaptcha(String email, UserType userType, String businessScope) {
        CaptchaResult captchaResult = captchaMessageHelper.generateEmailCaptcha(email, userType,
                businessScope, HZeroService.Oauth.CODE);

        if (!captchaResult.isSuccess()) {
            return captchaResult;
        }

        Map<String, String> params = new HashMap<>(2);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        messageClient
                .async()
                .sendMessage(
                        BaseConstants.DEFAULT_TENANT_ID,
                        configGetter.getValue(ProfileCode.MSG_CODE_MODIFY_PASSWORD),
                        null,
                        Collections.singletonList(new Receiver().setEmail(email)),
                        params,
                        Collections.singletonList("EMAIL")
                );

        captchaResult.clearCaptcha();

        return captchaResult;
    }

}