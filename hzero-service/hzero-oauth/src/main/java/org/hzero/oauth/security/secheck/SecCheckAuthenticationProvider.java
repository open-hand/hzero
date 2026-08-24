package org.hzero.oauth.security.secheck;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.core.util.AssertUtils;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.constant.LoginField;
import org.hzero.oauth.security.custom.CustomUserDetailsService;
import org.hzero.oauth.security.exception.AccountNotExistsException;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.exception.ErrorWithTimesException;
import org.hzero.oauth.security.exception.LoginExceptions;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 二次校验登录认证器
 * <p>
 * 参考 {@link AbstractUserDetailsAuthenticationProvider}，{@link DaoAuthenticationProvider}
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecCheckAuthenticationProvider.class);

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final CustomUserDetailsService userDetailsService;
    private final CaptchaMessageHelper captchaMessageHelper;
    private final UserAccountService userAccountService;
    private final LoginRecordService loginRecordService;

    public SecCheckAuthenticationProvider(CustomUserDetailsService userDetailsService,
                                          CaptchaMessageHelper captchaMessageHelper,
                                          UserAccountService userAccountService,
                                          LoginRecordService loginRecordService) {
        this.userDetailsService = userDetailsService;
        this.captchaMessageHelper = captchaMessageHelper;
        this.userAccountService = userAccountService;
        this.loginRecordService = loginRecordService;
    }

    /**
     * 只有 {@link SecCheckAuthenticationToken} 类型才使用该认证器
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (SecCheckAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SecCheckAuthenticationToken.class, authentication,
                "Only SecCheckAuthenticationToken is supported");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        AssertUtils.notNull(requestAttributes, "Request Attributes Required");

        // 获取二次校验类型
        String secCheckType = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

        // 转换Token对象
        SecCheckAuthenticationToken authenticationToken = (SecCheckAuthenticationToken) authentication;
        SecCheckVO secCheckVO = this.getSecCheckVO(requestAttributes, secCheckType);
        AssertUtils.isTrue(secCheckVO.getSupportTypes().contains(secCheckType), "Unsupported Secondary Check Type: " + secCheckType);

        // 获取账户
        String account = secCheckVO.getAccount(secCheckType);

        // 获取用户信息
        UserDetails user = this.retrieveUser(secCheckType, secCheckVO.getAccount(secCheckType), authenticationToken);
        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

        // 校验
        this.additionalAuthenticationChecks(secCheckType, account, user, authenticationToken);

        // 清除session数据
        requestAttributes.removeAttribute(SecCheckVO.SEC_CHECK_KEY, RequestAttributes.SCOPE_SESSION);
        // 校验成功，返回成功的认证对象
        return this.createSuccessAuthentication(user, authentication, user);
    }

    /**
     * 获取用户信息
     *
     * @param secCheckType   二次校验类型
     * @param account        账户
     * @param authentication 授权对象
     * @return 用户信息
     * @throws AuthenticationException 认证异常
     */
    protected UserDetails retrieveUser(String secCheckType, String account, SecCheckAuthenticationToken authentication)
            throws AuthenticationException {
        // 获取当前登录用户信息
        User user = this.userAccountService.findLoginUser(secCheckType, account, this.getUserType(authentication));
        if (user == null) {
            throw new AccountNotExistsException(LoginExceptions.PHONE_NOT_FOUND.value());
        }

        // 临时存储用户信息
        this.loginRecordService.saveLocalLoginUser(user);

        // 获取用户详情
        return this.getUserDetailsService().loadUserByUsername(account);
    }

    /**
     * 额外的认证校验
     *
     * @param secCheckType   二次校验类型
     * @param account        账户
     * @param userDetails    用户详情
     * @param authentication 认证对象
     * @throws AuthenticationException 认证异常
     */
    protected void additionalAuthenticationChecks(String secCheckType, String account, UserDetails userDetails,
                                                  SecCheckAuthenticationToken authentication)
            throws AuthenticationException {

        // 检查验证码
        this.checkCaptcha(account, authentication);
        // 校验账户情况
        this.checkAccount(secCheckType, account);
    }

    /**
     * 创建成功认证结果对象
     *
     * @param user 用户信息
     * @return 成功认证结果对象
     */
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        // 返回认证结果
        SecCheckAuthenticationToken result = new SecCheckAuthenticationToken(principal,
                this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());

        // 返回认值结果
        return result;
    }

    /**
     * 获取用户详情服务对象
     *
     * @return 用户详情服务对象
     */
    protected UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    /**
     * 获取验证码Helper
     *
     * @return 验证码Helper
     */
    protected CaptchaMessageHelper getCaptchaMessageHelper() {
        return this.captchaMessageHelper;
    }

    /**
     * 获取二次校验数据对象
     *
     * @param requestAttributes 请求属性对象
     * @param secCheckType      二次校验类型
     * @return 二次校验数据对象
     */
    private SecCheckVO getSecCheckVO(RequestAttributes requestAttributes, String secCheckType) {
        // session
        Object attribute = requestAttributes.getAttribute(SecCheckVO.SEC_CHECK_KEY, RequestAttributes.SCOPE_SESSION);
        AssertUtils.isTrue(attribute instanceof SecCheckVO, "Secondary Check Value Object Is Required");

        // 返回二次校验数据对象
        return (SecCheckVO) attribute;
    }

    /**
     * 获取用户类型
     *
     * @param authentication 授权对象
     * @return 用户类型
     */
    private UserType getUserType(SecCheckAuthenticationToken authentication) {
        String userType = null;
        Object details = authentication.getDetails();
        userType = ((SecCheckAuthenticationDetails) details).getUserType();
        return UserType.ofDefault(userType);
    }

    /**
     * 检查账户
     *
     * @param secCheckType 二次校验类型
     * @param account      账户
     */
    private void checkAccount(String secCheckType, String account) {
        // 获取用户信息
        User localLoginUser = this.loginRecordService.getLocalLoginUser();

        if (LoginField.PHONE.code().equals(secCheckType)) {
            if (BooleanUtils.isNotTrue(localLoginUser.getSecCheckPhoneFlag())) {
                // 手机号二次校验未开启
                throw new CustomAuthenticationException(LoginExceptions.PHONE_SECONDARY_CHECK_NOT_OPEN.value());
            }

            if (!BaseConstants.Flag.YES.equals(localLoginUser.getPhoneCheckFlag())) {
                // 手机号未绑定
                throw new CustomAuthenticationException(LoginExceptions.PHONE_NOT_CHECK.value());
            }

            if (!StringUtils.equals(localLoginUser.getPhone(), account)) {
                // 绑定的手机号与校验的手机号不一致
                throw new CustomAuthenticationException(LoginExceptions.PHONE_BINDING_AND_INPUT_DIFF.value());
            }
        } else if (LoginField.EMAIL.code().equals(secCheckType)) {
            if (BooleanUtils.isNotTrue(localLoginUser.getSecCheckEmailFlag())) {
                // 邮箱二次校验未开启
                throw new CustomAuthenticationException(LoginExceptions.EMAIL_SECONDARY_CHECK_NOT_OPEN.value());
            }

            if (!BaseConstants.Flag.YES.equals(localLoginUser.getEmailCheckFlag())) {
                // 邮箱未绑定
                throw new CustomAuthenticationException(LoginExceptions.EMAIL_NOT_CHECK.value());
            }

            if (!StringUtils.equals(localLoginUser.getEmail(), account)) {
                // 绑定的邮箱与校验的邮箱不一致
                throw new CustomAuthenticationException(LoginExceptions.EMAIL_BINDING_AND_INPUT_DIFF.value());
            }
        } else {
            // 不支持的二次校验类型
            throw new CustomAuthenticationException(LoginExceptions.UNSUPPORTED_SECONDARY_CHECK_TYPE.value(), secCheckType);
        }
    }

    /**
     * 检查验证码
     *
     * @param account        账户
     * @param authentication 授权对象
     */
    private void checkCaptcha(String account, SecCheckAuthenticationToken authentication) {
        Assert.isInstanceOf(SecCheckAuthenticationDetails.class, authentication.getDetails());
        // 认证详细信息
        SecCheckAuthenticationDetails details = (SecCheckAuthenticationDetails) authentication.getDetails();

        // 检查验证码
        String inputCaptcha = details.getInputCaptcha();
        String captchaKey = details.getCaptchaKey();
        String businessScope = details.getBusinessScope();
        if (StringUtils.isAnyEmpty(inputCaptcha, captchaKey)) {
            throw new CustomAuthenticationException(LoginExceptions.CAPTCHA_NULL.value());
        }

        // 校验结果
        CaptchaResult captchaResult = this.getCaptchaMessageHelper().checkCaptcha(captchaKey, inputCaptcha, account,
                this.getUserType(authentication), businessScope, HZeroService.Oauth.CODE, false);

        if (!captchaResult.isSuccess()) {
            ErrorWithTimesException ex = new ErrorWithTimesException(captchaResult.getCode());
            ex.setErrorTimes(captchaResult.getErrorTimes());
            ex.setSurplusTimes(captchaResult.getSurplusTimes());
            throw ex;
        }
    }
}
