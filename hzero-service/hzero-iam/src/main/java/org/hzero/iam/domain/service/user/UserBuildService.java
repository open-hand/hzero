package org.hzero.iam.domain.service.user;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.common.UserSource;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.iam.config.IamProperties;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 用户构建抽象服务
 *
 * @author bojiangzhou 2019/04/22
 */
public abstract class UserBuildService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBuildService.class);

    private static final Random RANDOM = new SecureRandom();

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected EncryptClient encryptClient;
    @Autowired
    private IamProperties iamProperties;

    private Pattern usernamePattern = null;

    @Value("${hzero.user.login-name.length:8}")
    private int loginNameLength;

    /**
     * 创建用户
     *
     * @param user User
     * @return User
     */
    protected User createUser(User user) {
        LOGGER.info("begin create user, user={}", user);

        // 处理用户密码
        decryptUserPassword(user);

        // 初始化用户信息
        initUser(user);

        // 基础信息校验
        baseValidity(user);

        // 用户有效性检查
        checkValidity(user);

        // 检查登录名
        checkLoginName(user);

        // 生成登录账号
        generateLoginName(user);

        // 插入数据库之前的一些处理
        handleBeforePersist(user);

        // 持久化用户
        persistUser(user);

        // 创建 UserInfo
        handleUserInfo(user);

        // 缓存用户信息
        cacheUser(user);

        LOGGER.info("end create user, user={}", user);

        // 返回用户信息
        return user;
    }

    /**
     * 更新用户
     *
     * @param user User
     * @return User
     */
    protected User updateUser(User user) {
        LOGGER.info("begin update user, user={}", user);

        // 初始化用户信息
        initUser(user);

        // 基础信息校验
        baseValidity(user);

        // 用户有效性检查
        checkValidity(user);

        // 插入数据库之前的一些处理
        handleBeforePersist(user);

        // 新建用户
        persistUser(user);

        // 创建 UserInfo
        handleUserInfo(user);

        // 缓存用户信息
        cacheUser(user);

        LOGGER.info("end update user, user={}", user);

        // 返回用户信息
        return user;
    }

    /**
     * 注册用户
     *
     * @param user User
     * @return User
     */
    protected User registerUser(User user) {
        return createUser(user);
    }

    /**
     * 用户信息初始化
     *
     * @param user User
     */
    protected void initUser(User user) {
        if (user.getPasswordResetFlag() == null) {
            user.setPasswordResetFlag(BaseConstants.Flag.NO);
        }
        if (user.getPhoneCheckFlag() == null) {
            user.setPhoneCheckFlag(BaseConstants.Flag.NO);
        }
        if (user.getEmailCheckFlag() == null) {
            user.setEmailCheckFlag(BaseConstants.Flag.NO);
        }
        if (user.getUserSource() == null) {
            user.setUserSource(UserSource.ADMIN_CREATE.code());
        }
        if (user.getStartDateActive() == null) {
            user.setStartDateActive(LocalDate.now());
        }
        user.initUserType();
        user.setAdmin(false);
    }

    /**
     * 解密密码
     */
    protected void decryptUserPassword(User user) {
        // 解密密码
        if (StringUtils.isNotBlank(user.getPassword())) {
            String decryptPassword;
            try {
                decryptPassword = encryptClient.decrypt(user.getPassword());
            } catch (Exception e) {
                LOGGER.error("password decrypt failed, the password is: {}", user.getPassword());
                decryptPassword = user.getPassword();
            }
            user.setPassword(decryptPassword);
        }
    }

    protected void baseValidity(User user) {
        if (!iamProperties.isUserEmailNullable() && StringUtils.isBlank(user.getEmail())) {
            throw new CommonException("hiam.warn.user.emailNonnull");
        }
        if (!iamProperties.isUserPhoneNullable() && StringUtils.isBlank(user.getPhone())) {
            throw new CommonException("hiam.warn.user.phoneNonnull");
        }

        if (StringUtils.isNotBlank(user.getEmail()) && !Regexs.isEmail(user.getEmail())) {
            throw new CommonException("hiam.warn.user.emailFormatIncorrect");
        }

        if (StringUtils.isNotBlank(user.getPhone()) && !Regexs.isMobile(user.getInternationalTelCode(), user.getPhone())) {
            throw new CommonException("hiam.warn.user.phoneFormatIncorrect");
        }

        if (user.getEndDateActive() != null) {
            // 有效期起和止可以取同一天...
            if (user.getEndDateActive().isBefore(user.getStartDateActive())) {
                throw new CommonException("hiam.warn.user.endDateBiggerThenStartDate");
            }
        }
    }

    /**
     * 用户信息有效性校验
     *
     * @param user User
     */
    protected abstract void checkValidity(User user);

    /**
     * 检查用户登录名，默认策略：存在则校验用户名，不存在则自动生成
     *
     * @param user
     */
    protected void checkLoginName(User user) {
        String loginName = user.getLoginName();
        if (StringUtils.isBlank(loginName)) {
            return;
        }

        if (!this.iamProperties.isLoginNamePhoneFormat() && Regexs.isMobile(loginName)) {
            // 账号不能为手机号
            throw new CommonException("hiam.warn.user.login-name.not-phone-format");
        }

        if (!this.iamProperties.isLoginNameEmailFormat() && Regexs.isEmail(loginName)) {
            // 账号不能为邮箱号
            throw new CommonException("hiam.warn.user.login-name.not-email-format");
        }

        // 校验用户名格式是否正确
        if (usernamePattern != null && !usernamePattern.matcher(loginName).matches()) {
            throw new CommonException("hiam.warn.user.loginNameFormatIncorrect");
        }

        if (userRepository.existsByLoginName(user.getLoginName())) {
            throw new CommonException("hiam.warn.user.loginNameExists");
        }
    }

    /**
     * 生成用户登录账号，默认使用8位随机数组成
     *
     * @param user User
     */
    protected void generateLoginName(User user) {
        // 如果已有登录名 则不自动生成
        if (StringUtils.isNotBlank(user.getLoginName())) {
            return;
        }
        String[] arr = new String[loginNameLength];
        // 第一位数字不为0
        arr[0] = RandomStringUtils.random(1, 1, 9, false, true, Constants.NUMBERS, RANDOM);
        for (int i = 1; i < loginNameLength; i++) {
            arr[i] = RandomStringUtils.random(1, 0, 9, false, true, Constants.NUMBERS, RANDOM);
        }

        String loginName = StringUtils.join(arr);

        if (userRepository.existsByLoginName(loginName)) {
            // 存在则重新生成
            generateLoginName(user);
        } else {
            user.setLoginName(loginName);
        }
    }

    /**
     * 创建用户之前的一些处理
     *
     * @param user User
     */
    protected void handleBeforePersist(User user) {
        user.setTmpPassword(user.getPassword());
        // 加密密码
        user.encodePassword();
    }

    /**
     * 持久化用户信息到数据库
     *
     * @param user User
     */
    protected abstract void persistUser(User user);

    /**
     * 创建 UserInfo
     *
     * @param user User
     */
    protected abstract void handleUserInfo(User user);

    /**
     * 缓存用户信息
     *
     * @param user User
     */
    protected void cacheUser(User user) {
        userRepository.cacheUser(user.getId());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String usernameRegex = iamProperties.getUsernameRegex();
        LOGGER.info("Username regex is [{}]", usernameRegex);
        if (StringUtils.isNotBlank(usernameRegex)) {
            usernamePattern = Pattern.compile(usernameRegex);
        }
    }
}
