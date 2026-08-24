package org.hzero.boot.oauth.domain.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;

import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.entity.BaseUserInfo;
import org.hzero.boot.oauth.domain.repository.BaseUserInfoRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserRepository;
import org.hzero.boot.oauth.domain.service.BaseUserService;
import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;

public class BaseUserServiceImpl implements BaseUserService {

    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private BasePasswordPolicyRepository basePasswordPolicyRepository;
    @Autowired
    private PasswordErrorTimesService passwordErrorTimesService;
    @Autowired
    private BaseUserInfoRepository baseUserInfoRepository;


    @Override
    public void lockUser(Long userId, Long tenantId) {
        BasePasswordPolicy passwordPolicy = basePasswordPolicyRepository.selectPasswordPolicy(tenantId);
        BaseUser user = baseUserRepository.selectByPrimaryKey(userId);
        BaseUserInfo userInfo = baseUserInfoRepository.selectByPrimaryKey(userId);

        if (user == null || userInfo == null) {
            throw new CommonException("hoth.warn.password.userNotFound");
        }

        long lockedExpiredTime = Optional.ofNullable(passwordPolicy.getLockedExpireTime()).orElse(Integer.MAX_VALUE);

        user.setLocked(true);
        user.setLockedUntilAt(new Date(System.currentTimeMillis() + lockedExpiredTime * 1000));
        baseUserRepository.updateOptional(user, BaseUser.FIELD_LOCKED, BaseUser.FIELD_LOCKED_UNTIL_AT);

        userInfo.setLockedDate(new Date());
        baseUserInfoRepository.updateOptional(userInfo, BaseUserInfo.FIELD_LOCKED_DATE);

        // 清除登录错误次数，使得从数据库更改之后可以登录
        passwordErrorTimesService.clearErrorTimes(userId);
    }

    @Override
    public void unLockUser(Long userId, Long tenantId) {
        BaseUser user = baseUserRepository.selectByPrimaryKey(userId);
        BaseUserInfo userInfo = baseUserInfoRepository.selectByPrimaryKey(userId);

        if (user == null || userInfo == null) {
            throw new CommonException("hoth.warn.password.userNotFound");
        }

        user.setLocked(false);
        user.setLockedUntilAt(null);
        baseUserRepository.updateOptional(user, BaseUser.FIELD_LOCKED, BaseUser.FIELD_LOCKED_UNTIL_AT);

        userInfo.setLockedDate(null);
        baseUserInfoRepository.updateOptional(userInfo, BaseUserInfo.FIELD_LOCKED_DATE);

        // 清除登录错误次数
        passwordErrorTimesService.clearErrorTimes(userId);
    }

}
