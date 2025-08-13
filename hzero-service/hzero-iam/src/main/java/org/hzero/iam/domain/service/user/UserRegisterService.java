package org.hzero.iam.domain.service.user;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;

import org.hzero.common.UserSource;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.user.UserType;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.constant.Constants;

/**
 * 创建注册服务
 *
 * @author bojiangzhou 2019/04/22
 */
public class UserRegisterService extends UserBuildService {

    @Autowired
    protected MemberRoleService memberRoleService;
    @Autowired
    protected MemberRoleRepository memberRoleRepository;
    @Autowired
    protected UserCheckService userCheckService;
    @Autowired
    protected RoleRepository roleRepository;

    @Override
    protected final User createUser(User user) {
        return super.createUser(user);
    }

    @Override
    protected final User updateUser(User user) {
        return super.updateUser(user);
    }

    /**
     * 注册用户
     *
     * @param user User
     * @return User
     */
    @Override
    public User registerUser(User user) {
        return super.registerUser(user);
    }

    @Override
    protected void initUser(User user) {
        user.setPasswordResetFlag(BaseConstants.Flag.YES);
        user.setUserSource(UserSource.SELF_REGISTER.code());
        if (user.getStartDateActive() == null) {
            user.setStartDateActive(LocalDate.now());
        }
        // 默认设置为 平台
        user.setOrganizationId(Constants.SITE_TENANT_ID);
        user.initUserType();
        user.setAdmin(false);
    }

    @Override
    protected void checkValidity(User user) {
        if (StringUtils.isBlank(user.getRealName())) {
            throw new CommonException("hiam.warn.user.parameterNotBeNull", "realName");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new CommonException("hiam.warn.user.parameterNotBeNull", "password");
        }

        // 检查用户账号相关
        checkUserAccount(user);
    }

    /**
     * 检查用户账号是否合法
     *
     * @param user User
     */
    protected void checkUserAccount(User user) {
        if (StringUtils.isNotBlank(user.getCompanyName())) {
            // 检查公司名称是否已注册
            userCheckService.checkCompanyNameRegistered(user.getCompanyName());
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            // 检查手机号是否已注册
            userCheckService.checkPhoneRegistered(user.getPhone(), UserType.ofDefault(user.getUserType()));
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            // 检查邮箱是否已注册
            userCheckService.checkEmailRegistered(user.getEmail(), UserType.ofDefault(user.getUserType()));
        }

        // 检查密码是否符合密码策略
        userCheckService.checkPasswordPolicy(user.getPassword(), user.getOrganizationId());

        if (user.getTextId() != null) {
            // 检查注册协议是否有效
            userCheckService.checkRegistrationProtocol(user.getTextId());
        }
    }

    /**
     * 创建用户，插入数据库
     *
     * @param user User
     */
    @Override
    protected void persistUser(User user) {
        // 插入数据
        userRepository.insertSelective(user);
    }

    /**
     * 创建 UserInfo
     *
     * @param user User
     */
    @Override
    protected void handleUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setUserId(user.getId());
        userInfo.setTenantId(user.getOrganizationId());
        userRepository.insertUserInfoSelective(userInfo);
    }

}
