package org.hzero.iam.app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.algorithm.tree.Node;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.cache.ProcessCacheValue;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.RolePermissionRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.service.role.*;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.CheckedFlag;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 角色应用服务默认实现类
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 10:39
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleCreateService roleCreateService;
    @Autowired
    private TemplateRoleCreateService templateRoleCreateService;
    @Autowired
    private RoleUpdateService roleUpdateService;
    @Autowired
    private RolePermissionSetAssignService rolePermissionSetAssignService;
    @Autowired
    private RolePermissionSetRecycleService rolePermissionSetRecycleService;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(Role role, boolean inherited, boolean duplicate) {
        CustomUserDetails details = UserUtils.getUserDetails();
        User adminUser = new User();
        adminUser.setId(details.getUserId());
        return roleCreateService.createRole(role, adminUser, inherited, duplicate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRoleByRoleTpl(User user, Tenant tenant, Role parentRole, String roleTplCode) {
        // parentRole 必传参数校验
        Assert.notNull(parentRole, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(parentRole.getId(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(user.getId(), BaseConstants.ErrorCode.DATA_INVALID);

        // tenant 必传参数校验 以及 模板参数校验
        Assert.notNull(tenant, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(tenant.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(StringUtils.isNotEmpty(roleTplCode), BaseConstants.ErrorCode.DATA_INVALID);

        // 入参日志打印
        LOGGER.info("parent role is: {}\n tenant is {}\n roleTplCode is {}", parentRole, tenant, roleTplCode);

        // 获取租户管理员模板信息
        Role roleTpl = roleRepository.selectOneRoleByCode(roleTplCode);
        Assert.notNull(roleTpl, BaseConstants.ErrorCode.DATA_INVALID);

        return templateRoleCreateService.createRoleByTpl(parentRole, roleTpl, tenant, user, null);
    }

    @Override
    public List<Role> listTenantAdmin(Long tenantId) {
        return roleRepository.listTenantAdmin(tenantId);
    }

    @Override
    public List<RoleDTO> listTreeList(RoleVO roleVO) {

        CustomUserDetails self = UserUtils.getUserDetails();
        roleVO = Optional.ofNullable(roleVO).orElse(new RoleVO());
        roleVO.setUserId(self.getUserId());

        // 查询组装树所需原料数据
        List<RoleVO> roleVOList = roleRepository.selectUserManageableRoleTree(roleVO);
        List<RoleDTO> treeNodes = RoleDTO.convertEntityToTreeDTO(roleVOList);
        treeNodes.forEach(item -> {
            if (treeNodes.stream().noneMatch(parent -> parent.getId().equals(item.getParentRoleId()))) {
                item.setParentRoleId(null);
            }
        });

        // 构建树
        return TreeBuilder.buildTree(treeNodes, new Node<Long, RoleDTO>() {
            @Override
            public Long getKey(RoleDTO obj) {
                return obj.getId();
            }

            @Override
            public Long getParentKey(RoleDTO obj) {
                // 检查父级节点是否存在
                return obj.getParentRoleId();
            }
        });
    }

    @Override
    public List<Role> selectUserRole(Long organizationId, Long userId) {
        return roleRepository.selectUserRole(organizationId, userId);
    }

    @Override
    public Page<RoleVO> selectSelfManageableRoles(RoleVO params, PageRequest pageRequest) {
        return this.roleRepository.selectSelfManageableRoles(params, pageRequest);
    }

    @Override
    public RoleVO selectRoleDetails(Long roleId) {
        return roleRepository.selectRoleDetails(roleId);
    }

    @Override
    @ProcessLovValue
    @ProcessCacheValue
    public Page<RoleVO> selectManageableRoles(RoleVO params, PageRequest pageRequest) {
        return roleRepository.pageManageableRoles(params, pageRequest);
    }

    @Override
    //@ProcessLovValue
    public Page<RoleVO> selectMemberAssignableRoles(RoleVO params, PageRequest pageRequest) {
        params.setMemberType(Optional.ofNullable(params.getMemberType()).orElse(HiamMemberType.USER.value()));
        return roleRepository.pageMemberAssignableRoles(params, pageRequest);
    }

    @Override
    @ProcessLovValue
    @ProcessCacheValue
    public Page<RoleVO> selectTreeManageableRoles(RoleVO params, PageRequest pageRequest) {
        // 父级角色为空时，查询已分配的角色作为顶级角色
        Page<RoleVO> page = null;
        if (params.getParentRoleId() == null) {
            page = roleRepository.selectSelfAssignedRolesForTree(params, pageRequest);
        } else {
            page = roleRepository.selectNextSubRoles(params, pageRequest);
        }

        // 前端需要...
        if (CollectionUtils.isNotEmpty(page)) {
            for (RoleVO roleVO : page) {
                roleVO.setChildren(Collections.emptyList());
            }
        }

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(Role role) {
        return roleUpdateService.updateRole(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableRole(Long roleId) {
        roleUpdateService.enableRole(roleId, true, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableRole(Long roleId) {
        // FIX: 设置为 false，禁用角色时不禁用子孙角色
        roleUpdateService.disableRole(roleId, false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignRolePermissionSets(Long roleId, Set<Long> permissionSetIds, String type) {
        rolePermissionSetAssignService.assignRolePermissionSets(roleId, permissionSetIds, type);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void recycleRolePermissionSets(Long roleId, Set<Long> permissionSetIds, String type) {
        rolePermissionSetRecycleService.recycleRolePermissionSets(roleId, permissionSetIds, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void directAssignRolePermission(RolePermission rolePermission) {
        String type = RolePermissionType.value(rolePermission.getType()).name();

        Assert.isTrue(CollectionUtils.isNotEmpty(rolePermission.getPermissionSetIds()), "permissionSetIds is empty.");

        Long roleId = rolePermission.getRoleId();

        Role role = null;
        // 角色ID、角色编码只能传一个
        if (roleId != null) {
            role = roleRepository.selectRoleSimpleById(roleId);
            if (role == null) {
                throw new CommonException("hiam.warn.roleNotFoundForId", roleId);
            }
        } else if (StringUtils.isNotBlank(rolePermission.getLevelPath())) {
            role = roleRepository.selectRoleSimpleByLevelPath(rolePermission.getLevelPath());
            if (role == null) {
                throw new CommonException("hiam.warn.roleNotFoundForLevelPath", rolePermission.getLevelPath());
            }
        }
        // 考虑历史接口，做个补充
        else if (StringUtils.isNotBlank(rolePermission.getRoleCode())) {
            role = roleRepository.selectRoleSimpleByCode(rolePermission.getRoleCode());
            if (role == null) {
                throw new CommonException("hiam.warn.roleNotFoundForCode", rolePermission.getRoleCode());
            }
        }
        Assert.notNull(role, "Role is null.");
        roleId = role.getId();

        // 默认设置创建标识
        String createFlag = StringUtils.defaultIfBlank(rolePermission.getCreateFlag(), CheckedFlag.CHECKED.value());
        String inheritFlag = StringUtils.defaultIfBlank(rolePermission.getInheritFlag(), CheckedFlag.UNCHECKED.value());

        Long finalRoleId = roleId;
        Long finalTenantId = role.getTenantId();
        List<RolePermission> rolePermissionList = rolePermission.getPermissionSetIds().stream().map(
                permissionId -> new RolePermission(finalRoleId, permissionId, inheritFlag, createFlag, type, finalTenantId))
                .collect(Collectors.toList());

        for (RolePermission permission : rolePermissionList) {
            // 由于rolePermission who字段是在get和set方法中赋值，因此这里不可使用别的方法判断是否存在
            List<RolePermission> exists = rolePermissionRepository.selectByCondition(Condition.builder(RolePermission.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(RolePermission.FIELD_PERMISSION_SET_ID, permission.getPermissionSetId())
                            .andEqualTo(RolePermission.FIELD_ROLE_ID, roleId)
                            .andEqualTo(RolePermission.FIELD_TYPE, type)
                    )
                    .build());
            if (CollectionUtils.isNotEmpty(exists) && exists.size() == 1) {
                rolePermissionRepository.updateByPrimaryKey(exists.get(0));
            } else {
                rolePermissionRepository.insertSelective(permission);
            }
        }
    }

}
