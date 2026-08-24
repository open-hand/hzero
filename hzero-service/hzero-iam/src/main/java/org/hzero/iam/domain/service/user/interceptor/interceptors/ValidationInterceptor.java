package org.hzero.iam.domain.service.user.interceptor.interceptors;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.api.dto.TenantRoleDTO;
import org.hzero.iam.domain.entity.User;

/**
 * 通用校验
 *
 * @author bojiangzhou 2020/05/29
 */
@Component
public class ValidationInterceptor implements HandlerInterceptor<User> {

    @Override
    public void interceptor(User user) {
        if (CollectionUtils.isEmpty(user.getMemberRoleList())) {
            return;
        }

        Map<Long, List<TenantRoleDTO>> map = Optional.ofNullable(user.getDefaultRoles())
                .orElse(Collections.emptyList())
                .stream()
                .filter(t -> Objects.equals(t.getDefaultFlag(), BaseConstants.Flag.YES))
                .collect(Collectors.groupingBy(TenantRoleDTO::getTenantId));

        // 同一租户只能有一个默认角色
        map.forEach((k, v) -> {
            if (v.size() > 1) {
                throw new IllegalArgumentException("Only one default role is allowed under the same tenant.");
            }
        });
    }
}
