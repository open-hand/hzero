package org.hzero.iam.domain.service.role;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RolePermissionRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.Constants.YesNoFlag;
import org.hzero.iam.infra.constant.Operation;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.mybatis.helper.SecurityTokenHelper;

/**
 * 角色权限集 回收 核心业务类
 *
 * @author bojiangzhou 2019/01/24
 */
public class RolePermissionSetRecycleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionSetRecycleService.class);

    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected RolePermissionRepository rolePermissionRepository;
    @Autowired
    protected MemberRoleRepository memberRoleRepository;

    /**
     * 回收角色权限集
     *
     * @param roleId 角色ID
     * @param permissionSetIds 权限集ID集合
     */
    public void recycleRolePermissionSets(Long roleId, Set<Long> permissionSetIds, String type) {
        if (CollectionUtils.isEmpty(permissionSetIds)) {
            return;
        }

        checkRolePsRecyclable(roleId);

        SecurityTokenHelper.close();

        type = RolePermissionType.value(type).name();

        Role target = roleRepository.selectByPrimaryKey(roleId);
        if (target == null) {
            throw new CommonException("hiam.error.role.notFound");
        }

        List<RolePermission> result = Collections.synchronizedList(new ArrayList<>(permissionSetIds.size() * 100));

        // 回收目标角色权限集
        List<RolePermission> recyclablePermissionSets = selectRecyclablePermissionSets(target, permissionSetIds, type);
        List<RolePermission> recycleList = handleRecycleCreatedRolePermissionSets(target, recyclablePermissionSets);

        result.addAll(recycleList);

        // 回收目标角色的子孙角色的权限集
        List<Role> createdSubRoles = roleRepository.selectCreatedSubRoleTreeWithPermissionSets(roleId, permissionSetIds, type);
        createdSubRoles.parallelStream().forEach(createdSubRole -> {
            List<RolePermission> subList = handleRecycleCreatedRolePermissionSets(createdSubRole, createdSubRole.getPermissionSets());
            result.addAll(subList);
        });

        // 回收继承的角色及子孙角色的权限集
        List<Role> inheritSubRoles = roleRepository.selectInheritSubRoleTreeWithPermissionSets(roleId, permissionSetIds, type);
        inheritSubRoles.parallelStream().forEach(inheritSubRole -> {
            List<RolePermission> subList = handleRecycleInheritRolePermissionSets(inheritSubRole, inheritSubRole.getPermissionSets());
            result.addAll(subList);
        });

        Map<Operation, List<RolePermission>> mapResult = rolePermissionRepository.batchSaveRolePermission(result.stream());

        LOGGER.info("Finish recycle rolePermission, updateSize: {}", mapResult.get(Operation.UPDATE).size());

        mapResult.clear();
        SecurityTokenHelper.clear();
    }

    private void checkRolePsRecyclable(Long roleId) {
        CustomUserDetails self = UserUtils.getUserDetails();
        boolean topAdminRole = roleRepository.isTopAdminRole(self.getUserId(), roleId);
        if (topAdminRole ) {
            throw new CommonException("hiam.warn.role.denyOperationSelfTopRole");
        }
    }

    /**
     * 查询可回收的权限集
     *
     * @param role 角色
     */
    protected List<RolePermission> selectRecyclablePermissionSets(Role role, Set<Long> recyclePermissionSetIds, String type) {
        RolePermission param = new RolePermission();
        param
            .setRoleId(role.getId())
            .setPermissionSetIds(recyclePermissionSetIds)
            .setType(type)
        ;

        return rolePermissionRepository.selectRolePermissionSets(param);
    }

    /**
     * 回收角色及子孙角色的权限集
     */
    protected List<RolePermission> handleRecycleCreatedRolePermissionSets(Role role, List<RolePermission> recyclablePermissionSets) {
        if (CollectionUtils.isEmpty(recyclablePermissionSets)) {
            return Collections.emptyList();
        }

        List<RolePermission> result = new ArrayList<>(1024);

        recyclablePermissionSets.forEach(ps -> {
            // 主动回收，创建标识置为X，标识回收
            ps.setCreateFlag(YesNoFlag.DELETE);
            if (YesNoFlag.YES.equals(ps.getInheritFlag())) {
                ps.setInheritFlag(YesNoFlag.DELETE);
            }
            ps.setOperation(Operation.UPDATE);
        });

        result.addAll(recyclablePermissionSets);

        if (CollectionUtils.isNotEmpty(role.getCreatedSubRoles())) {
            role.getCreatedSubRoles().forEach(createdSubRole -> {
                List<RolePermission> subList = handleRecycleCreatedRolePermissionSets(createdSubRole, createdSubRole.getPermissionSets());
                result.addAll(subList);
            });
        }

        return result;
    }

    /**
     * 回收继承的角色的权限集
     */
    protected List<RolePermission> handleRecycleInheritRolePermissionSets(Role role, List<RolePermission> recyclablePermissionSets) {
        if (CollectionUtils.isEmpty(recyclablePermissionSets)) {
            return Collections.emptyList();
        }

        List<RolePermission> result = new ArrayList<>(1024);

        recyclablePermissionSets.forEach(ps -> {
            // 继承标识置为X，标识回收
            ps.setInheritFlag(Constants.YesNoFlag.DELETE);
            ps.setOperation(Operation.UPDATE);
        });

        result.addAll(recyclablePermissionSets);

        if (CollectionUtils.isNotEmpty(role.getInheritSubRoles())) {
            role.getInheritSubRoles().forEach(inheritSubRole -> {
                List<RolePermission> subList = handleRecycleInheritRolePermissionSets(inheritSubRole, inheritSubRole.getPermissionSets());
                result.addAll(subList);
            });
        }

        return result;
    }

}
