package org.hzero.iam.domain.service.role;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.RootUserService;
import org.hzero.iam.domain.service.role.observer.RoleAssignObserver;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.mybatis.helper.SecurityTokenHelper;

/**
 * 角色成员分配服务
 *
 * @author bojiangzhou 2019/05/05
 */
public class MemberRoleAssignService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberRoleAssignService.class);

    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected MemberRoleRepository memberRoleRepository;
    @Autowired
    protected HpfmHrUnitRepository hrUnitRepository;
    @Autowired
    protected TenantRepository tenantRepository;
    @Autowired
    protected Optional<List<RoleAssignObserver>> optionalObservers;


    /**
     * 批量分配角色成员
     *
     * @param memberRoleList 角色成员列表
     * @param checkAuth      检查角色权限
     */
    public List<MemberRole> assignMemberRole(List<MemberRole> memberRoleList, boolean checkAuth) {
        LOGGER.debug("batch assign member-role start, memberRoleList={} ========", memberRoleList);

        if (CollectionUtils.isEmpty(memberRoleList)) {
            throw new CommonException("hiam.warn.memberRole.listIsEmpty");
        }

        // 检查有效性
        checkValidityAndInit(memberRoleList);

        // 检查角色
        checkRole(memberRoleList, true);

        // 检查成员
        checkMember(memberRoleList);

        if (checkAuth) {
            checkRoleManageable(memberRoleList);
        }

        // 插入或更新 MemberRole
        saveMemberRole(memberRoleList);

        optionalObservers.ifPresent(observers -> {
            for (RoleAssignObserver observer : observers) {
                observer.assignMemberRole(memberRoleList);
            }
        });

        return memberRoleList;
    }

    public void revokeMemberRole(List<MemberRole> memberRoleList, boolean checkAuth) {
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return;
        }
        // 默认为用户类型
        memberRoleList.forEach(mr -> {
            if (StringUtils.isBlank(mr.getMemberType())) {
                mr.setMemberType(HiamMemberType.USER.value());
            }
        });

        // 验证memberId/roleId必须存在
        long invalidRowCount = memberRoleList.stream()
                .filter(item -> item.getMemberId() == null || item.getRoleId() == null)
                .count();
        if (invalidRowCount > 0) {
            throw new CommonException("hiam.warn.memberRole.batchDeleteMissMemberIdOrRoleId");
        }

        checkRole(memberRoleList, false);

        CustomUserDetails self = UserUtils.getUserDetails();
        Long curUserId = self.getUserId();
        if (checkAuth) {
            // 自己的顶级角色不能删除
            for (MemberRole memberRole : memberRoleList) {
                if (HiamMemberType.USER.value().equals(memberRole.getMemberType()) && curUserId.equals(memberRole.getMemberId())) {
                    boolean isTopRole = roleRepository.isTopAdminRole(curUserId, memberRole.getRoleId());
                    if (isTopRole ) {
                        throw new CommonException("hiam.warn.role.denyOperationSelfTopRole");
                    }
                }
            }

            checkRoleManageable(memberRoleList);
        }

        // 批量删除
        memberRoleRepository.batchDelete(memberRoleList);

        optionalObservers.ifPresent(observers -> {
            for (RoleAssignObserver observer : observers) {
                observer.revokeMemberRole(memberRoleList);
            }
        });
    }

    /**
     * 检查有效性
     *
     * @param memberRoleList 成员角色
     */
    protected void checkValidityAndInit(List<MemberRole> memberRoleList) {
        for (MemberRole memberRole : memberRoleList) {
            Assert.notNull(memberRole.getMemberId(), "member id cannot be null");
            Assert.notNull(memberRole.getRoleId(), "role id cannot be null");
            // 默认 memberType=user
            memberRole.setMemberType(StringUtils.defaultIfBlank(memberRole.getMemberType(), HiamMemberType.USER.value()).toLowerCase());
        }
    }

    /**
     * 检查角色
     *
     * @param memberRoleList 成员角色
     */
    protected void checkRole(List<MemberRole> memberRoleList, boolean mustEnable) {
        List<Role> disabledRoles = new ArrayList<>(memberRoleList.size());
        Map<Long, Role> roleMap = new HashMap<>();
        for (MemberRole memberRole : memberRoleList) {
            Role role = roleMap.computeIfAbsent(memberRole.getRoleId(), (roleId) -> roleRepository.selectRoleSimpleById(roleId));
            if (role == null) {
                throw new CommonException("hiam.warn.memberRole.roleNotFound");
            }

            if (Boolean.FALSE.equals(role.getEnabled())) {
                disabledRoles.add(role);
            }

            memberRole.setSourceType(role.getLevel());
            memberRole.setSourceId(role.getTenantId());
            // 分配层级固定为 organization，分配层级没有实际作用，考虑兼容性保留
            memberRole.setAssignLevel(HiamResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(role.getTenantId());

            memberRole.setRole(role);
        }
        if (mustEnable && CollectionUtils.isNotEmpty(disabledRoles)) {
            throw new CommonException("hiam.warn.memberRole.roleDisabled",
                    disabledRoles.stream().map(Role::getName).collect(Collectors.toList()));
        }
    }

    /**
     * 检查角色是否在当前用户可分配范围内
     *
     * @param memberRoleList 成员角色列表
     */
    protected void checkRoleManageable(List<MemberRole> memberRoleList) {
        if (RootUserService.isRootUser()) {
            return;
        }

        List<Long> queryRoleIds = memberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        RoleVO params = new RoleVO();
        params.setQueryRoleIds(queryRoleIds);

        List<Long> roleIds = roleRepository.selectAllManageableRoleIds(params, DetailsHelper.getUserDetails());

        List<String> notBelongRoleNames = memberRoleList.stream()
                .map(MemberRole::getRole)
                .distinct()
                .filter(item -> !roleIds.contains(item.getId()))
                .map(Role::getName)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(notBelongRoleNames)) {
            throw new CommonException("hiam.warn.memberRole.roleNotBelongToSelfUser", notBelongRoleNames);
        }
    }

    /**
     * 检查成员
     *
     * @param memberRoleList 成员角色
     */
    protected void checkMember(List<MemberRole> memberRoleList) {
        List<User> adminUsers = new ArrayList<>(memberRoleList.size());
        Map<Long, User> userMap = new HashMap<>();
        Map<Long, Client> clientMap = new HashMap<>();
        for (MemberRole memberRole : memberRoleList) {
            if (HiamMemberType.USER.value().equalsIgnoreCase(memberRole.getMemberType())) {
                if (memberRole.getUser() == null) {
                    User user = userMap.computeIfAbsent(memberRole.getMemberId(), (memberId) -> userRepository.selectSimpleUserById(memberId));
                    Optional.ofNullable(user).orElseThrow(() -> new CommonException("hiam.warn.memberRole.userNotFound"));
                }
            } else if (HiamMemberType.CLIENT.value().equalsIgnoreCase(memberRole.getMemberType())) {
                if (memberRole.getClient() == null) {
                    Client client = clientMap.computeIfAbsent(memberRole.getMemberId(), (memberId) -> clientRepository.selectByPrimaryKey(memberId));
                    Optional.ofNullable(client).orElseThrow(() -> new CommonException("hiam.warn.memberRole.clientNotFound"));
                }
            } else {
                throw new CommonException("hiam.warn.memberRole.memberTypeError");
            }
        }
        if (CollectionUtils.isNotEmpty(adminUsers)) {
            throw new CommonException("hiam.warn.memberRole.adminUserCantAssignRole",
                    adminUsers.stream().map(User::getLoginName).collect(Collectors.toList()));
        }
    }

    /**
     * 插入或更新 MemberRole
     *
     * @param memberRoleList 成员角色列表
     */
    protected void saveMemberRole(List<MemberRole> memberRoleList) {
        SecurityTokenHelper.close();
        for (MemberRole memberRole : memberRoleList) {
            // 唯一查询
            MemberRole params = new MemberRole(memberRole.getRoleId(), memberRole.getMemberId(), memberRole.getMemberType());

            MemberRole existsMemberRole = memberRoleRepository.selectOne(params);

            if (existsMemberRole != null) {
                memberRole.setId(existsMemberRole.getId());
                memberRole.setObjectVersionNumber(existsMemberRole.getObjectVersionNumber());
                // 更新
                existsMemberRole.setStartDateActive(memberRole.getStartDateActive());
                existsMemberRole.setEndDateActive(memberRole.getEndDateActive());
                updateMemberRole(existsMemberRole);
            } else {
                memberRole.setId(null);
                memberRoleRepository.insertSelective(memberRole);
            }
        }
        SecurityTokenHelper.clear();
    }

    /**
     * 更新成员角色
     *
     * @param memberRole 待更新的角色成员
     */
    protected void updateMemberRole(MemberRole memberRole) {
        memberRoleRepository.updateOptional(memberRole,
                MemberRole.FIELD_START_DATE_ACTIVE,
                MemberRole.FIELD_END_DATE_ACTIVE);
    }


}
