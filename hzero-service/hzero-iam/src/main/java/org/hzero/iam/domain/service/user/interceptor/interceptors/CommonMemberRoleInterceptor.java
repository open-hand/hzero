package org.hzero.iam.domain.service.user.interceptor.interceptors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.role.MemberRoleAssignService;
import org.hzero.iam.infra.constant.HiamMemberType;

/**
 * 成员角色处理 —— 通用处理
 *
 * @author bojiangzhou 2020/05/28
 */
@Component
public class CommonMemberRoleInterceptor implements HandlerInterceptor<User> {

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
        });
        memberRoleAssignService.assignMemberRole(user.getMemberRoleList(), true);
    }
}
