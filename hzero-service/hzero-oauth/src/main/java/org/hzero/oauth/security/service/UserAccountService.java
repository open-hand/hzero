package org.hzero.oauth.security.service;

import javax.annotation.Nullable;

import org.springframework.security.core.AuthenticationException;

import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.core.user.UserType;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.constant.LoginType;

/**
 * 用户登录相关业务
 *
 * @author bojiangzhou 2019/02/25
 */
public interface UserAccountService {

    /**
     * 校验用户信息
     *
     * @param user 登录用户
     * @throws AuthenticationException 认证异常
     */
    void checkLoginUser(User user) throws AuthenticationException;

    /**
     * 查询登录用户
     *
     * @param loginType 登录方式
     * @param account   loginName/phone/email
     */
    User findLoginUser(LoginType loginType, String account, UserType userType);

    /**
     * 查询登陆用户
     *
     * @param loginField 具体登陆方式
     * @param account    loginName/phone/email
     */
    User findLoginUser(String loginField, String account, UserType userType);

    /**
     * 查询登录用户
     *
     * @param account loginName/phone/email
     */
    User findLoginUser(String account, UserType userType);

    /**
     * 根据 loginName 查询登录用户
     *
     * @param loginName 用户名
     */
    User findLoginUser(String loginName);

    /**
     * 查询登录用户
     *
     * @param userId 用户ID
     */
    User findLoginUser(Long userId);

    /**
     * 是否需要验证码
     *
     * @param user 用户
     */
    boolean isNeedCaptcha(User user);

    /**
     * 是否需要修改密码
     *
     * @param user 用户
     */
    boolean isPasswordExpired(BasePasswordPolicy basePasswordPolicy, User user);

    /**
     * 是否需要强制修改初始密码
     *
     * @param user 用户
     */
    boolean isNeedForceModifyPassword(BasePasswordPolicy basePasswordPolicy, User user);

    /**
     * 获取当前用户下的安全策略
     *
     * @param user 用户信息
     * @return 安全策略
     */
    BasePasswordPolicy getPasswordPolicyByUser(User user);

    /**
     * 查询当前客户端
     *
     * @return Client
     */
    @Nullable
    BaseClient findCurrentClient();
}
