package org.hzero.iam.app.service;

import org.hzero.core.user.UserType;
import org.hzero.iam.api.dto.UserConfigDTO;
import org.hzero.iam.api.dto.UserPasswordDTO;

/**
 * 登录用户应用服务
 *
 * @author bojiangzhou 2019/04/19 优化代码
 */
public interface UserSelfService {

    //
    // 登录用户操作
    // ------------------------------------------------------------------------------

    /**
     * 首先验证原手机验证码是否已经验证通过，然后验证用户新手机验证码是否正确，验证通过后更新手机号
     *
     * @param lastCheckKey  原手机校验的验证码
     * @param captchaKey    验证码
     * @param captcha       验证码
     * @param phone         新手机
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    void validateNewPhoneCaptchaAndUpdate(String lastCheckKey, String captchaKey, String captcha, String phone,
                                          UserType userType, String businessScope);

    /**
     * 首先验证原手机验证码是否已经验证通过，然后验证用户新邮箱验证码是否正确，验证通过后更新邮箱
     *
     * @param lastCheckKey  保存了旧手机号验证码结果的key
     * @param captchaKey    新手机号验证码的key
     * @param captcha       验证码
     * @param email         邮箱
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    void validateNewEmailCaptchaAndUpdate(String lastCheckKey, String captchaKey, String captcha, String email,
                                          UserType userType, String businessScope);

    /**
     * 更新用户名
     *
     * @param realName 用户名
     */
    void updateUserRealName(String realName);

    /**
     * 更新用户的默认角色
     *
     * @param roleId 角色ID
     */
    void updateUserDefaultRole(Long roleId, Long tenantId);

    /**
     * 更新用户的默认公司
     *
     * @param companyId 公司ID
     */
    void updateUserDefaultCompany(Long companyId, Long tenantId);

    /**
     * 更新用户的默认租户
     *
     * @param defaultTenantId 默认租户Id
     */
    void updateUserDefaultTenant(Long defaultTenantId);

    /**
     * 更新用户时区
     *
     * @param timeZone 时区
     */
    void updateUserTimeZone(String timeZone);

    /**
     * 更新用户语言
     *
     * @param language 语言
     */
    void updateUserDefaultLanguage(String language);

    /**
     * 更新用户日期时间格式
     *
     * @param dateFormat 日期格式
     * @param timeFormat 时间格式
     */
    void updateUserDatetimeFormat(String dateFormat, String timeFormat);

    /**
     * 更新用户头像
     *
     * @param avatar 用户头像
     */
    void updateUserAvatar(String avatar);

    /**
     * 用户手机认证
     *
     * @param captchaKey    验证码KEY
     * @param captcha       验证码
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    void validateCaptchaAndAuthenticatePhone(String captchaKey, String captcha, UserType userType, String businessScope);

    /**
     * 用户邮箱认证
     *
     * @param captchaKey    验证码KEY
     * @param captcha       验证码
     * @param userType      用户类型
     * @param businessScope 验证码业务范围
     */
    void validateCaptchaAndAuthenticateEmail(String captchaKey, String captcha, UserType userType, String businessScope);

    /**
     * 用户修改自己的密码
     */
    void selfUpdatePassword(UserPasswordDTO userPasswordDTO);

    /**
     * 检查用户原始密码是否正确
     *
     * @param originalPassword 原始密码
     */
    void checkSelfPasswordRight(String originalPassword);

    /**
     * 更新用户菜单布局样式
     *
     * @param userConfigDTO 菜单布局样式，可以为空
     */
    void updateUserConfigItems(UserConfigDTO userConfigDTO);
}
