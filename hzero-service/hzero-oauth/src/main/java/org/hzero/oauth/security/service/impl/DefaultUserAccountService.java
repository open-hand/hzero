package org.hzero.oauth.security.service.impl;

import static org.hzero.core.base.BaseConstants.Symbol.MIDDLE_LINE;
import static org.hzero.oauth.security.constant.LoginField.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.repository.BaseClientRepository;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.service.BaseUserService;
import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Regexs;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.repository.UserRepository;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.LoginField;
import org.hzero.oauth.security.constant.LoginType;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.exception.LoginExceptions;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.util.RequestUtil;

/**
 * 用户登录业务默认实现
 *
 * @author bojiangzhou 2019/02/25
 */
public class DefaultUserAccountService implements UserAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserAccountService.class);

    private UserRepository userRepository;
    private BaseUserService baseUserService;
    private SecurityProperties securityProperties;
    private Set<String> supportLoginFields;
    private PasswordPolicyManager passwordPolicyManager;
    private BasePasswordPolicyRepository basePasswordPolicyRepository;
    private BaseClientRepository baseClientRepository;

    public DefaultUserAccountService(UserRepository userRepository,
                                     BaseUserService baseUserService,
                                     PasswordPolicyManager passwordPolicyManager,
                                     BasePasswordPolicyRepository basePasswordPolicyRepository,
                                     BaseClientRepository baseClientRepository,
                                     SecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.baseUserService = baseUserService;
        this.passwordPolicyManager = passwordPolicyManager;
        this.basePasswordPolicyRepository = basePasswordPolicyRepository;
        this.baseClientRepository = baseClientRepository;
        this.securityProperties = securityProperties;

        supportLoginFields = securityProperties.getLogin().getSupportFields();
        Assert.isTrue(CollectionUtils.isNotEmpty(supportLoginFields), "support login fields must not be empty.");
    }

    /**
     * 检查账户有效性
     *
     * @param user not null.
     * @throws AuthenticationException 认证异常
     */
    @Override
    public void checkLoginUser(User user) throws AuthenticationException {

        checkUserAccount(user);

        checkUserTenant(user);

        checkUserLoginField(user);

        checkUserRole(user);
    }

    /**
     * 根据登录方式查找用户
     *
     * @param loginType {@link LoginType}
     * @param username 用户名
     * @return UserDO
     */
    @Override
    public User findLoginUser(LoginType loginType, String username, UserType userType) {
        User user;
        switch (loginType) {
            case ACCOUNT:
                user = queryByLoginField(username, userType);
                break;
            case SMS:
                user = queryByPhone(username, userType);
                break;
            default:
                user = null;
                break;
        }

        return user;
    }

    @Override
    public User findLoginUser(String loginField, String username, UserType userType) {
        User user;
        LoginField val;
        try {
            val = valueOf(loginField.toUpperCase());
        }catch (Exception e){
            throw new RuntimeException("not supported login field : " + loginField);
        }
        switch (val) {
            case PHONE:
                user = queryByPhone(username, userType);
                break;
            case EMAIL:
                user = queryByEmail(username, userType);
                break;
            case USERNAME:
                user = queryByUserName(username, userType);
                break;
            default:
                user = null;
                break;
        }

        return user;
    }

    @Override
    public User findLoginUser(String username, UserType userType) {
        return findLoginUser(LoginType.ACCOUNT, username, userType);
    }

    @Override
    public User findLoginUser(String loginName) {
        return queryByUserName(loginName, null);
    }

    @Override
    public User findLoginUser(Long userId) {
        return userRepository.selectByPrimaryKey(userId);
    }

    /**
     * 校验用户账号
     */
    protected void checkUserAccount(User user) {
        // 账户是否有效
        if (!user.getEnabled()) {
            throw new AuthenticationServiceException(LoginExceptions.USER_NOT_ACTIVATED.value());
        }

        // 账户是否被锁
        if (user.getLocked() != null && user.getLocked()) {
            long nowTime = System.currentTimeMillis();
            Date lockDate = user.getLockedUntilAt();
            if (lockDate != null) {
                long lockUntilTime = lockDate.getTime();
                if (lockUntilTime > nowTime) {
                    // 账号处于被锁期间,返回登录页面
                    throw new CustomAuthenticationException(LoginExceptions.ACCOUNT_LOCKED.value());
                } else {
                    // 解锁用户
                    baseUserService.unLockUser(user.getId(), user.getOrganizationId());
                }
            }
        }

        // 判断账号是否过期
        if (user.getEndDateActive() != null && user.getEndDateActive().isBefore(LocalDate.now())) {
            throw new AuthenticationServiceException(LoginExceptions.ACCOUNT_EXPIRE.value());
        }
    }

    /**
     * 检查用户租户
     */
    protected void checkUserTenant(User user) {
        // 判断租户是否有效
        if (user.getTenantName() == null) {
            throw new CustomAuthenticationException(LoginExceptions.TENANT_INVALID.value());
        }
        if (BaseConstants.Flag.NO.equals(user.getTenantEnabledFlag())) {
            throw new CustomAuthenticationException(LoginExceptions.TENANT_DISABLED.value());
        }
    }

    /**
     * 检查用户登录字段
     */
    protected void checkUserLoginField(User user) {
        // 手机登录时，要求手机必须已验证
        if (PHONE.equals(user.getLoginField()) && BaseConstants.Flag.NO.equals(user.getPhoneCheckFlag())) {
            throw new AuthenticationServiceException(LoginExceptions.PHONE_NOT_CHECK.value());
        }
        // 邮箱登录时，要求邮箱必须已验证
        else if (EMAIL.equals(user.getLoginField())
                        && BaseConstants.Flag.NO.equals(user.getEmailCheckFlag())) {
            throw new AuthenticationServiceException(LoginExceptions.EMAIL_NOT_CHECK.value());
        }
    }

    /**
     * 检查用户分配角色
     */
    protected void checkUserRole(User user) {
        if (BooleanUtils.isTrue(user.getAdmin())) {
            LOGGER.info("Admin user login, skip check role. user is {}", user);
            return;
        }
        // 校验是否有分配角色
        if (CollectionUtils.isEmpty(user.getRoles())) {
            throw new AuthenticationServiceException(LoginExceptions.ROLE_NONE.value());
        }
    }

    protected User queryByLoginField(String account, UserType userType) {
        if (StringUtils.isBlank(account)) {
            return null;
        }

        User user = null;
        LoginField loginField = null;
        if (Regexs.isEmail(account) && supportLoginFields.contains(EMAIL.code())) {
            user = userRepository.selectLoginUserByEmail(account, userType);
            loginField = EMAIL;
        } else if (StringUtils.contains(account, MIDDLE_LINE) && supportLoginFields.contains(PHONE.code())) {
            String[] arr = StringUtils.split(account, MIDDLE_LINE, 2);
            String crownCode = arr[0];
            String mobile = arr[1];
            if (Regexs.isNumber(crownCode) && Regexs.isNumber(mobile) && Regexs.isMobile(crownCode, mobile)) {
                user = userRepository.selectLoginUserByPhone(crownCode, mobile, userType);
                loginField = PHONE;
            }
        } else if (Regexs.isNumber(account) && Regexs.isMobile(account) && supportLoginFields.contains(PHONE.code())) {
            user = userRepository.selectLoginUserByPhone(account, userType);
            loginField = PHONE;
        }

        if (user == null && supportLoginFields.contains(USERNAME.code())) {
            user = userRepository.selectLoginUserByLoginName(account, userType);
            loginField = USERNAME;
        }

        if (user != null) {
            user.setLoginField(loginField);
        }

        return user;
    }

    protected User queryByPhone(String account, UserType userType) {
        if (StringUtils.isBlank(account) || !supportLoginFields.contains(PHONE.code())) {
            return null;
        }

        User user = null;
        // 国际冠码手机号
        if (StringUtils.contains(account, MIDDLE_LINE) && supportLoginFields.contains(PHONE.code())) {
            String[] arr = StringUtils.split(account, MIDDLE_LINE, 2);
            String crownCode = arr[0];
            String mobile = arr[1];
            if (Regexs.isNumber(crownCode) && Regexs.isNumber(mobile) && Regexs.isMobile(crownCode, mobile)) {
                user = userRepository.selectLoginUserByPhone(crownCode, mobile, userType);
            }
        } else if (Regexs.isNumber(account) && Regexs.isMobile(account)) {
            user = userRepository.selectLoginUserByPhone(account, userType);
        }

        if (user != null) {
            user.setLoginField(PHONE);
        }

        return user;
    }

    protected User queryByEmail(String account, UserType userType) {
        if (StringUtils.isBlank(account)) {
            return null;
        }

        User user = null;
        if (Regexs.isEmail(account) && supportLoginFields.contains(EMAIL.code())) {
            user = userRepository.selectLoginUserByEmail(account, userType);
        }

        if (user != null) {
            user.setLoginField(EMAIL);
        }

        return user;
    }

    protected User queryByUserName(String account, UserType userType) {
        if (StringUtils.isBlank(account)) {
            return null;
        }

        User user = null;
        if (supportLoginFields.contains(USERNAME.code())) {
            user = userRepository.selectLoginUserByLoginName(account, userType);
        }

        if (user != null) {
            user.setLoginField(USERNAME);
        }

        return user;
    }

    @Override
    public boolean isNeedCaptcha(User user) {
        BaseUser baseUser = null;
        if (user != null) {
            baseUser = new BaseUser(user.getId(), user.getLoginName(), user.getOrganizationId(), user.getLocked());
        }
        return passwordPolicyManager.isNeedCaptcha(baseUser);
    }

    @Override
    public boolean isPasswordExpired(BasePasswordPolicy basePasswordPolicy, User user) {

        Integer updateRate = basePasswordPolicy.getPasswordUpdateRate();
        Date lastUpdatedAt = user.getLastPasswordUpdatedAt();

        if (updateRate != null && updateRate > 0) {
            // 得到提醒日期
            Date date = DateUtils.addDays(lastUpdatedAt, updateRate);
            Date now = new Date();
            return now.after(date);
        }

        return false;
    }

    @Override
    public boolean isNeedForceModifyPassword(BasePasswordPolicy passwordPolicy, User user) {
        if (Objects.equals(user.getPasswordResetFlag(), BaseConstants.Flag.YES)) {
            return false;
        }
        // 为 0 的时候校验密码策略是否开启了强制修改初始密码的配置
        if (Boolean.FALSE.equals(passwordPolicy.getEnablePassword())) {
            // 安全策略为null或者密码安全策略未启用
            return false;
        }
        // 处理是否强制修改初始密码
        return Boolean.TRUE.equals(passwordPolicy.getForceModifyPassword());
    }


    @Override
    public BasePasswordPolicy getPasswordPolicyByUser(User user) {
        if (user == null) {
            return null;
        }
        BasePasswordPolicy passwordPolicy = basePasswordPolicyRepository.selectPasswordPolicy(user.getOrganizationId());
        if (passwordPolicy == null) {
            LOGGER.warn("password policy not found, organizationId is {}, login user is {}", user.getOrganizationId(), user.getId());
        }
        return passwordPolicy;
    }

    @Override
    @Nullable
    public BaseClient findCurrentClient() {
        String clientId = RequestUtil.getParameterValueFromRequestOrSavedRequest("client_id", null);
        if (StringUtils.isBlank(clientId)) {
            clientId = securityProperties.getLogin().getDefaultClientId();
            if(StringUtils.isBlank(clientId)){
                return null;
            }
        }
        return baseClientRepository.selectClient(clientId);
    }

    //
    // getter
    // ------------------------------------------------------------------------------

    protected UserRepository getUserRepository() {
        return userRepository;
    }

    protected SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    protected BaseUserService getBaseUserService() {
        return baseUserService;
    }

    protected Set<String> getSupportLoginFields() {
        return supportLoginFields;
    }

}
