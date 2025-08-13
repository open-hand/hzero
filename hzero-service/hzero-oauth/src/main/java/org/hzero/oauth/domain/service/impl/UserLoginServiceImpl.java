package org.hzero.oauth.domain.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.constant.HmsgBootConstant;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.boot.oauth.domain.repository.BaseClientRepository;
import org.hzero.boot.oauth.domain.repository.BaseOpenAppRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserRepository;
import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.user.UserType;
import org.hzero.core.util.AssertUtils;
import org.hzero.oauth.api.dto.Result;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.service.UserLoginService;
import org.hzero.oauth.domain.utils.ConfigGetter;
import org.hzero.oauth.domain.utils.ProfileCode;
import org.hzero.oauth.domain.vo.AuthenticationResult;
import org.hzero.oauth.security.constant.LoginField;
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.exception.LoginExceptions;
import org.hzero.oauth.security.secheck.SecCheckVO;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.social.SocialUserBindService;
import org.hzero.oauth.security.token.MobileLoginTokenService;
import org.hzero.oauth.security.token.OpenLoginTokenService;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.starter.social.core.common.constant.ChannelEnum;
import org.hzero.starter.social.core.common.constant.SocialConstant;

/**
 * 用户登录业务默认实现
 *
 * @author bojiangzhou 2019/02/25
 */
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;
    @Autowired
    private MessageClient messageClient;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private BaseClientRepository clientRepository;
    @Autowired
    private MobileLoginTokenService mobileLoginTokenService;
    @Autowired
    private OpenLoginTokenService openLoginTokenService;
    @Autowired
    private PasswordPolicyManager passwordPolicyManager;
    @Autowired
    private BaseOpenAppRepository baseOpenAppRepository;
    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private ConfigGetter configGetter;
    @Autowired
    private SocialUserBindService socialUserBindService;

    /**
     * 在登录页面根据用户名获取登录用户时，直接从session获取，登录失败时将用户信息放入session中.
     *
     * @param request HttpServletRequest
     */
    @Override
    public User queryRequestUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute(SecurityAttributes.SECURITY_LOGIN_USERNAME);
        if (username != null) {
            return (User) session.getAttribute(SecurityAttributes.SECURITY_LOGIN_USER);
        }
        return null;
    }

    @Override
    public boolean isNeedCaptcha(User user) {
        return userAccountService.isNeedCaptcha(user);
    }

    @Override
    public List<BaseOpenApp> queryOpenLoginWays(String channel) {
        List<BaseOpenApp> apps = baseOpenAppRepository.getOpenApps(channel);
        if (CollectionUtils.isEmpty(apps)) {
            return Collections.emptyList();
        }
        apps.forEach(app -> {
            app.setAppId(null);
            app.setAppKey(null);
        });
        return apps;
    }

    @Override
    public List<BaseOpenApp> queryOpenLoginWays(HttpServletRequest request) {
        String channel = StringUtils.defaultIfBlank(request.getParameter(SocialConstant.PARAM_CHANNEL), ChannelEnum.pc.name());
        return queryOpenLoginWays(channel);
    }

    @Override
    public CaptchaResult sendPhoneCaptcha(String internationalTelCode, String phone, UserType userType,
                                          String businessScope, boolean checkRegistered) {
        CaptchaResult captchaResult = null;
        if (checkRegistered) {
            if (!baseUserRepository.existsByPhone(phone, userType)) {
                captchaResult = new CaptchaResult();
                captchaResult.setSuccess(false);
                captchaResult.setCode(LoginExceptions.PHONE_NOT_FOUND.value());
                captchaResult.setMessage(MessageAccessor.getMessage(LoginExceptions.PHONE_NOT_FOUND.value(), LoginUtil.getLanguageLocale()).desc());
                return captchaResult;
            }
        }

        captchaResult = captchaMessageHelper.generateMobileCaptcha(internationalTelCode, phone, userType,
                businessScope, HZeroService.Oauth.CODE);

        if (!captchaResult.isSuccess()) {
            captchaResult.clearCaptcha();
            return captchaResult;
        }

        Map<String, String> params = new HashMap<>(2);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        try {
            messageClient.async().sendMessage(BaseConstants.DEFAULT_TENANT_ID, configGetter.getValue(ProfileCode.MSG_CODE_MOBILE_LOGIN), null,
                    Collections.singletonList(new Receiver().setPhone(phone).setIdd(internationalTelCode)), params, Collections.singletonList("SMS"));
        } catch (Exception e) {
            captchaResult.setSuccess(false);
            captchaResult.setMessage(MessageAccessor.getMessage("hoth.warn.captcha.sendPhoneCaptchaError", LoginUtil.getLanguageLocale()).desc());
        }

        captchaResult.clearCaptcha();

        return captchaResult;
    }

    @Override
    public Map<String, Object> getLoginInitParams(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(2);

        String clientId = request.getParameter(LoginUtil.FIELD_CLIENT_ID);
        String username = request.getParameter(LoginUtil.FIELD_USERNAME);
        String userType = request.getParameter(UserType.PARAM_NAME);

        Long organizationId = null;
        User user = null;
        //登录验证码策略
        if (StringUtils.isNotBlank(clientId)) {
            BaseClient client = clientRepository.selectClient(clientId);
            if (client != null) {
                organizationId = client.getOrganizationId();
            }
        }

        if (StringUtils.isNotBlank(username)) {
            user = userAccountService.findLoginUser(username, UserType.ofDefault(userType));
            if (user != null) {
                organizationId = user.getOrganizationId();
            }
        }

        // 是否需要验证码
        result.put(SecurityAttributes.FIELD_IS_NEED_CAPTCHA, passwordPolicyManager.isNeedCaptcha(organizationId));
        // 三方登录方式
        result.put(SecurityAttributes.FIELD_OPEN_LOGIN_WAYS, queryOpenLoginWays(request));

        return result;
    }

    @Override
    public AuthenticationResult loginMobileForToken(HttpServletRequest request) {
        return mobileLoginTokenService.loginForToken(request);
    }

    @Override
    public AuthenticationResult loginOpenForToken(HttpServletRequest request) {
        return openLoginTokenService.loginForToken(request);
    }

    @Override
    public AuthenticationResult bindOpenAccount(HttpServletRequest request) {
        return socialUserBindService.bindOpenAccount(request);
    }

    @Override
    public Result forceModifyPwdSendCaptcha(HttpSession session, String internationalTelCode,
                                            String phone, String userType, String businessScope) {
        String sessionPhone = (String) session.getAttribute(SecurityAttributes.SECURITY_LOGIN_MOBILE);
        if (StringUtils.isNotBlank(sessionPhone)) {
            phone = sessionPhone;
        }

        // 生成验证码
        CaptchaResult captchaResult = this.captchaMessageHelper.generateMobileCaptcha(internationalTelCode, phone,
                UserType.ofDefault(userType), businessScope, HZeroService.Oauth.CODE);

        // 发送验证码
        Map<String, String> params = new HashMap<>(2);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        try {
            // 发送手机验证码
            this.messageClient.async().sendMessage(BaseConstants.DEFAULT_TENANT_ID, this.configGetter
                            .getValue(ProfileCode.MSG_CODE_MODIFY_PASSWORD), null,
                    Collections.singletonList(new Receiver().setPhone(phone).setIdd(internationalTelCode)), params,
                    Collections.singletonList(HmsgBootConstant.MessageType.SMS));

            captchaResult.clearCaptcha();
            // 转换结果
            Result result = new Result(captchaResult.isSuccess(), captchaResult.getCode(), captchaResult.getMessage());
            result.setData(captchaResult.getCaptchaKey());

            // 返回结果对象
            return result;
        } catch (Exception e) {
            // 验证码发送失败
            return new Result(false, MessageAccessor.getMessage("hoth.warn.captcha.sendPhoneCaptchaError", LoginUtil.getLanguageLocale()).desc());
        }
    }

    @Override
    public Result secCheckSendCaptcha(HttpSession session, String secCheckType, String internationalTelCode,
                                      String userType, String businessScope) {
        Object attribute = session.getAttribute(SecCheckVO.SEC_CHECK_KEY);
        AssertUtils.isTrue(attribute instanceof SecCheckVO, "Secondary Check Value Object Is Required");
        SecCheckVO secCheckVO = (SecCheckVO) attribute;
        if (!secCheckVO.getSupportTypes().contains(secCheckType)) {
            // 不支持的二次校验类型
            return new Result(false, MessageAccessor.getMessage("hoth.warn.unSupportSecCheckType", secCheckType, LoginUtil.getLanguageLocale()).desc());
        }

        // 账户
        String account = secCheckVO.getAccount(secCheckType);

        CaptchaResult captchaResult;
        if (LoginField.PHONE.code().equals(secCheckType)) {
            captchaResult = captchaMessageHelper.generateMobileCaptcha(internationalTelCode, account, UserType.ofDefault(userType),
                    businessScope, HZeroService.Oauth.CODE);
        } else if (LoginField.EMAIL.code().equals(secCheckType)) {
            captchaResult = this.captchaMessageHelper.generateEmailCaptcha(account, UserType.ofDefault(userType),
                    businessScope, HZeroService.Oauth.CODE);
        } else {
            return new Result(false, MessageAccessor.getMessage("hoth.warn.phoneOrEmailNotInvalid", LoginUtil.getLanguageLocale()).desc());
        }

        if (!captchaResult.isSuccess()) {
            // 验证码发送失败
            return new Result(false, MessageAccessor.getMessage("hoth.warn.captcha.sendCaptchaError", LoginUtil.getLanguageLocale()).desc());
        }

        // 发送验证码
        Map<String, String> params = new HashMap<>(2);
        params.put(CaptchaResult.FIELD_CAPTCHA, captchaResult.getCaptcha());
        try {
            if (LoginField.PHONE.code().equals(secCheckType)) {
                // 发送手机验证码
                this.messageClient.async().sendMessage(BaseConstants.DEFAULT_TENANT_ID, configGetter
                                .getValue(ProfileCode.MSG_CODE_MOBILE_LOGIN), null,
                        Collections.singletonList(new Receiver().setPhone(account).setIdd(internationalTelCode)), params,
                        Collections.singletonList(HmsgBootConstant.MessageType.SMS));
            } else if (LoginField.EMAIL.code().equals(secCheckType)) {
                // 发送邮箱验证码
                this.messageClient.async().sendMessage(BaseConstants.DEFAULT_TENANT_ID, configGetter
                                .getValue(ProfileCode.MSG_CODE_EMAIL_LOGIN), null,
                        Collections.singletonList(new Receiver().setEmail(account)), params,
                        Collections.singletonList(HmsgBootConstant.MessageType.EMAIL));
            }

            captchaResult.clearCaptcha();
            // 转换结果
            Result result = new Result(captchaResult.isSuccess(), captchaResult.getCode(), captchaResult.getMessage());
            result.setData(captchaResult.getCaptchaKey());

            // 返回结果对象
            return result;
        } catch (Exception e) {
            // 验证码发送失败
            return new Result(false, MessageAccessor.getMessage("hoth.warn.captcha.sendCaptchaError", LoginUtil.getLanguageLocale()).desc());
        }
    }
}
