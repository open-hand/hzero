package org.hzero.iam.app.service.impl;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.constant.HmsgBootConstant;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.service.BaseUserService;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.user.UserType;
import org.hzero.core.util.AssertUtils;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.repository.UserInfoRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.user.*;
import org.hzero.iam.domain.service.user.interceptor.UserInterceptorChainManager;
import org.hzero.iam.domain.service.user.interceptor.UserOperation;
import org.hzero.iam.domain.service.user.util.ConfigGetter;
import org.hzero.iam.domain.service.user.util.ProfileCode;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.SecCheckType;
import org.hzero.iam.infra.feign.OauthAdminFeignClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author bojiangzhou 2019/04/19 代码优化
 * @author allen 2018/6/29
 */
public class UserServiceImpl implements UserService, AopProxy<UserServiceImpl> {

    private static final String DEFAULT_PASSWORD = "123456";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    protected ConfigGetter configGetter;
    @Autowired
    private UserCreateService userCreateService;
    @Autowired
    private UserUpdateService userUpdateService;
    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private LovAdapter lovAdapter;
    @Autowired
    private UserCreateInternalService userCreateInternalService;
    @Autowired
    private OauthAdminFeignClient oauthAdminService;
    @Autowired
    private UserPasswordService userPasswordService;
    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    protected UserCheckService userCheckService;
    @Autowired
    private UserCaptchaService userCaptchaService;
    @Autowired
    private EncryptClient encryptClient;

    @Autowired
    private UserInterceptorChainManager userInterceptorChainManager;

    @Autowired
    protected BasePasswordPolicyRepository passwordPolicyRepository;
    @Autowired
    protected MessageClient messageClient;

    @Autowired
    @Qualifier("IamCommonAsyncTaskExecutor")
    private ThreadPoolExecutor commonAsyncTaskExecutor;

    //
    // 用户注册
    // ------------------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(User user) {
        userInterceptorChainManager.doInterceptor(UserOperation.REGISTER_USER, user, (u) -> {
            // 注册用户
            userRegisterService.registerUser(u);
        });

        return user;
    }

    //
    // 用户管理
    // ------------------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        userInterceptorChainManager.doInterceptor(UserOperation.CREATE_USER, user, (u) -> {
            // 创建用户
            userCreateService.createUser(u);
        });

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUserInternal(User user) {
        userInterceptorChainManager.doInterceptor(UserOperation.CREATE_USER_INTERNAL, user, (u) -> {
            // 创建用户
            userCreateInternalService.createUser(u);
        });

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        userInterceptorChainManager.doInterceptor(UserOperation.UPDATE_USER, user, (u) -> {
            // 更新用户
            userUpdateService.updateUser(u);
        });

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUserInternal(User user) {
        userInterceptorChainManager.doInterceptor(UserOperation.UPDATE_USER_INTERNAL, user, (u) -> {
            // 更新用户
            userUpdateService.updateUser(u);
        });

        return user;
    }

    @Override
    public User importCreateUser(User user) {
        userInterceptorChainManager.doInterceptor(UserOperation.IMPORT_USER, user, (u) -> {
            // 内部创建用户
            userCreateInternalService.createUser(u);
        });

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockUser(Long userId, Long organizationId) {
        User user = userRepository.selectSimpleUserByIdAndTenantId(userId, organizationId);
        Assert.notNull(user, "hiam.warn.user.notFound");
        baseUserService.lockUser(userId, organizationId);
        //clean token
        oauthAdminService.invalidByUsername(user.getLoginName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockUser(Long userId, Long organizationId) {
        baseUserService.unLockUser(userId, organizationId);
    }

    @Override
    public void frozenUser(Long userId, Long organizationId) {
        User user = userRepository.selectSimpleUserByIdAndTenantId(userId, organizationId);
        Assert.notNull(user, "hiam.warn.user.notFound");
        if (BooleanUtils.isTrue(user.getEnabled())) {
            // 非冻结的用户才需冻结
            user.frozen();
            userRepository.updateOptional(user, User.FIELD_ENABLED);
            //clean token
            oauthAdminService.invalidByUsername(user.getLoginName());
        }
    }

    @Override
    public void unfrozenUser(Long userId, Long organizationId) {
        User user = userRepository.selectSimpleUserByIdAndTenantId(userId, organizationId);
        Assert.notNull(user, "hiam.warn.user.notFound");
        user.unfrozen();
        userRepository.updateOptional(user, User.FIELD_ENABLED);
    }

    @Override
    public void updateUserPassword(Long userId, Long organizationId, UserPasswordDTO userPasswordDTO) {
        userPasswordDTO.init();

        // 当前用户ID
        Long selfId = UserUtils.getUserDetails().getUserId();
        // 查询当前用户信息
        User self = userRepository.selectByPrimaryKey(selfId);
        Assert.notNull(self, String.format("user with id %s not found.", selfId));

        // 查询待更新的用户信息
        User user = userRepository.selectSimpleUserByIdAndTenantId(userId, organizationId);
        if (user == null) {
            throw new CommonException("hiam.warn.user.notFound");
        }
        // 不让修改管理员密码
        if (user.getAdmin() != null && user.getAdmin()) {
            throw new CommonException("hiam.warn.user.modifyAdminPassDeny");
        }
        // 判断是否为当前登录用户
        if (userId.equals(UserUtils.getUserDetails().getUserId())) {
            throw new CommonException("hiam.warn.user.modifySelfPassDeny");
        }

        // 查询当前用户的密码策略
        BasePasswordPolicy passwordPolicy = this.passwordPolicyRepository.selectPasswordPolicy(self.getOrganizationId());
        // 处理强制使用手机验证码校验: 是当前用户更改子账户密码，所以需要根据当前用户的密码策略判断是否需要进行强制手机验证码校验
        this.userCaptchaService.validateForceCodeVerify(passwordPolicy, self, userPasswordDTO);
        user.setOrganizationId(organizationId);
        updateUserPassword(user, userPasswordDTO.getPassword(), userPasswordDTO.isPasswordIsEncrypt());
    }

    @Override
    public void resetUserPassword(Long userId, Long organizationId, UserPasswordDTO userPasswordDTO) {
        // 查询待重置密码的用户的密码策略
        BasePasswordPolicy passwordPolicy = this.passwordPolicyRepository.selectPasswordPolicy(organizationId);
        // 设置租户的默认密码
        userPasswordDTO.setPassword(StringUtils.defaultIfBlank(passwordPolicy.getOriginalPassword(), DEFAULT_PASSWORD));
        userPasswordDTO.setPasswordIsEncrypt(Boolean.FALSE);

        // 更新密码
        this.updateUserPassword(userId, organizationId, userPasswordDTO);
    }

    /**
     * 更新用户密码
     *
     * @param user      待处理的用户对象
     * @param password  用户的新密码
     * @param isEncrypt 密码是否加密
     */
    private void updateUserPassword(User user, String password, boolean isEncrypt) {
        if (isEncrypt) {
            // 解密
            password = encryptClient.decrypt(password);
        }
        // 判断是否启用密码策略校验
        if (user.getCheckPasswordPolicy() == null || user.getCheckPasswordPolicy()) {
            // 检查密码是否符合密码策略
            userCheckService.checkPasswordPolicy(password, user.getOrganizationId());
        }
        userPasswordService.updateUserPassword(user.getId(), password);

        // 判断在修改密码之后是否需要发送消息
        if (this.configGetter.isTrue(user.getOrganizationId(), ProfileCode.IF_SEND_MODIFY_PASSWORD)) {
            // 用户新密码
            String userNewPassword = password;
            // 开启子线程发送消息
            this.commonAsyncTaskExecutor.execute(() -> {
                // 发送消息
                this.sendMessageAfterUpdatePassword(user, userNewPassword);
            });
        }
    }

    @Override
    public List<Map<String, String>> listIDD() {
        List<Map<String, String>> result = new ArrayList<>(256);
        List<LovValueDTO> lov = lovAdapter.queryLovValue(Constants.IDD_LOV_CODE, Constants.SITE_TENANT_ID);
        lov = lov.stream().sorted(Comparator.comparing(LovValueDTO::getOrderSeq)).collect(Collectors.toList());
        lov.forEach(item -> {
            Map<String, String> map = new HashMap<>(BaseConstants.Digital.FOUR);
            map.put("internationalTelCode", item.getValue());
            map.put("internationalTelMeaning", item.getMeaning());
            result.add(map);
        });
        return result;
    }

    @Override
    public void updateUserPasswordByPhone(PasswordDTO passwordDTO, UserType userType) {
        User params = new User();
        params.setPhone(passwordDTO.getPhone());
        params.setUserType(userType.value());
        User user = userRepository.selectOne(params);
        if (user == null) {
            throw new CommonException("hiam.warn.user.notFoundWithPhone");
        }
        updateUserPassword(user, passwordDTO.getPassword(), true);
    }

    @Override
    public void updateUserPasswordByEmail(PasswordDTO passwordDTO, UserType userType) {
        User params = new User();
        params.setEmail(passwordDTO.getEmail());
        params.setUserType(userType.value());
        User user = userRepository.selectOne(params);
        if (user == null) {
            throw new CommonException("hiam.warn.user.notFoundWithEmail");
        }
        updateUserPassword(user, passwordDTO.getPassword(), true);
    }

    @Override
    public void updatePasswordByAccount(String account, UserType userType, String businessScope,
                                        String password, String captchaKey, String captcha) {
        userCaptchaService.validateCaptcha(captchaKey, captcha, account, userType, businessScope);
        User user = new User();
        if (account.contains(BaseConstants.Symbol.AT)) {
            user.setEmail(account);
        } else {
            user.setPhone(account);
        }
        user = userRepository.selectOne(user);
        Assert.notNull(user, MessageAccessor.getMessage("user.account.not-exists", new Object[]{account}).desc());

        userPasswordService.updateUserPassword(user.getId(), encryptClient.decrypt(password));
    }

    @Override
    public void resetUserPassword(Long userId, Long tenantId) {
        userPasswordService.resetUserPassword(userId, tenantId, false);
    }

    @Override
    public Page<UserEmployeeAssignDTO> pageUserEmployeeAssign(PageRequest pageRequest, Long organizationId, Long userId,
                                                              UserEmployeeAssignDTO params) {
        params.setOrganizationId(organizationId);
        params.setUserId(userId);
        return userRepository.pageUserEmployeeAssign(pageRequest, params);
    }

    @Override
    public List<Receiver> listReceiverByUserIds(Long tenantId, List<Long> userIds) {
        return userRepository.listReceiverByUserIds(userIds);
    }

    @Override
    public List<Receiver> listOpenReceiverByUserIds(Long tenantId, List<Long> userIds, String thirdPlatformType) {
        return userRepository.listOpenReceiverByUserIds(userIds, thirdPlatformType);
    }

    @Override
    public Page<UserSecCheckDTO> pageSecondaryCheck(UserSecCheckSearchDTO searchDTO, PageRequest pageRequest) {
        // 分页
        return PageHelper.doPage(pageRequest, () -> {
            // 查询数据
            return this.userRepository.listSecondaryCheck(searchDTO);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableSecCheck(Long organizationId, Set<Long> userIds, SecCheckType secCheckType) {
        // 启用
        this.toggleSecondaryCheck(organizationId, userIds, true, secCheckType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableSecCheck(Long organizationId, Set<Long> userIds, SecCheckType secCheckType) {
        // 禁用
        this.toggleSecondaryCheck(organizationId, userIds, false, secCheckType);
    }

    /**
     * 更新是否启用二次校验标识
     *
     * @param organizationId 租户ID
     * @param userIds        用户IDs
     * @param enableFlag     启用标识
     * @param secCheckType   二次校验发送验证码的类型
     */
    private void toggleSecondaryCheck(Long organizationId, Set<Long> userIds, boolean enableFlag, SecCheckType secCheckType) {
        if (CollectionUtils.isEmpty(userIds) || Objects.isNull(secCheckType)) {
            return;
        }

        // 分批处理数据
        ListUtils.partition(new ArrayList<>(userIds), 1000).stream()
                .map(subUserIds -> {
                    // 查询条件构建器
                    Condition.Builder conditionBuilder = Condition.builder(UserInfo.class)
                            .select(UserInfo.FIELD_USER_ID, UserInfo.FIELD_OBJECT_VERSION_NUMBER)
                            .where(Sqls.custom()
                                    .andEqualTo(UserInfo.FILED_TENANT_ID, organizationId)
                                    .andIn(UserInfo.FIELD_USER_ID, subUserIds)
                            );

                    switch (secCheckType) {
                        // 手机号
                        case PHONE:
                            conditionBuilder.andWhere(Sqls.custom()
                                    .andEqualTo(UserInfo.FIELD_SEC_CHECK_PHONE_FLAG, !enableFlag)
                            );
                            break;
                        // 邮箱
                        case EMAIL:
                            conditionBuilder.andWhere(Sqls.custom()
                                    .andEqualTo(UserInfo.FIELD_SEC_CHECK_EMAIL_FLAG, !enableFlag)
                            );
                            break;
                        default:
                            break;
                    }

                    // 查询用户信息
                    return this.userInfoRepository.selectByCondition(conditionBuilder.build());
                }).filter(CollectionUtils::isNotEmpty)
                // 设置新状态
                .peek(userInfos -> userInfos.forEach(user -> {
                    switch (secCheckType) {
                        // 手机号
                        case PHONE:
                            user.setSecCheckPhoneFlag(enableFlag);
                            break;
                        // 邮箱
                        case EMAIL:
                            user.setSecCheckEmailFlag(enableFlag);
                            break;
                        default:
                            break;
                    }
                }))
                // 更新数据
                .forEach(userInfos -> {
                    switch (secCheckType) {
                        // 手机号
                        case PHONE:
                            this.userInfoRepository.batchUpdateOptional(userInfos, UserInfo.FIELD_SEC_CHECK_PHONE_FLAG);
                            break;
                        // 邮箱
                        case EMAIL:
                            this.userInfoRepository.batchUpdateOptional(userInfos, UserInfo.FIELD_SEC_CHECK_EMAIL_FLAG);
                            break;
                        default:
                            break;
                    }
                });
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
            // 消息接收者
            Receiver receiver = new Receiver()
                    .setUserId(user.getId())
                    .setTargetUserTenantId(user.getOrganizationId())
                    .setEmail(user.getEmail())
                    .setPhone(user.getPhone());

            // 发送一个无密码的站内消息
            this.messageClient.async().sendMessage(
                    configGetter.getValue(user.getOrganizationId(), ProfileCode.MSG_CODE_MODIFY_PASSWORD),
                    Collections.singletonList(receiver),
                    args
            );

            // 添加密码参数
            args.put(User.FIELD_PASSWORD, password);
            // 根据手机和邮箱的绑定情况发送对应的消息
            List<String> typeCodeList = new ArrayList<>();
            // 查询用户信息
            UserInfo userInfo = this.userInfoRepository.selectByPrimaryKey(user.getId());
            AssertUtils.notNull(userInfo, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            if (BaseConstants.Flag.YES.equals(userInfo.getPhoneCheckFlag())) {
                typeCodeList.add(HmsgBootConstant.MessageType.SMS);
            }
            if (BaseConstants.Flag.YES.equals(userInfo.getEmailCheckFlag())) {
                typeCodeList.add(HmsgBootConstant.MessageType.EMAIL);
            }
            if (CollectionUtils.isNotEmpty(typeCodeList)) {
                // 发送带有密码的消息(手机/邮箱)
                this.messageClient.async().sendMessage(
                        configGetter.getValue(user.getOrganizationId(), ProfileCode.MSG_CODE_MODIFY_PWD_WITH_PWD),
                        Collections.singletonList(receiver),
                        args,
                        typeCodeList
                );
            }
        } catch (Exception e) {
            throw new CommonException("hiam.warn.sendMessageError");
        }
    }
}
