package org.hzero.iam.domain.service.user.interceptor.interceptors;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.api.dto.TenantRoleDTO;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.iam.domain.repository.UserConfigRepository;

/**
 * 成员配置
 *
 * @author bojiangzhou 2020/05/28
 */
@Component
public class UserConfigInterceptor implements HandlerInterceptor<User> {

    @Autowired
    protected UserConfigRepository userConfigRepository;

    @Override
    public void interceptor(User user) {
        if (CollectionUtils.isEmpty(user.getDefaultRoles())) {
            return;
        }

        Map<Long, TenantRoleDTO> uncheckedMap = user.getDefaultRoles().stream()
                .filter(t -> Objects.equals(t.getDefaultFlag(), BaseConstants.Flag.NO))
                .collect(Collectors.toMap(TenantRoleDTO::getTenantId, Function.identity()));

        Map<Long, TenantRoleDTO> checkedMap = user.getDefaultRoles().stream()
                .filter(t -> Objects.equals(t.getDefaultFlag(), BaseConstants.Flag.YES))
                .collect(Collectors.toMap(TenantRoleDTO::getTenantId, Function.identity()));

        // 更新取消默认的
        uncheckedMap.forEach((tenantId, tenantRole) -> {
            if (!checkedMap.containsKey(tenantId)) {
                UserConfig userConfig = new UserConfig(user.getId(), tenantId);
                // 取消设置默认角色
                userConfig.setDefaultRoleId(null);
                userConfigRepository.createOrUpdate(userConfig);
            }
        });

        // 更新默认角色
        checkedMap.forEach((tenantId, tenantRole) -> {
            UserConfig config = new UserConfig(user.getId(), tenantId);
            config.setDefaultRoleId(tenantRole.getRoleId());
            userConfigRepository.createOrUpdate(config);
        });
    }
}
