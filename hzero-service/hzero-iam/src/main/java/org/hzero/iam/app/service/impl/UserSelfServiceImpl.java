package org.hzero.iam.app.service.impl;

import java.util.*;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.boot.platform.common.CommonClient;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.user.UserType;
import org.hzero.core.util.TokenUtils;
import org.hzero.iam.api.dto.UserConfigDTO;
import org.hzero.iam.api.dto.UserPasswordDTO;
import org.hzero.iam.app.service.UserSelfService;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserConfigRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.repository.UserSelfRepository;
import org.hzero.iam.domain.service.user.UserCaptchaService;
import org.hzero.iam.domain.service.user.UserCheckService;
import org.hzero.iam.domain.service.user.UserDetailsService;
import org.hzero.iam.domain.service.user.util.ConfigGetter;
import org.hzero.iam.domain.service.user.util.ProfileCode;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.feign.OauthAdminFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 当前用户业务
 *
 * @author bojiangzhou 2019/04/19
 */
public class UserSelfServiceImpl implements UserSelfService {

    @Autowired
    protected UserCheckService userCheckService;
    @Autowired
    protected UserCaptchaService userCaptchaService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserConfigRepository userConfigRepository;
    @Autowired
    protected UserDetailsService userDetailsService;
    @Autowired
    protected MessageClient messageClient;
    @Autowired
    protected UserPasswordService userPasswordService;
    @Autowired
    protected ConfigGetter configGetter;
    @Autowired
    protected EncryptClient encryptClient;
    @Autowired
    protected BasePasswordPolicyRepository passwordPolicyRepository;
    @Autowired
    protected UserSelfRepository userSelfRepository;
    @Autowired
    private OauthAdminFeignClient oauthAdminFeignClient;
    @Autowired
    private CommonClient commonClient;

    @Override
    public void validateNewPhoneCaptchaAndUpdate(String lastCheckKey, String captchaKey, String captcha, String phone,
                                                 UserType userType, String businessScope) {
        userCaptchaService.validateCaptchaAfterLastCheck(lastCheckKey, captchaKey, captcha, phone, userType, businessScope);
        userCheckService.checkPhoneRegistered(phone, userType);
        // 更新手机号
        CustomUserDetails self = UserUtils.getUserDetails();
        User dbUser = userRepository.selectSimpleUserById(self.getUserId());
        User user = new User();
        user.setPhone(phone);
        user.setId(self.getUserId());
        user.setObjectVersionNumber(dbUser.getObjectVersionNumber());
        userRepository.updateOptional(user, User.FIELD_PHONE);

        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(self.getUserId());
        userInfo.setPhoneCheckFlag(BaseConstants.Flag.YES);
        userRepository.updateUserInfoByPrimaryKey(userInfo);
    }

    @Override
    public void validateNewEmailCaptchaAndUpdate(String lastCheckKey, String captchaKey, String captcha, String email,
                                                 UserType userType, String businessScope) {
        userCaptchaService.validateCaptchaAfterLastCheck(lastCheckKey, captchaKey, captcha, email, userType, businessScope);
        userCheckService.checkEmailRegistered(email, userType);
        // 更新邮箱号
        CustomUserDetails self = UserUtils.getUserDetails();
        User dbUser = userRepository.selectSimpleUserById(self.getUserId());
        User user = new User();
        user.setEmail(email);
        user.setId(self.getUserId());
        user.setObjectVersionNumber(dbUser.getObjectVersionNumber());
        userRepository.updateOptional(user, User.FIELD_EMAIL);

        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(self.getUserId());
        userInfo.setEmailCheckFlag(BaseConstants.Flag.YES);
        userRepository.updateUserInfoByPrimaryKey(userInfo);
    }

    /**
     * 更改用户真实姓名
     *
     * @param realName 真实姓名
     */
    @Override
    public void updateUserRealName(String realName) {
        CustomUserDetails self = UserUtils.getUserDetails();
        User user = new User();
        User dbUser = userRepository.selectSimpleUserById(self.getUserId());
        user.setId(self.getUserId());
        user.setRealName(realName);
        user.setObjectVersionNumber(dbUser.getObjectVersionNumber());
        userRepository.updateOptional(user, User.FIELD_REAL_NAME);

        userRepository.cacheUser(user.getId());
    }

    /**
     * 更改默认角色
     *
     * @param roleId   角色ID
     * @param tenantId 租户ID
     */
    @Override
    public void updateUserDefaultRole(Long roleId, Long tenantId) {
        CustomUserDetails self = UserUtils.getUserDetails();
        // 判断默认角色是否在当前用户角色范围内
        if (!self.getRoleIds().contains(roleId)) {
            // 操作错误，当前用户未分配指定的默认角色
            throw new CommonException("hiam.error.user.update-default-role.not-exists");
        }

        UserConfig param = new UserConfig();
        param.setTenantId(tenantId);
        param.setUserId(self.getUserId());
        UserConfig userConfig = userConfigRepository.selectOne(param);
        if (userConfig != null) {
            userConfig.setDefaultRoleId(roleId);
            userRepository.updateUserConfigByPrimaryKey(userConfig);
        } else {
            UserConfig config = new UserConfig();
            config.setUserId(self.getUserId());
            config.setTenantId(tenantId);
            config.setDefaultRoleId(roleId);
            userRepository.insertUserConfigSelective(config);
        }
    }

    /**
     * 更改默认公司
     *
     * @param companyId 公司ID
     * @param tenantId  租户ID
     */
    @Override
    public void updateUserDefaultCompany(Long companyId, Long tenantId) {
        CustomUserDetails details = UserUtils.getUserDetails();

        UserConfig param = new UserConfig();
        param.setUserId(details.getUserId());
        param.setTenantId(tenantId);
        UserConfig userConfig = userConfigRepository.selectOne(param);
        if (userConfig != null) {
            userConfig.setDefaultCompanyId(companyId);
            userRepository.updateUserConfigByPrimaryKey(userConfig);
        } else {
            UserConfig config = new UserConfig();
            config.setUserId(details.getUserId());
            config.setTenantId(tenantId);
            config.setDefaultCompanyId(companyId);
            userRepository.insertUserConfigSelective(config);
        }
    }

    @Override
    public void updateUserDefaultTenant(Long defaultTenantId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(customUserDetails.getUserId());
        Assert.notNull(userInfo, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        userInfo.setDefaultTenantId(defaultTenantId);
        userRepository.updateUserInfoByPrimaryKey(userInfo);
    }

    /**
     * 更改用户时区，同时更改 UserDetails
     *
     * @param timeZone 时区
     */
    @Override
    public void updateUserTimeZone(String timeZone) {
        CustomUserDetails self = UserUtils.getUserDetails();
        User dbUser = userRepository.selectSimpleUserById(self.getUserId());
        User user = new User();
        user.setTimeZone(timeZone);
        user.setId(self.getUserId());
        user.setObjectVersionNumber(dbUser.getObjectVersionNumber());
        userRepository.updateOptional(user, User.FIELD_TIME_ZONE);

        userDetailsService.storeUserTimeZone(timeZone);
    }

    /**
     * 更改默认语言，同时更改 UserDetails
     *
     * @param language 语言
     */
    @Override
    public void updateUserDefaultLanguage(String language) {
        CustomUserDetails self = UserUtils.getUserDetails();
        User dbUser = userRepository.selectSimpleUserById(self.getUserId());
        User user = new User();
        user.setLanguage(language);
        user.setId(self.getUserId());
        user.setObjectVersionNumber(dbUser.getObjectVersionNumber());
        userRepository.updateOptional(user, User.FIELD_LANGUAGE);

        if (StringUtils.isBlank(language)) {
            language = commonClient.getSystemConfigByConfigCode(Constants.Config.TENANT_DEFAULT_LANGUAGE, self.getTenantId());
        }
        userDetailsService.storeUserLanguage(language);

        // 更新用户语言缓存
        userRepository.cacheUser(user.getId());
    }

    /**
     * 更改日期、时间格式
     *
     * @param dateFormat 日期格式
     * @param timeFormat 时间格式
     */
    @Override
    public void updateUserDatetimeFormat(String dateFormat, String timeFormat) {
        CustomUserDetails self = UserUtils.getUserDetails();
        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(self.getUserId());
        userInfo.setUserId(self.getUserId());
        if (StringUtils.isNotBlank(dateFormat)) {
            userInfo.setDateFormat(dateFormat);
        }
        if (StringUtils.isNotBlank(timeFormat)) {
            userInfo.setTimeFormat(timeFormat);
        }
        userRepository.updateUserInfoByPrimaryKey(userInfo);
    }

    /**
     * 更改头像
     *
     * @param avatar 头像路径
     */
    @Override
    public void updateUserAvatar(String avatar) {
        CustomUserDetails self = UserUtils.getUserDetails();
        User dbUser = userRepository.selectSimpleUserById(self.getUserId());
        User user = new User();
        user.setId(self.getUserId());
        user.setImageUrl(avatar);
        user.setObjectVersionNumber(dbUser.getObjectVersionNumber());
        userRepository.updateOptional(user, User.FIELD_IMAGE_URL);
    }

    @Override
    public void validateCaptchaAndAuthenticatePhone(String captchaKey, String captcha, UserType userType, String businessScope) {
        userCaptchaService.validateCaptcha(captchaKey, captcha, userType, businessScope);
        CustomUserDetails self = UserUtils.getUserDetails();
        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(self.getUserId());
        userInfo.setPhoneCheckFlag(BaseConstants.Flag.YES);
        userRepository.updateUserInfoByPrimaryKey(userInfo);
    }

    @Override
    public void validateCaptchaAndAuthenticateEmail(String captchaKey, String captcha, UserType userType, String businessScope) {
        userCaptchaService.validateCaptcha(captchaKey, captcha, userType, businessScope);
        CustomUserDetails self = UserUtils.getUserDetails();
        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(self.getUserId());
        userInfo.setEmailCheckFlag(BaseConstants.Flag.YES);
        userRepository.updateUserInfoByPrimaryKey(userInfo);
    }

    @Override
    public void selfUpdatePassword(UserPasswordDTO userPasswordDTO) {
        // 检查用户是否存在
        CustomUserDetails self = UserUtils.getUserDetails();

        User user = userRepository.selectByPrimaryKey(self.getUserId());
        Assert.notNull(user, String.format("user with id %s not found.", self.getUserId()));

        String originalPassword = encryptClient.decrypt(userPasswordDTO.getOriginalPassword());
        String password = encryptClient.decrypt(userPasswordDTO.getPassword());

        BasePasswordPolicy passwordPolicy = passwordPolicyRepository.selectPasswordPolicy(self.getOrganizationId());
        // 处理强制使用手机验证码校验
        this.userCaptchaService.validateForceCodeVerify(passwordPolicy, user, userPasswordDTO);

        // 校验原密码是否正确
        boolean enableSecurity = Optional.ofNullable(passwordPolicy.getEnableSecurity()).orElse(false);
        if (!user.comparePassword(originalPassword)) {
            long maxErrorTime = Optional.ofNullable(passwordPolicy.getMaxErrorTime()).orElse(0);
            if (enableSecurity) {
                // 增加错误次数
                long errorTimes = userSelfRepository.increaseErrorTimes(user.getId());
                if (errorTimes >= maxErrorTime) {
                    // 超过最大次数，清除token
                    oauthAdminFeignClient.invalidByUsername(user.getLoginName());
                    userSelfRepository.clearErrorTimes(user.getId());
                }
                throw new CommonException("error.password.originalPassword.modify", maxErrorTime - errorTimes);
            }
            throw new CommonException("error.password.originalPassword");
        }

        // 更改密码
        userPasswordService.updateUserPassword(self.getUserId(), password);
        userSelfRepository.clearErrorTimes(self.getUserId());
        if (configGetter.isTrue(user.getOrganizationId(), ProfileCode.IF_SEND_MODIFY_PASSWORD)) {
            // 发送站内信
            sendMessageAfterUpdatePassword(user, password);
        }
        // 检查是否需要重新登录
        String accessToken = TokenUtils.getToken();
        if (BooleanUtils.isTrue(passwordPolicy.getEnablePassword())
                && BooleanUtils.isTrue(passwordPolicy.getLoginAgain())
                && StringUtils.isNotBlank(accessToken)) {
            oauthAdminFeignClient.invalidByToken(accessToken);
        }
    }

    /**
     * 修改密码后，发送消息，默认发送站内信
     *
     * @param user 用户
     */
    protected void sendMessageAfterUpdatePassword(User user, String password) {
        // 发送站内信
        Map<String, String> args = new HashMap<>(1);
        args.put(User.FIELD_LOGIN_NAME, user.getLoginName());

        try {
            Receiver receiver = new Receiver()
                    .setUserId(user.getId())
                    .setTargetUserTenantId(user.getOrganizationId())
                    .setEmail(user.getEmail())
                    .setPhone(user.getPhone());

            messageClient.async().sendMessage(
                    configGetter.getValue(user.getOrganizationId(), ProfileCode.MSG_CODE_MODIFY_PASSWORD),
                    Collections.singletonList(receiver),
                    args
            );
        } catch (Exception e) {
            throw new CommonException("hiam.warn.sendMessageError");
        }
    }

    /**
     * 检查用户密码是否正确
     *
     * @param originalPassword 原密码
     */
    @Override
    public void checkSelfPasswordRight(String originalPassword) {
        CustomUserDetails details = UserUtils.getUserDetails();
        // 检查用户ldap及原密码
        User user = userRepository.selectByPrimaryKey(details.getUserId());
        // 校验原始密码是否正确
        if (!user.comparePassword(encryptClient.decrypt(originalPassword))) {
            throw new CommonException("error.password.originalPassword");
        }
    }

    @Override
    public void updateUserConfigItems(UserConfigDTO configDTO) {
        CustomUserDetails details = UserUtils.getUserDetails();
        UserConfig userConfig = new UserConfig();
        userConfig.setUserId(details.getUserId());
        userConfig.setTenantId(details.getTenantId());
        UserConfig dbUserConfig = userConfigRepository.selectOne(userConfig);
        if (Objects.equals(null, dbUserConfig)) {
            // 数据库中不存在配置项，插入一条配置值，包含用户Id，租户Id以及配置项
            userConfig.setMenuLayout(configDTO.getMenuLayout());
            userConfig.setRoleMergeFlag(configDTO.getRoleMergeFlag());
            userConfig.setPopoutReminderFlag(configDTO.getPopoutReminderFlag());
            userConfigRepository.insertSelective(userConfig);
        } else {
            // 判断哪些配置需要更新(字段非空即为需要更新的配置)
            // 更新菜单布局配置，可以更新为空，为default时获取系统配置下配置的菜单布局配置信息
            if (configDTO.getMenuLayout() != null) {
                dbUserConfig.setMenuLayout(StringUtils.equals(UserConfigDTO.DEFAULT, configDTO.getMenuLayout())
                        ? null : configDTO.getMenuLayout());
            }
            // 更新角色合并标识，可以更新为空，为-1时获取系统配置下配置的角色合并标识信息
            if (configDTO.getRoleMergeFlag() != null) {
                dbUserConfig.setRoleMergeFlag(UserConfigDTO.NEGATIVE.equals(configDTO.getRoleMergeFlag())
                        ? null : configDTO.getRoleMergeFlag());
            }
            // 更新弹框提醒标识，可以更新为空
            if (configDTO.getPopoutReminderFlag() != null) {
                dbUserConfig.setPopoutReminderFlag(UserConfigDTO.NEGATIVE.equals(configDTO.getPopoutReminderFlag())
                        ? null : configDTO.getPopoutReminderFlag());
            }
            userConfigRepository.updateByPrimaryKey(dbUserConfig);
        }
    }

}
