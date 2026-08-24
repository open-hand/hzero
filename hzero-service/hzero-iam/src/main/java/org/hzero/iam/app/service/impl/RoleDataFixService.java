package org.hzero.iam.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.common.HZeroCacheKey;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.iam.api.dto.RolePermissionAssignDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.service.role.RolePermissionSetAssignService;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.common.utils.HiamRoleUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 角色数据修复
 *
 * @author bojiangzhou 2020/07/09
 */
@Component
public class RoleDataFixService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleDataFixService.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RolePermissionSetAssignService rolePermissionSetAssignService;
    @Autowired
    private RedisHelper redisHelper;

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("InheritRolePs").build());

    /**
     * 初始化角色继承树上角色-权限集关系
     */
    public void initRoleOfInheritSubRoleTreePss(RolePermissionAssignDTO dto) {
        LOGGER.info("Async init permission of inherit role tree...");
        EXECUTOR.submit(() -> {
            initInheritRoleThreePs(dto);
        });
    }

    public void initInheritRoleThreePs(RolePermissionAssignDTO dto) {
        try {
            long start = System.currentTimeMillis();
            List<Role> roles;
            if (CollectionUtils.isNotEmpty(dto.getRoleLevelPaths())) {
                // 根据编码查询
                roles = roleRepository.selectByCondition(Condition.builder(Role.class)
                        .andWhere(Sqls.custom().andIn(Role.FIELD_LEVEL_PATH, dto.getRoleLevelPaths())).build());
            } else {
                // 查询预置角色 不包含超级管理员
                roles = roleRepository.selectBuiltInRoles(false);
            }

            if (CollectionUtils.isNotEmpty(roles)) {
                rolePermissionSetAssignService.assignInheritedRolePermissionSets(roles);
            }
            long end = System.currentTimeMillis();
            LOGGER.info("Init inherit role tree permission success, cost time: {}", end - start);
        } catch (Exception e) {
            LOGGER.error("Async init inherit role permission error.", e);
        }
    }

    /**
     * 初始化角色创建者租户字段数据
     */
    public synchronized Map<String, Object> fixRoleData() {
        Map<String, Object> result = new HashMap<>(8);

        List<Role> roles = roleRepository.selectSimpleRolesWithTenant(new RoleVO());

        int updateUserCount = 0;
        int userNotFoundCount = 0;
        int updateRoleCodeCount = 0;
        int updateRoleCount = roles.size();

        // 修复角色编码和创建者租户
        for (Role role : roles) {
            // 处理 createByTenantId
            User user = redisHelper.fromJson(redisHelper.hshGet(HZeroCacheKey.USER, role.getCreatedBy().toString()), User.class);

            if (user == null) {
                LOGGER.warn("role createdBy user not found. roleId: {}, createdBy: {}", role.getId(), role.getCreatedBy());
                userNotFoundCount++;
            } else {
                role.setCreatedByTenantId(user.getOrganizationId());
                role.setCreatedByTenantNum(user.getTenantNum());
                updateUserCount++;
            }

            // 角色编码调整
            String newCode = "";
            if (Boolean.TRUE.equals(role.getBuiltIn())) {
                // 内置角色保持原编码
                newCode = role.getCode();
            } else {
                // 非内置角色取最后一段
                String[] arr = role.getCode().split(BaseConstants.Symbol.SLASH);
                newCode = arr[arr.length - 1];
                updateRoleCodeCount++;
            }
            role.setCode(newCode);
        }

        // 修复角色路径
        initRoleLevelPath(roles);

        roles.parallelStream().forEach(role -> {
            roleRepository.updateOptional(role, Role.FIELD_CODE, Role.FIELD_CREATED_BY_TENANT_ID,
                    Role.FIELD_LEVEL_PATH, Role.FIELD_INHERIT_LEVEL_PATH);
        });

        result.put("updateRole[createdByTenantId]Count", updateUserCount);
        result.put("role[createdBy]NotFoundCount", userNotFoundCount);
        result.put("updateRole[code]Count", updateRoleCodeCount);
        result.put("updateRoleCount", updateRoleCount);

        roles.clear();

        LOGGER.info("Finish fix roleData, {}", result);

        return result;
    }

    private void initRoleLevelPath(List<Role> roles) {
        List<Role> createdRoleTree = HiamRoleUtils.formatRoleListToTree(roles, false);
        initCreatedRoleLevelPath(null, createdRoleTree);

        List<Role> inheritedRoleTree = HiamRoleUtils.formatRoleListToTree(roles, true);
        initInheritedRoleLevelPath(null, inheritedRoleTree);
    }

    private void initCreatedRoleLevelPath(Role parentRole, List<Role> roles) {
        for (Role role : roles) {
            role.buildCreatedRoleLevelPath(parentRole);
            if (CollectionUtils.isNotEmpty(role.getCreatedSubRoles())) {
                initCreatedRoleLevelPath(role, role.getCreatedSubRoles());
            }
        }
    }

    private void initInheritedRoleLevelPath(Role inheritRole, List<Role> roles) {
        for (Role role : roles) {
            role.buildInheritRoleLevelPath(inheritRole);
            if (CollectionUtils.isNotEmpty(role.getInheritSubRoles())) {
                initInheritedRoleLevelPath(role, role.getInheritSubRoles());
            }
        }
    }


}
