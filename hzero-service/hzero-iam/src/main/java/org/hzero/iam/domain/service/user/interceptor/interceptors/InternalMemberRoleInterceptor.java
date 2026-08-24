package org.hzero.iam.domain.service.user.interceptor.interceptors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserConfigRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.role.MemberRoleAssignService;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.constant.HiamResourceLevel;

/**
 * 成员角色处理 —— 用户注册
 *
 * @author bojiangzhou 2020/05/28
 */
@Component
public class InternalMemberRoleInterceptor implements HandlerInterceptor<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalMemberRoleInterceptor.class);

    @Autowired
    protected MemberRoleRepository memberRoleRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserConfigRepository userConfigRepository;
    @Autowired
    protected MemberRoleAssignService memberRoleAssignService;

    @Override
    public void interceptor(User user) {
        if (CollectionUtils.isEmpty(user.getMemberRoleList())) {
            return;
        }
        user.getMemberRoleList().forEach(item -> {
            item.setMemberId(user.getId());
            item.setMemberType(HiamMemberType.USER.toString().toLowerCase());
            item.setUser(user.simpleCopyUser(user));

            // 如果传的角色路径进来，则转换成ID
            Role role = null;
            if (StringUtils.isNotBlank(item.getLevelPath())) {
                role = roleRepository.selectRoleSimpleByLevelPath(item.getLevelPath());
                if (role == null) {
                    throw new CommonException("hiam.warn.roleNotFoundForLevelPath", item.getLevelPath());
                }
                item.setRoleId(role.getId());
            }
            // 考虑历史接口，做个补充
            else if (StringUtils.isNotBlank(item.getRoleCode())) {
                role = roleRepository.selectRoleSimpleByCode(item.getRoleCode());
                if (role == null) {
                    throw new CommonException("hiam.warn.roleNotFoundForCode", item.getRoleCode());
                }
                item.setRoleId(role.getId());
            } else {
                role = roleRepository.selectRoleSimpleById(item.getRoleId());
                if (role == null) {
                    throw new CommonException("hiam.warn.roleNotFoundForId", item.getRoleId());
                }
            }

            item.setAssignLevel(HiamResourceLevel.ORGANIZATION.value());
            item.setAssignLevelValue(role.getTenantId());
        });
        memberRoleAssignService.assignMemberRole(user.getMemberRoleList(), false);
    }
}
