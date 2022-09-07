package org.hzero.boot.oauth.domain.service;

/**
 * 登录尝试次数资源库
 *
 * @author bojiangzhou 2019/08/06
 */
public interface PasswordErrorTimesService {

    /**
     * 增加密码错误次数
     *
     * @param userId 用户ID
     */
    long increaseErrorTimes(Long userId);

    /**
     * 清除密码错误次数
     *
     * @param userId 用户ID
     */
    void clearErrorTimes(Long userId);

    /**
     * 获取密码错误次数
     *
     * @param userId 用户ID
     */
    long getErrorTimes(Long userId);

}
