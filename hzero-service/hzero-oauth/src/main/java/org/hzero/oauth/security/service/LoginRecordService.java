package org.hzero.oauth.security.service;

import java.util.concurrent.TimeUnit;

import org.hzero.common.HZeroService;
import org.hzero.oauth.domain.entity.User;

/**
 * 登录记录服务
 *
 * @author bojiangzhou 2019/02/25
 */
public interface LoginRecordService {

    String LOGOUT_REDIRECT_URL_PREFIX = HZeroService.Oauth.CODE + ":logout_redirect_url:";

    /**
     * 登录失败记录
     *
     * @param user 用户
     * @return 返回还可以登录几次
     */
    long loginError(User user);

    /**
     * 获取登录失败次数
     */
    long getErrorTimes(User user);

    /**
     * 登录成功记录
     *
     * @param user 用户
     */
    void loginSuccess(User user);

    /**
     * 将登录用户存储到线程变量中，避免多次查询
     *
     * @param user 登录用户
     */
    void saveLocalLoginUser(User user);

    /**
     * 获取线程变量中的登录用户
     *
     * @return user 登录用户
     */
    User getLocalLoginUser();

    /**
     * 清除线程变量中的登录用户
     */
    void clearLocalLoginUser();

    /**
     * 登录成功后，记录登出地址
     *
     * @param tokenValue        access_token
     * @param logoutRedirectUrl HttpServletRequest
     */
    void recordLogoutUrl(String tokenValue, String logoutRedirectUrl);

    /**
     * 清除登出地址
     *
     * @param tokenValue access_token
     */
    void removeLogoutUrl(String tokenValue);

    /**
     * 获取记录的登录跳转地址，并
     */
    String getLogoutUrl(String tokenValue);

    boolean existsLogoutUrl(String tokenValue);

    /**
     * 保存密码
     *
     * @param clientName 客户端
     * @param pass       密码
     * @param expire     过期时间
     * @param timeUnit   时间单位
     * @return true - pass 存在； false - pass 不存在
     */
    boolean savePassIfAbsent(String clientName, String pass, long expire, TimeUnit timeUnit);

}
