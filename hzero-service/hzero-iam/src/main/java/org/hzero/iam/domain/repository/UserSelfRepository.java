package org.hzero.iam.domain.repository;

/**
 * 登录用户领域服务
 *
 * @author fanghan.liu 2020/06/03 16:41
 */
public interface UserSelfRepository {

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

}
