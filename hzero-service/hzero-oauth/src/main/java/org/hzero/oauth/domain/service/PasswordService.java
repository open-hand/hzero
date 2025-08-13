package org.hzero.oauth.domain.service;

import org.hzero.core.user.UserType;
import org.hzero.oauth.domain.vo.MobileCaptchaVerifyVO;

/**
 * 密码服务
 *
 * @author bojiangzhou 2019/03/04
 */
public interface PasswordService {

    /**
     * 通过手机号或邮箱修改密码
     *
     * @param account       手机号/邮箱
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     * @param password      密码
     * @param captchaKey    验证码KEY
     * @param captcha       验证码
     */
    void updatePasswordByAccount(String account, UserType userType, String businessScope,
                                 String password, String captchaKey, String captcha);

    /**
     * 修改用户密码
     *
     * @param userId   用户Id
     * @param userType 用户类型
     * @param password 密码
     * @param verifyVO 手机验证码校验参数对象
     */
    void updatePasswordByUser(Long userId, UserType userType, String password, MobileCaptchaVerifyVO verifyVO);
}
