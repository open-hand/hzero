package org.hzero.iam.domain.service.secgrp.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.secgrp.SecGrpCoreService;
import org.hzero.iam.domain.service.secgrp.authority.SecGrpAuthorityService;
import org.hzero.iam.domain.service.secgrp.authority.impl.*;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 安全组领域对象
 *
 * @author bojiangzhou 2012/02/12
 */
@Component
public class SecGrpCoreServiceImpl implements SecGrpCoreService {

    private final Logger logger = LoggerFactory.getLogger(SecGrpCoreServiceImpl.class);

    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private SecGrpRevokeRepository secGrpRevokeRepository;
    @Autowired
    private SecGrpAssignRepository secGrpAssignRepository;

    @Autowired
    private SecGrpAclAuthorityService aclAuthorityService;
    @Autowired
    private SecGrpDashAuthorityService dashAuthorityService;
    @Autowired
    private SecGrpFieldAuthorityService fieldAuthorityService;
    @Autowired
    private SecGrpDimAuthorityService dimAuthorityService;
    @Autowired
    private SecGrpDclAuthorityService dclAuthorityService;


    @Autowired
    private List<SecGrpAuthorityService<?>> authorityServices = new ArrayList<>();

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(11, 180, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000), new ThreadFactoryBuilder().setNameFormat("secGrpSvcPool-%d").build());

    @Override
    public void checkSecGrpExists(@NotNull Long tenantId, @NotNull String secGrpLevel, @NotNull String secGrpCode) {
        SecGrp query = new SecGrp();
        query.setSecGrpLevel(secGrpLevel);
        query.setTenantId(tenantId);
        query.setSecGrpCode(secGrpCode);
        if (secGrpRepository.selectCount(query) > 0) {
            throw new CommonException("hiam.error.secgrp.duplicate");
        }
    }

    @Override
    public void initSecGrpAuthority(@NotNull SecGrp secGrp) {
        List<AsyncTask<Boolean>> tasks = authorityServices.stream().map(authorityService -> new AsyncTask<Boolean>() {
            @Override
            public String taskName() {
                return authorityService.getClass().getSimpleName();
            }

            @Override
            public Boolean doExecute() {
                authorityService.initSecGrpAuthority(secGrp, secGrp.getRoleId());
                return Boolean.TRUE;
            }
        }).collect(Collectors.toList());

        CommonExecutor.batchExecuteAsync(tasks, executor, "initSecGrpAuthority");
    }

    @Override
    public void copySecGrpAuthority(@NotNull List<SecGrp> sourceSecGrps, @NotNull SecGrp targetSecGrp) {
        List<AsyncTask<Boolean>> tasks = authorityServices.stream().map(authorityService -> new AsyncTask<Boolean>() {
            @Override
            public String taskName() {
                return authorityService.getClass().getSimpleName();
            }

            @Override
            public Boolean doExecute() {
                authorityService.copySecGrpAuthority(sourceSecGrps, targetSecGrp);
                return Boolean.TRUE;
            }
        }).collect(Collectors.toList());

        CommonExecutor.batchExecuteAsync(tasks, executor, "copySecGrpAuthority");
    }

    @Override
    public void deleteSecGrp(@NotNull Long secGrpId) {
        for (SecGrpAuthorityService<?> authorityService : authorityServices) {
            authorityService.deleteAuthorityBySecGrpId(secGrpId);
        }

        // 删除安全组
        secGrpRepository.deleteByPrimaryKey(secGrpId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void disableSecGrp(@NotNull Long secGrpId) {
        // 处理权限回收
        for (SecGrpAuthorityService<?> authorityService : this.authorityServices) {
            // 回收权限
            authorityService.disableSecGrpAuthority(secGrpId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void enableSecGrp(@NotNull Long secGrpId) {
        // 处理权限分配
        for (SecGrpAuthorityService<?> authorityService : this.authorityServices) {
            // 分配权限
            authorityService.enableSecGrpAuthority(secGrpId);
        }
    }

    @Override
    public void assignRoleSecGrp(Long roleId, List<Long> secGrpIds) {
        logger.info("assign role SecGrp, roleId={}, secGrpIds={}", roleId, secGrpIds);

        // 判断是否有角色的操作权限
        Role role = checkRoleManageable(roleId);

        // 判断安全组是否可分配
        List<SecGrp> assignableSecGrps = checkRoleSecGrpAssignable(roleId, secGrpIds);

        // 分配安全组
        for (SecGrp secGrp : assignableSecGrps) {
            // 维护角色-安全组关系
            RolePermission rolePermission = new RolePermission(role.getId(), secGrp.getSecGrpId(), Constants.YesNoFlag.NO,
                    Constants.YesNoFlag.YES, RolePermissionType.SG.name(), role.getTenantId());
            rolePermissionRepository.insertSelective(rolePermission);

            for (SecGrpAuthorityService<?> authorityService : authorityServices) {
                authorityService.assignRoleSecGrpAuthority(role, secGrp);
            }
        }
    }

    @Override
    public void recycleRoleSecGrp(Long roleId, List<Long> secGrpIds) {
        logger.info("recycle role SecGrp, roleId={}, secGrpIds={}", roleId, secGrpIds);

        // 判断是否有角色的操作权限
        Role role = checkRoleManageable(roleId);

        // 判断安全组是否可分配
        List<SecGrp> assignableSecGrps = checkRoleSecGrpAssignable(roleId, secGrpIds);

        Map<Long, SecGrp> map = assignableSecGrps.stream().collect(Collectors.toMap(SecGrp::getSecGrpId, t -> t));

        RolePermission param = new RolePermission()
                .setRoleId(role.getId())
                .setPermissionSetIds(new HashSet<>(secGrpIds))
                .setType(RolePermissionType.SG.name());

        List<RolePermission> rolePermissions = rolePermissionRepository.selectRolePermissionSets(param);

        if (CollectionUtils.isEmpty(rolePermissions)) {
            logger.info("Nothing need recycle from role :{}", role.getId());
        }

        // 回收安全组
        for (RolePermission rp : rolePermissions) {
            rolePermissionRepository.deleteByPrimaryKey(rp.getId());

            SecGrp secGrp = map.get(rp.getPermissionSetId());

            // 回收安全组权限
            for (SecGrpAuthorityService<?> authorityService : authorityServices) {
                authorityService.recycleRoleSecGrpAuthority(role, secGrp);
            }
        }
    }

    @Override
    public void shieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, SecGrpAuthorityType authorityType) {
        // 判断是否有角色的操作权限
        Role role = checkRoleManageable(roleId);
        // 判断安全组是否可分配
        SecGrp secGrp = checkRoleSecGrpAssignable(roleId, Collections.singletonList(secGrpId)).get(0);

        // 权限屏蔽数据
        SecGrpRevoke secGrpRevoke = SecGrpRevoke.build(secGrp, role, authorityId, SecGrpAuthorityRevokeType.SHIELD, authorityType);
        // 判断当前屏蔽数据是否存在，不存在才进行屏蔽的处理
        if (this.secGrpRevokeRepository.selectCount(secGrpRevoke) == 0) {
            // 权限回收
            for (SecGrpAuthorityService<?> authorityService : authorityServices) {
                if (authorityService.support(authorityType)) {
                    // 回收权限
                    authorityService.shieldRoleAuthority(role, Collections.singleton(authorityId));
                }
            }

            // 插入权限表
            this.secGrpRevokeRepository.insertSelective(secGrpRevoke);
        }
    }

    @Override
    public void cancelShieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, SecGrpAuthorityType authorityType) {
        // 判断是否有角色的操作权限
        Role role = checkRoleManageable(roleId);
        // 判断安全组是否可分配
        SecGrp secGrp = checkRoleSecGrpAssignable(roleId, Collections.singletonList(secGrpId)).get(0);

        // 权限屏蔽数据
        SecGrpRevoke secGrpRevoke = SecGrpRevoke.build(secGrp, role, authorityId, SecGrpAuthorityRevokeType.SHIELD, authorityType);
        // 判断当前屏蔽数据是否存在，存在才进行屏蔽的处理
        if (this.secGrpRevokeRepository.selectCount(secGrpRevoke) == 1) {
            // 删除权限屏蔽关系
            this.secGrpRevokeRepository.delete(secGrpRevoke);

            // 取消权限屏蔽前触发权限分配
            for (SecGrpAuthorityService<?> authorityService : authorityServices) {
                if (authorityService.support(authorityType)) {
                    // 分配权限
                    authorityService.cancelShieldRoleAuthority(role, Collections.singleton(authorityId));
                }
            }
        }
    }

    /**
     * 分配用户安全组需要的步骤
     * 1. 保存映射关系
     * 2. 保存字段权限、数据维护权限、数据权限到表中
     * 2. 应用字段权限(缓存)
     */
    @Override
    public void assignUserSecGrp(Long userId, List<Long> secGrpIds) {
        logger.info("assign user SecGrp, roleId={}, secGrpIds={}", userId, secGrpIds);

        // 判断是否有用户的操作权限
        User user = checkUserManageable(userId);

        // 判断安全组是否可分配
        List<SecGrp> assignableSecGrps = checkUserSecGrpAssignable(userId, secGrpIds);

        // 分配安全组
        for (SecGrp secGrp : assignableSecGrps) {
            // 保存用户和安全组关系
            SecGrpAssign secGrpAssign = new SecGrpAssign(Constants.SecGrpAssign.USER_DIMENSION, user.getId(), secGrp.getSecGrpId(), user.getOrganizationId());
            secGrpAssignRepository.insertSelective(secGrpAssign);

            // 分配用户权限
            List<AsyncTask<Boolean>> tasks = authorityServices.stream().map(authorityService -> new AsyncTask<Boolean>() {
                @Override
                public String taskName() {
                    return authorityService.getClass().getSimpleName();
                }

                @Override
                public Boolean doExecute() {
                    authorityService.assignUserSecGrpAuthority(user, secGrp);
                    return Boolean.TRUE;
                }
            }).collect(Collectors.toList());

            CommonExecutor.batchExecuteAsync(tasks, executor, "assignUserSecGrp");
        }
    }

    /**
     * 用户取消安全组
     * <p>
     * 1. 解除用户和安全组的关系(hiam_sec_grp_assign)
     * 2. 删除 用户权限(hiam_user_authority)相关的记录
     * - 2.0 通过secGrpId查找到对应的authority_type_code,在通过userId,authority_type_code,tenantId删除记录
     * - 2.1 判断是否具有默认权限
     * - 2.2 不存在默认权限,删除该记录
     * - 2.3 删除 用户权限所关联的用户权限行(hiam_user_authority_line)的记录
     * - 2.3.0 通过删除的authority_id 查找对应的authority_line_id,并判断dataSouce=SEC_GRP
     * - 2.3.1 判断是否具有默认权限
     * - 2.3.2 不存在默认权限, 删除该记录
     * 4. 删除 接口字段权限(hiam_field_permission)
     * - 4.1 查找本次安全组所关联的字段权限
     * - 4.2 判断是否具有默认权限
     * - 4.3 不存在默认权限，删除该记录, 并删除对应的缓存
     *
     * @param userId    用户ID
     * @param secGrpIds 安全组ID
     */
    @Override
    public void recycleUserSecGrp(Long userId, List<Long> secGrpIds) {
        logger.info("recycle user SecGrp, roleId={}, secGrpIds={}", userId, secGrpIds);

        // 判断是否有用户的操作权限
        User user = checkUserManageable(userId);

        // 判断安全组是否可分配
        List<SecGrp> assignableSecGrps = checkUserSecGrpAssignable(userId, secGrpIds);

        for (SecGrp secGrp : assignableSecGrps) {
            // 删除用户和安全组关系
            SecGrpAssign secGrpAssign = new SecGrpAssign(Constants.SecGrpAssign.USER_DIMENSION, user.getId(), secGrp.getSecGrpId(), user.getOrganizationId());
            secGrpAssignRepository.delete(secGrpAssign);

            // 删除用户权限
            for (SecGrpAuthorityService<?> authorityService : authorityServices) {
                authorityService.recycleUserSecGrpAuthority(user, secGrp);
            }
        }
    }

    /**
     * 检查角色是否可分配此安全组
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID
     * @return 可分配则返回安全组，不可分配则抛出异常
     */
    private List<SecGrp> checkRoleSecGrpAssignable(Long roleId, List<Long> secGrpIds) {
        List<SecGrp> assignableSecGrps = secGrpRepository.listRoleAssignableSecGrp(roleId, secGrpIds);
        Set<Long> assignableSecGrpIds = assignableSecGrps.stream().map(SecGrp::getSecGrpId).collect(Collectors.toSet());
        for (Long secGrpId : secGrpIds) {
            if (!assignableSecGrpIds.contains(secGrpId)) {
                throw new CommonException("hiam.warn.secGrp.noAuthorityToSecGrp");
            }
        }
        return assignableSecGrps;
    }

    /**
     * 检查角色是否在用户管理范围内
     *
     * @param roleId 角色ID
     * @return 在管理范围内则返回角色，否则抛出异常
     */
    private Role checkRoleManageable(Long roleId) {
        List<RoleVO> manageableRoles = roleRepository.selectSelfAllManageableRoles(new RoleVO().setId(roleId));
        if (CollectionUtils.isEmpty(manageableRoles)) {
            throw new CommonException("hiam.warn.secGrp.noAuthorityToRole");
        }
        return manageableRoles.get(0).toRole();
    }

    /**
     * 检查用户是否可分配此安全组
     *
     * @param userId    用户ID
     * @param secGrpIds 安全组ID
     * @return 可分配则返回安全组，不可分配则抛出异常
     */
    private List<SecGrp> checkUserSecGrpAssignable(Long userId, List<Long> secGrpIds) {
        List<SecGrp> assignableSecGrps = secGrpRepository.listUserAssignableSecGrp(userId, secGrpIds);
        Set<Long> assignableSecGrpIds = assignableSecGrps.stream().map(SecGrp::getSecGrpId).collect(Collectors.toSet());
        for (Long secGrpId : secGrpIds) {
            if (!assignableSecGrpIds.contains(secGrpId)) {
                throw new CommonException("hiam.warn.secGrp.noAuthorityToSecGrp");
            }
        }
        return assignableSecGrps;
    }

    /**
     * 检查用户是否在管理范围内
     *
     * @param userId 用户ID
     * @return 在管理范围内则返回用户，否则抛出异常
     */
    private User checkUserManageable(Long userId) {
        User user = userRepository.selectSimpleUserById(userId);
        CustomUserDetails self = UserUtils.getUserDetails();
        // 如果当前用户是平台级或者用户所属租户ID一致才可以给用户分配
        if (self.getSiteRoleIds().contains(self.getRoleId()) || Objects.equals(self.getTenantId(), user.getOrganizationId())) {
            return user;
        } else {
            throw new CommonException("hiam.warn.secGrp.noAuthorityToUser");
        }
    }
}
