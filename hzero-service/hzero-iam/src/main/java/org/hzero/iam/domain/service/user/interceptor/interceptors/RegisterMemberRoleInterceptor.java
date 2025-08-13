package org.hzero.iam.domain.service.user.interceptor.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserConfigRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamResourceLevel;

/**
 * 成员角色处理 —— 用户注册
 *
 * @author bojiangzhou 2020/05/28
 */
@Component
public class RegisterMemberRoleInterceptor implements HandlerInterceptor<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterMemberRoleInterceptor.class);

    @Autowired
    protected MemberRoleRepository memberRoleRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserConfigRepository userConfigRepository;

    @Override
    public void interceptor(User user) {
        List<MemberRole> memberRoleList = new ArrayList<>();

        // 默认分配游客角色
        Role siteGuestRole = roleRepository.selectRoleSimpleByCode(Constants.SITE_GUEST_ROLE_CODE);
        if (siteGuestRole != null) {
            // 分配平台层游客身份
            memberRoleList.add(
                    new MemberRole(
                            siteGuestRole.getId(),
                            user.getId(),
                            Constants.MemberType.USER,
                            Constants.SITE_TENANT_ID,
                            HiamResourceLevel.SITE.value()
                    )
            );
        }

        Role organizationGuestRole = roleRepository.selectRoleSimpleByCode(Constants.ORGANIZATION_GUEST_ROLE_CODE);
        if (organizationGuestRole != null) {
            // 分配租户层游客身份
            memberRoleList.add(
                    new MemberRole(
                            organizationGuestRole.getId(),
                            user.getId(),
                            Constants.MemberType.USER,
                            Constants.SITE_TENANT_ID,
                            HiamResourceLevel.ORGANIZATION.value()
                    )
            );
        }

        LOGGER.debug("Generate default guest role for register user, memberRoles={}", memberRoleList);

        if (CollectionUtils.isNotEmpty(memberRoleList)) {
            // 批量插入成员角色关系
            memberRoleRepository.batchInsertSelective(memberRoleList);

            if (siteGuestRole != null) {
                UserConfig config = new UserConfig(user.getId(), user.getOrganizationId());
                config.setDefaultRoleId(siteGuestRole.getId());
                userConfigRepository.createOrUpdate(config);
                user.setMemberRoleList(memberRoleList);
            }
        }
    }
}
