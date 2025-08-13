package org.hzero.iam.domain.service.user;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.exception.MessageException;
import org.hzero.core.user.UserType;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.MobileCaptchaVerifyVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户验证码相关服务
 *
 * @author bojiangzhou 2018/07/03
 */
public class UserCaptchaService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCheckService userCheckService;
    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;
    @Autowired
    private MessageClient messageClient;

    // ===============================================================================
    // 手机验证码
    // ===============================================================================

    /**
     * 向手机发送验证码，缓存验证码和手机号，返回验证码KEY。
     *
     * @param phone     phone number
     * @param userPhone if phone is user's
     * @return captcha key
     */
    public CaptchaResult sendPhoneCaptcha(String phone, String userType, boolean userPhone, String template) {
        String internationalTelCode = null;
        if (userPhone) {
            // 手机号与原手机号是否匹配
            CustomUserDetails details = UserUtils.getUserDetails();
            User user = userRepository.selectByPrimaryKey(details.getUserId());
            Assert.isTrue(StringUtils.equals(user.getPhone(), phone), "user.send-captcha.phone.error");
            internationalTelCode = user.getInternationalTelCode();
        }

        CaptchaResult captchaResult = captchaMessageHelper.generateMobileCaptcha(phone, userType, Constants.APP_CODE);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }


        Map<String, String> params = new HashMap<>(8);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        try {
            messageClient.async().sendMessage(
                    Constants.SITE_TENANT_ID,
                    template,
                    null,
                    Collections.singletonList(new Receiver().setPhone(phone).setIdd(internationalTelCode)),
                    params,
                    Collections.singletonList("SMS")
            );
        } catch (Exception e) {
            throw new CommonException("hiam.warn.sendPhoneError");
        }

        captchaResult.clearCaptcha();

        return captchaResult;
    }

    /**
     * 向手机发送验证码，缓存验证码和手机号，返回验证码KEY。
     *
     * @param internationalTelCode 冠码
     * @param phone                phone number
     * @param userType             用户类型
     * @param businessScope        验证码业务范围
     * @param userPhone            if phone is user's
     * @return captcha key
     */
    public CaptchaResult sendPhoneCaptcha(String internationalTelCode, String phone, UserType userType, String businessScope,
                                          boolean userPhone, boolean checkRegister, String template) {
        if (checkRegister) {
            if (!userRepository.existsByPhone(phone, userType.value())) {
                throw new CommonException("hiam.warn.user.notFoundWithPhone");
            }
        }

        if (userPhone) {
            // 手机号与原手机号是否匹配
            CustomUserDetails details = UserUtils.getUserDetails();
            User user = userRepository.selectByPrimaryKey(details.getUserId());
            Assert.isTrue(StringUtils.equals(user.getPhone(), phone), "user.send-captcha.phone.error");
            internationalTelCode = user.getInternationalTelCode();
        }

        CaptchaResult captchaResult = captchaMessageHelper.generateMobileCaptcha(internationalTelCode, phone, userType, businessScope, Constants.APP_CODE);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }

        Map<String, String> params = new HashMap<>(8);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        try {
            messageClient.async().sendMessage(
                    Constants.SITE_TENANT_ID,
                    template,
                    null,
                    Collections.singletonList(new Receiver().setPhone(phone).setIdd(internationalTelCode)),
                    params,
                    Collections.singletonList("SMS")
            );
        } catch (Exception e) {
            throw new CommonException("hiam.warn.sendPhoneError");
        }

        captchaResult.clearCaptcha();

        return captchaResult;
    }

    /**
     * 向手机发送验证码，缓存验证码和手机号，返回验证码KEY
     *
     * @param email         email
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     * @param userEmail     if email is user's
     * @return captcha key
     */
    public CaptchaResult sendEmailCaptcha(String email, UserType userType, String businessScope,
                                          boolean userEmail, boolean checkRegister, String template) {
        if (checkRegister) {
            if (!userRepository.existsByEmail(email, userType.value())) {
                throw new CommonException("hiam.warn.user.notFoundWithEmail");
            }
        }

        if (userEmail) {
            // 手机号与原手机号是否匹配
            CustomUserDetails details = UserUtils.getUserDetails();
            User user = userRepository.selectByPrimaryKey(details.getUserId());
            Assert.isTrue(StringUtils.equals(user.getEmail(), email), "user.send-captcha.email.error");
        }

        CaptchaResult captchaResult = captchaMessageHelper.generateEmailCaptcha(email, userType, businessScope, Constants.APP_CODE);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }

        Map<String, String> params = new HashMap<>(8);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        try {
            messageClient.async().sendMessage(
                    Constants.SITE_TENANT_ID,
                    template,
                    null,
                    Collections.singletonList(new Receiver().setEmail(email)),
                    params,
                    Collections.singletonList("EMAIL")
            );
        } catch (Exception e) {
            throw new CommonException("hiam.warn.sendEmailError");
        }

        captchaResult.clearCaptcha();

        return captchaResult;
    }

    /**
     * 验证码校验
     *
     * @param captchaKey    captcha key
     * @param captcha       captcha
     * @param number        phone or email
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    public void validateCaptcha(String captchaKey, String captcha, String number, UserType userType, String businessScope) {
        CaptchaResult captchaResult = captchaMessageHelper.checkCaptcha(captchaKey, captcha, number, userType,
                businessScope, Constants.APP_CODE, false);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }
    }

    /**
     * 验证码校验
     *
     * @param captchaKey    captcha key
     * @param captcha       captcha
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    public void validateCaptcha(String captchaKey, String captcha, UserType userType, String businessScope) {
        CaptchaResult captchaResult = captchaMessageHelper.checkCaptcha(captchaKey, captcha, userType,
                businessScope, Constants.APP_CODE, false);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }
    }

    /**
     * 校验验证码并缓存结果
     *
     * @param captchaKey    captcha key
     * @param captcha       captcha
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     * @return pre check result key
     */
    public CaptchaResult validateCaptchaAndCacheResult(String captchaKey, String captcha, UserType userType, String businessScope) {
        CaptchaResult captchaResult = captchaMessageHelper.checkCaptcha(captchaKey, captcha, userType,
                businessScope, Constants.APP_CODE, true);

        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }

        return captchaResult;
    }

    /**
     * 验证用户密码是否正确，缓存验证结果
     *
     * @param password password
     * @return pre check result key
     */
    public CaptchaResult validatePasswordAndCacheResult(String password, UserType userType, String businessScope) {
        boolean result = userCheckService.checkPasswordRight(password);
        Assert.isTrue(result, "user.validate-password.incorrect");
        return captchaMessageHelper.cacheCheckResult(Constants.APP_CODE, userType, businessScope);
    }

    /**
     * 验证前置结果
     *
     * @param lastCheckKey pre check result key
     * @throws CommonException 验证不通过
     */
    public void validateLastCheckResult(String lastCheckKey, UserType userType,
                                        String businessScope, String captchaCachePrefix) {
        CaptchaResult captchaResult = captchaMessageHelper.checkLastResult(lastCheckKey, userType, businessScope, captchaCachePrefix);
        if (!captchaResult.isSuccess()) {
            throw new MessageException(captchaResult.getMessage(), captchaResult.getCode());
        }
    }

    /**
     * 验证前置结果，验证是否已注册，发送验证码
     *
     * @param lastCheckKey         前一次验证码验证结果KEY
     * @param internationalTelCode 国际冠码
     * @param phone                phone
     * @param userType             用户类型
     * @param businessScope        验证码业务范围
     * @return captcha key
     */
    public CaptchaResult sendPhoneCaptchaAfterLastCheck(String internationalTelCode, String lastCheckKey, String phone,
                                                        UserType userType, String businessScope, String template) {
        // 原手机号/密码验证是否通过
        validateLastCheckResult(lastCheckKey, userType, businessScope, Constants.APP_CODE);
        // 手机是否已注册
        userCheckService.checkPhoneRegistered(phone, userType);

        return sendPhoneCaptcha(internationalTelCode, phone, userType, businessScope, false, false, template);
    }

    /**
     * 验证前置结果，验证是否已注册，发送验证码
     *
     * @param lastCheckKey  pre check result key
     * @param email         email
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     * @return captcha key
     */
    public CaptchaResult sendEmailCaptchaAfterLastCheck(String lastCheckKey, String email, UserType userType,
                                                        String businessScope, String template) {
        // 原手机号/密码验证是否通过
        validateLastCheckResult(lastCheckKey, userType, businessScope, Constants.APP_CODE);
        // 邮箱是否已注册
        userCheckService.checkEmailRegistered(email, userType);

        return sendEmailCaptcha(email, userType, businessScope, false, false, template);
    }

    /**
     * 验证前置结果，验证码手机号，验证验证码
     *
     * @param lastCheckKey  pre check result key
     * @param captchaKey    captcha key
     * @param captcha       captcha
     * @param number        phone or email
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    public void validateCaptchaAfterLastCheck(String lastCheckKey, String captchaKey, String captcha, String number,
                                              UserType userType, String businessScope) {
        // 原手机号/密码验证是否通过
        validateLastCheckResult(lastCheckKey, userType, businessScope, Constants.APP_CODE);

        validateCaptcha(captchaKey, captcha, number, userType, businessScope);
    }

    /**
     * 校验强制手机验证码校验
     *
     * @param passwordPolicy 安全策略
     * @param user           操作用户对象
     * @param verifyVO       用户手机验证码验证消息
     */
    public void validateForceCodeVerify(@Nonnull BasePasswordPolicy passwordPolicy, @Nonnull User user,
                                        @Nonnull MobileCaptchaVerifyVO verifyVO) {
        // 判断是否需要强制使用手机验证码校验
        if (BooleanUtils.isTrue(passwordPolicy.getForceCodeVerify())) {
            // 校验验证码
            this.validateCaptcha(verifyVO.getCaptchaKey(), verifyVO.getCaptcha(), verifyVO.getPhone(),
                    UserType.ofDefault(verifyVO.getUserType()), verifyVO.getBusinessScope());

            // 查询详情
            UserInfo userInfo = this.userRepository.selectUserInfoByPrimaryKey(user.getId());
            if (!BaseConstants.Flag.YES.equals(userInfo.getPhoneCheckFlag())) {
                // 用户账户手机号未验证，请先验证手机号
                throw new CommonException("hiam.error.self.phone.un-check");
            }
            if (!StringUtils.equals(verifyVO.getPhone(), user.getPhone())) {
                // 发送短信的手机号与用户绑定的手机号不符
                throw new CommonException("hiam.error.self.phone.incompatible");
            }
        }
    }

}
