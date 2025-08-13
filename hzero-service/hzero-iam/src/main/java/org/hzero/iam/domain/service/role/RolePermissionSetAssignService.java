package org.hzero.iam.domain.service.role;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.base.BaseConstants;
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
 * 角色权限集 分配 核心业务类
 *
 * @author bojiangzhou 2019/01/24
 */
public class RolePermissionSetAssignService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionSetAssignService.class);

    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected RolePermissionRepository rolePermissionRepository;
    @Autowired
    protected MemberRoleRepository memberRoleRepository;

    /**
     * 分配角色权限集
     *
     * @param roleId           角色ID
     * @param permissionSetIds 权限集ID集合
     */
    public void assignRolePermissionSets(Long roleId, Set<Long> permissionSetIds, String type) {
        if (CollectionUtils.isEmpty(permissionSetIds)) {
            return;
        }

        checkRolePsAssignable(roleId);

        LOGGER.info("Start assign rolePermission, roleId: {}, permissionSetIds: {}, type: {}", roleId, permissionSetIds, type);

        type = RolePermissionType.value(type).name();

        Role target = roleRepository.selectByPrimaryKey(roleId);
        if (target == null) {
            throw new CommonException("hiam.error.role.notFound");
        }

        SecurityTokenHelper.close();

        // 查询可分配的权限集，即目标角色的父角色可分配的权限集 :: 明确此处可分配的权限集都是可被分配的，即 createFlag=Y
        List<RolePermission> assignablePermissionSets = selectAssignablePermissionSets(target, false, permissionSetIds, type);

        // 查询角色已分配过的权限集
        List<RolePermission> assignedPermissionSets = selectAssignedPermissionSets(target, assignablePermissionSets, type);

        // 处理目标角色需重新分配的权限集::主动分配，设置 createFlag=Y (主动分配的只能主动取消回收)
        List<RolePermission> targetRolePermissionSets = handleAssignRolePermissionSets(target, assignablePermissionSets, assignedPermissionSets, type);

        // 处理继承角色::设置 inheritFlag=Y
        List<RolePermission> inheritRolePermissionSets = handleInheritedRolePermissionSets(target, targetRolePermissionSets, type);

        // 处理取消回收部分
        List<RolePermission> cancelRolePermissionSets = handleCancelingRolePermissionSets(target, assignedPermissionSets, type);

        Map<Operation, List<RolePermission>> mapResult = rolePermissionRepository
                .batchSaveRolePermission(Stream.of(targetRolePermissionSets, inheritRolePermissionSets, cancelRolePermissionSets).flatMap(List::stream));

        LOGGER.info("Finish assign rolePermission, topRoleId is: {}, insertSize: {}, updateSize: {}, deleteSize: {}",
                roleId, mapResult.get(Operation.INSERT).size(), mapResult.get(Operation.UPDATE).size(), mapResult.get(Operation.DELETE).size());

        SecurityTokenHelper.clear();
        mapResult.clear();
    }

    /**
     * 将权限集挂到指定执行租户的租户管理员上
     *
     * @param tenantId           租户ID
     * @param rolePermissionType 权限类型
     * @param permissionSetId    权限集ID
     */
    public void assignPermissionSetsToTenantManager(Long tenantId, Long permissionSetId, RolePermissionType rolePermissionType) {
        SecurityTokenHelper.close();

        // 根据标签查询租户管理员
        List<Role> tenantAdminList = roleRepository.selectRoleByLabel(tenantId, Sets.newHashSet(Role.LABEL_TENANT_ADMIN), null);

        if (CollectionUtils.isEmpty(tenantAdminList)) {
            LOGGER.warn("tenant admin role not found by label, tenantId = {}", tenantId);
            return;
        }

        for (Role role : tenantAdminList) {
            //直接挂上去
            RolePermission rolePermission = new RolePermission(role.getId(), permissionSetId, Constants.YesNoFlag.NO,
                    Constants.YesNoFlag.YES, rolePermissionType.name(), tenantId);
            rolePermissionRepository.insertSelective(rolePermission);
        }

        SecurityTokenHelper.clear();
    }


    /**
     * 为角色的继续树角色分配权限
     *
     * @param roles 角色
     */
    public void assignInheritedRolePermissionSets(List<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        SecurityTokenHelper.close();

        List<RolePermission> result = new ArrayList<>(1 << 16);
        for (Role role : roles) {
            List<RolePermission> assignablePermissionSets = selectAssignablePermissionSets(role, true, Collections.emptySet(), RolePermissionType.PS.name());

            // 处理继承角色::设置 inheritFlag=Y
            List<RolePermission> inheritList = handleInheritedRolePermissionSets(role, assignablePermissionSets, RolePermissionType.PS.name());
            result.addAll(inheritList);
        }
        Map<Operation, List<RolePermission>> mapResult = rolePermissionRepository.batchSaveRolePermission(result.stream());

        LOGGER.info("Finish assign inherited rolePermission, insertSize: {}, updateSize: {}, deleteSize: {}",
                mapResult.get(Operation.INSERT).size(), mapResult.get(Operation.UPDATE).size(), mapResult.get(Operation.DELETE).size());

        mapResult.clear();
        SecurityTokenHelper.clear();
    }

    //
    // protected method
    // ------------------------------------------------------------------------------

    /**
     * 顶级角色不能分配权限
     */
    protected void checkRolePsAssignable(Long roleId) {
        CustomUserDetails self = UserUtils.getUserDetails();
        boolean topAdminRole = roleRepository.isTopAdminRole(self.getUserId(), roleId);
        if (topAdminRole ) {
            throw new CommonException("hiam.warn.role.denyOperationSelfTopRole");
        }
    }

    /**
     * 查询角色可分配的权限集
     *
     * @param role             角色
     * @param self             是否查询自己的已分配权限
     * @param permissionSetIds 权限集
     * @return List<RolePermission>
     */
    protected List<RolePermission> selectAssignablePermissionSets(Role role, boolean self, Set<Long> permissionSetIds, String type) {
        RolePermission param = new RolePermission();
        Long roleId;
        if (self) {
            roleId = role.getId();
        } else {
            roleId = role.getParentRoleId();

            Role parentRole = roleRepository.selectRoleSimpleById(roleId);
            if (parentRole == null) {
                LOGGER.warn("Role's parentRole not found. roleId is {}", roleId);
                throw new CommonException("hiam.warn.role.parentRoleNotFound");
            }

            // 超级管理员直接返回
            if (BooleanUtils.isTrue(parentRole.getBuiltIn())
                    && BooleanUtils.isTrue(parentRole.getEnabled())
                    && StringUtils.equalsAny(parentRole.getCode(), Constants.SITE_SUPER_ROLE_CODE, Constants.TENANT_SUPER_ROLE_CODE)) {
                return permissionSetIds.stream().map(id -> {
                    return new RolePermission(roleId, id, YesNoFlag.NO, YesNoFlag.YES, type, BaseConstants.DEFAULT_TENANT_ID);
                }).collect(Collectors.toList());
            }
        }

        param
                .setRoleId(roleId)
                .setLevel(role.getLevel())
                .setBothCreateAndInheritFlag(true) // 查询 createFlag=Y OR inheritFlag=Y
                .setPermissionSetIds(permissionSetIds)
                .setType(type)
        ;

        return rolePermissionRepository.selectRolePermissionSets(param);
    }

    /**
     * 查询角色的权限集
     *
     * @param role                     角色
     * @param assignablePermissionSets 可分配权限集
     */
    protected List<RolePermission> selectAssignedPermissionSets(Role role, List<RolePermission> assignablePermissionSets, String type) {
        // ids
        Set<Long> psIds = getPermissionSetIds(assignablePermissionSets);

        RolePermission param = new RolePermission();
        param
                .setRoleId(role.getId())
                .setLevel(role.getLevel())
                .setPermissionSetIds(psIds)
                .setType(type)
        ;

        return rolePermissionRepository.selectRolePermissionSets(param);
    }

    /**
     * 为目标角色分配权限集，createFlag=Y
     *
     * @param role                     角色
     * @param assignablePermissionSets 可分配的权限集
     * @param assignedPermissionSets   目标角色已分配的权限集，范围已限制在 assignablePermissionSets 内
     */
    protected List<RolePermission> handleAssignRolePermissionSets(Role role,
                                                                  List<RolePermission> assignablePermissionSets,
                                                                  List<RolePermission> assignedPermissionSets,
                                                                  String type) {
        // ids
        Set<Long> psIds = getPermissionSetIds(assignedPermissionSets);

        List<RolePermission> result = new ArrayList<>(assignablePermissionSets.size());

        // 重新分配部分 : 原有创建标识为N, 代表需要分配权限集
        assignedPermissionSets.stream()
                .filter(ps -> YesNoFlag.NO.equals(ps.getCreateFlag()))
                .forEach(ps -> {
                    ps.setCreateFlag(YesNoFlag.YES);
                    ps.setOperation(Operation.UPDATE);
                });

        result.addAll(assignedPermissionSets); // 注意需要添加角色所有已分配的权限集

        Long roleId = role.getId();

        // 新增部分
        List<RolePermission> newlyList = assignablePermissionSets.stream()
                .filter(ps -> !psIds.contains(ps.getPermissionSetId()))
                .map(ps -> {
                    // createFlag=Y,inheritFlag=N
                    RolePermission rp = new RolePermission(roleId, ps.getPermissionSetId(), YesNoFlag.NO, YesNoFlag.YES, type, role.getTenantId());
                    rp.setOperation(Operation.INSERT);
                    return rp;
                })
                .collect(Collectors.toList());

        result.addAll(newlyList);

        return result;
    }

    /**
     * 递归分配继承子树上的相关权限
     *
     * @param role                     目标角色
     * @param assignablePermissionSets 可分配权限集
     */
    protected List<RolePermission> handleInheritedRolePermissionSets(Role role, List<RolePermission> assignablePermissionSets, String type) {
        if (CollectionUtils.isEmpty(assignablePermissionSets)) {
            return Collections.emptyList();
        }

        // 查询继承的角色，包含了角色已分配的权限集
        List<Role> inheritedRoles = roleRepository.selectInheritSubRoleTreeWithPermissionSets(role.getId(), getPermissionSetIds(assignablePermissionSets), type);

        if (CollectionUtils.isEmpty(inheritedRoles)) {
            return Collections.emptyList();
        }

        List<RolePermission> result = Collections.synchronizedList(new ArrayList<>(inheritedRoles.size() * assignablePermissionSets.size()));
        inheritedRoles.parallelStream().forEach(inheritedRole -> {
            // 处理继承的角色权限集分配
            List<RolePermission> resultList = handleAssignInheritedRolePermissionSets(inheritedRole, assignablePermissionSets, type);
            result.addAll(resultList);
        });

        return result;
    }

    /**
     * 取消权限回收
     *
     * @param role                     目标角色
     * @param assignablePermissionSets 可取消回收的权限集
     */
    protected List<RolePermission> handleCancelingRolePermissionSets(Role role, List<RolePermission> assignablePermissionSets, String type) {
        if (CollectionUtils.isEmpty(assignablePermissionSets)) {
            return Collections.emptyList();
        }

        // 取消回收当前角色权限集
        List<RolePermission> cancelList = handleCancelingPermissionSets(assignablePermissionSets);

        // 查询子角色树(创建树)，包含已分配的权限集
        List<Role> createdRoles = roleRepository.selectCreatedSubRoleTreeWithPermissionSets(role.getId(), getPermissionSetIds(cancelList), type);

        List<RolePermission> result = Collections.synchronizedList(new ArrayList<>(createdRoles.size() * assignablePermissionSets.size()));

        result.addAll(cancelList);

        // 取消回收创建树上子角色权限集
        createdRoles.parallelStream().forEach(createdRole -> {
            result.addAll(handleCancelingCreatedSubRolePermissionSets(createdRole, cancelList));
        });

        // 取消回收继承树上子角色权限集
        handleInheritedRolePermissionSets(role, cancelList, type);

        return result;
    }

    //
    // private method
    // ------------------------------------------------------------------------------

    private List<RolePermission> handleAssignInheritedRolePermissionSets(Role role, List<RolePermission> assignablePermissionSets, String type) {
        // 角色已分配过的权限集
        List<RolePermission> assignedPermissionSets = CollectionUtils.isEmpty(role.getPermissionSets()) ?
                Collections.emptyList() : role.getPermissionSets();

        // ids
        Set<Long> psIds = getPermissionSetIds(assignedPermissionSets);

        // ids
        Set<Long> assignablePsIds = getPermissionSetIds(assignablePermissionSets);

        List<RolePermission> result = new ArrayList<>(assignablePermissionSets.size());

        // 删除部分：继承的角色权限已经没了，子角色需删除这部分权限
        Iterator<RolePermission> iterator = assignedPermissionSets.iterator();
        while (iterator.hasNext()) {
            RolePermission rp = iterator.next();
            if (YesNoFlag.NO.equals(rp.getCreateFlag()) &&
                    YesNoFlag.YES.equals(rp.getInheritFlag()) &&
                    !assignablePsIds.contains(rp.getPermissionSetId())) {
                rp.setOperation(Operation.DELETE);
                result.add(rp);
                iterator.remove();
            }
        }

        // 重新分配部分
        List<RolePermission> assignList = assignedPermissionSets.stream()
                // 如果创建树上权限已被回收则无法被分配
                .filter(rp -> !YesNoFlag.DELETE.equals(rp.getCreateFlag()) && !YesNoFlag.YES.equals(rp.getInheritFlag()))
                .peek(rp -> {
                    rp.setInheritFlag(YesNoFlag.YES);
                    rp.setOperation(Operation.UPDATE);
                })
                .collect(Collectors.toList());

        result.addAll(assignList);

        // 新增部分
        List<RolePermission> newlyList = assignablePermissionSets.stream()
                .filter(ps -> !psIds.contains(ps.getPermissionSetId()))
                .map(ps -> {
                    // createFlag=N,inheritFlag=Y
                    RolePermission rp = new RolePermission(role.getId(), ps.getPermissionSetId(), YesNoFlag.YES, YesNoFlag.NO, type, role.getTenantId());
                    rp.setOperation(Operation.INSERT);
                    return rp;
                })
                .collect(Collectors.toList());
        newlyList.addAll(assignedPermissionSets); // 添加到角色已分配的权限集
        result.addAll(newlyList);

        // 处理继承的子角色
        if (CollectionUtils.isNotEmpty(role.getInheritSubRoles())) {
            for (Role inheritSubRole : role.getInheritSubRoles()) {
                result.addAll(handleAssignInheritedRolePermissionSets(inheritSubRole, newlyList, type));
            }
        }

        return result;
    }

    /**
     * 取消回收创建树子角色上的权限集
     */
    private List<RolePermission> handleCancelingCreatedSubRolePermissionSets(Role role, List<RolePermission> assignablePermissionSets) {
        if (CollectionUtils.isEmpty(assignablePermissionSets)) {
            return Collections.emptyList();
        }

        List<RolePermission> result = new ArrayList<>(assignablePermissionSets.size());

        // 可进行取消回收的权限
        List<RolePermission> assignedPermissionSets = role.getPermissionSets();

        Set<Long> permissionSetIds = getPermissionSetIds(assignablePermissionSets);

        // 限制范围
        assignedPermissionSets = assignedPermissionSets.stream()
                .filter(ps -> permissionSetIds.contains(ps.getPermissionSetId())).collect(Collectors.toList());

        List<RolePermission> cancelList = handleCancelingPermissionSets(assignedPermissionSets);

        result.addAll(cancelList);

        // 处理子角色
        if (CollectionUtils.isNotEmpty(role.getCreatedSubRoles())) {
            role.getCreatedSubRoles().forEach(createSubRole -> {
                result.addAll(handleCancelingCreatedSubRolePermissionSets(createSubRole, cancelList));
            });
        }
        return result;
    }

    /**
     * 取消回收权限集, createFlag=X -> createFlag=Y
     */
    private List<RolePermission> handleCancelingPermissionSets(List<RolePermission> assignedPermissionSets) {
        // 取消回收权限集 :: 原有创建标识为X, 代表需要取消回收
        return assignedPermissionSets.stream()
                .filter(ps -> YesNoFlag.DELETE.equals(ps.getCreateFlag()))
                .peek(rp -> {
                    rp.setCreateFlag(YesNoFlag.YES);
                    rp.setOperation(Operation.UPDATE);
                }).collect(Collectors.toList());
    }

    private Set<Long> getPermissionSetIds(List<RolePermission> rolePermissions) {
        return rolePermissions.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());
    }

}
