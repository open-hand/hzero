package org.hzero.iam.domain.service.user;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.core.user.UserType;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.infra.common.utils.UserUtils;

/**
 * 创建用户服务
 *
 * @author bojiangzhou 2019/04/22
 */
public class UserCreateService extends UserBuildService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

    @Autowired
    protected UserCheckService userCheckService;
    @Autowired
    protected TenantRepository tenantRepository;
    @Autowired
    protected UserPasswordService userPasswordService;

    /**
     * 创建用户
     * ProfileClient
     * @param user User
     * @return User
     */
    @Override
    public User createUser(User user) {
        return super.createUser(user);
    }

    @Override
    protected final User updateUser(User user) {
        return super.updateUser(user);
    }

    @Override
    protected final User registerUser(User user) {
        return super.registerUser(user);
    }

    @Override
    protected void checkValidity(User user) {
        if (StringUtils.isBlank(user.getRealName())) {
            throw new CommonException("hiam.warn.user.parameterNotBeNull", "realName");
        }

        // 检查用户角色
        checkMemberRole(user);

        // 检查用户租户
        checkUserTenant(user);

        if (user.getLdap() == null || !user.getLdap()) {
            // 检查用户账号相关
            checkUserAccount(user);
        }
    }

    protected void checkMemberRole(User user) {
        if (CollectionUtils.isEmpty(user.getMemberRoleList())) {
            throw new CommonException("hiam.warn.user.assignLeastOneRole");
        }
    }

    /**
     * 检查用户租户是否合法
     *
     * @param user User
     */
    protected void checkUserTenant(User user) {
        if (user.getOrganizationId() == null) {
            LOGGER.info("create user use self default current tenant id.");
            CustomUserDetails self = UserUtils.getUserDetails();
            user.setOrganizationId(self.getTenantId());
        }

        // 判断租户的用户数量是否超过限制
        Tenant tenant  = tenantRepository.selectByPrimaryKey(user.getOrganizationId());
        if (tenant == null) {
            throw new CommonException("hiam.warn.user.tenantNotFound");
        }
        if (tenant.getLimitUserQty() != null && tenant.getLimitUserQty() > 0) {
            // 限制有效用户数
            if (userRepository.countTenantUser(tenant.getTenantId()) + 1 > tenant.getLimitUserQty()) {
                throw new CommonException("hiam.error.active_users.reached");
            }
        }
        user.setTenantNum(tenant.getTenantNum());
        user.setTenantName(tenant.getTenantName());
    }

    /**
     * 检查用户账号是否合法
     *
     * @param user User
     */
    protected void checkUserAccount(User user) {
        // 密码为空则设置租户默认密码
        if (StringUtils.isBlank(user.getPassword())) {
            String defaultPassword = userPasswordService.getTenantDefaultPassword(user.getOrganizationId());
            if (StringUtils.isBlank(defaultPassword)) {
                throw new CommonException("hiam.warn.pwdPolicy.defaultPasswordNull");
            }
            user.setPassword(defaultPassword);
        } else {
            if (!user.ldapUser() && user.checkPassword()) {
                // 检查密码是否符合密码策略
                userCheckService.checkPasswordPolicy(user.getPassword(), user.getOrganizationId());
            }
        }

        // 检查手机号是否已注册
        if (StringUtils.isNotBlank(user.getPhone())) {
            userCheckService.checkPhoneRegistered(user.getPhone(), UserType.ofDefault(user.getUserType()));
        }
        // 检查邮箱是否已注册
        if (StringUtils.isNotBlank(user.getEmail())) {
            userCheckService.checkEmailRegistered(user.getEmail(), UserType.ofDefault(user.getUserType()));
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
        userInfo.setUserId(user.getId());
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setTenantId(user.getOrganizationId());
        userRepository.insertUserInfoSelective(userInfo);
    }

}
