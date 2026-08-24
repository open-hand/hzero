package org.hzero.boot.oauth.domain.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.choerodon.core.exception.CommonException;

import org.hzero.boot.oauth.domain.entity.BasePasswordHistory;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.entity.BaseUserInfo;
import org.hzero.boot.oauth.domain.repository.BasePasswordHistoryRepository;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserInfoRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserRepository;
import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.CheckStrength;

/**
 *
 * @author bojiangzhou 2019/08/07
 */
public class UserPasswordServiceImpl implements UserPasswordService {

    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private BasePasswordPolicyRepository basePasswordPolicyRepository;
    @Autowired
    private BaseUserInfoRepository baseUserInfoRepository;
    @Autowired
    private BasePasswordHistoryRepository basePasswordHistoryRepository;
    @Autowired
    private PasswordPolicyManager passwordPolicyManager;
    @Autowired
    private PasswordErrorTimesService passwordErrorTimesService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void updateUserPassword(Long userId, String newPassword) {
        updateUserPassword(userId, newPassword, false);
    }

    @Override
    public void resetUserPassword(Long userId, Long tenantId,
                                  boolean ldapUpdatable) {
        updateUserPassword(userId, getTenantDefaultPassword(tenantId), ldapUpdatable);
    }

    @Override
    public void updateUserPassword(Long userId, String newPassword, boolean ldapUpdatable) {
        BaseUser user = baseUserRepository.selectByPrimaryKey(userId);
        BaseUserInfo userInfo = baseUserInfoRepository.selectByPrimaryKey(userId);

        if (user == null || userInfo == null) {
            throw new CommonException("hoth.warn.password.userNotFound");
        }

        if (!ldapUpdatable) {
            checkLdapUser(user);
        }

        checkPasswordSame(user, newPassword);

        checkPasswordPolicy(user, newPassword);

        recordHistoryPassword(user);

        updatePassword(user, newPassword);

        updateUserInfo(user, userInfo, newPassword);

        afterHandle(user);
    }

    @Override
    public String getTenantDefaultPassword(Long tenantId) {
        BasePasswordPolicy passwordPolicy = basePasswordPolicyRepository.selectPasswordPolicy(tenantId);

        return StringUtils.defaultIfBlank(passwordPolicy.getOriginalPassword(), DEFAULT_PASSWORD);
    }

    /**
     * Ldap 用户不能更改密码
     */
    protected void checkLdapUser(BaseUser user) {
        if (user.getLdap() != null && user.getLdap()) {
            throw new CommonException("hoth.warn.password.ldapUserCantUpdatePassword");
        }
    }

    /**
     * 校验新密码不能与原密码相同
     */
    protected void checkPasswordSame(BaseUser user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CommonException("hoth.warn.password.same");
        }
    }

    /**
     * 校验新密码是否符合密码策略
     */
    protected void checkPasswordPolicy(BaseUser user, String newPassword) {
        passwordPolicyManager.passwordValidate(newPassword, user.getOrganizationId(), user);
    }

    /**
     * 记录历史密码
     */
    protected void recordHistoryPassword(BaseUser user) {
        BasePasswordHistory passwordHistory = new BasePasswordHistory(user.getId(), user.getPassword()).setTenantId(user.getOrganizationId());
        basePasswordHistoryRepository.insertSelective(passwordHistory);
    }

    /**
     * 更新密码
     */
    protected void updatePassword(BaseUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordUpdatedAt(new Date());
        user.setLocked(false);
        user.setLockedUntilAt(null);
        baseUserRepository.updateOptional(user,
                BaseUser.FIELD_PASSWORD,
                BaseUser.FIELD_LAST_PASSWORD_UPDATED_AT,
                BaseUser.FIELD_LOCKED,
                BaseUser.FIELD_LOCKED_UNTIL_AT
        );
    }

    protected void updateUserInfo(BaseUser user, BaseUserInfo baseUserInfo, String newPassword) {
        baseUserInfo.setPasswordResetFlag(BaseConstants.Flag.YES);
        baseUserInfo.setSecurityLevelCode(CheckStrength.getPasswordLevel(newPassword).name());
        baseUserInfoRepository.updateOptional(baseUserInfo,
                BaseUserInfo.FIELD_PASSWORD_RESET_FLAG,
                BaseUserInfo.FIELD_SECURITY_LEVEL_CODE);
    }

    protected void afterHandle(BaseUser user) {
        user.setPassword(null);
        passwordErrorTimesService.clearErrorTimes(user.getId());
    }

}
