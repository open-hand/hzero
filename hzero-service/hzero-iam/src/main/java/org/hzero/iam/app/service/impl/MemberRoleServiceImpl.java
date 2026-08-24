package org.hzero.iam.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.hzero.iam.api.dto.MemberRoleAssignDTO;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.role.MemberRoleAssignService;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamResourceLevel;

/**
 * @author allen
 */
@Service
public class MemberRoleServiceImpl implements MemberRoleService {

    @Autowired
    private MemberRoleRepository memberRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRoleAssignService memberRoleAssignService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<MemberRole> batchAssignMemberRole(List<MemberRole> memberRoleList) {
        return memberRoleAssignService.assignMemberRole(memberRoleList, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<MemberRole> batchAssignMemberRoleInternal(List<MemberRole> memberRoleList) {
        return memberRoleAssignService.assignMemberRole(memberRoleList, false);
    }

    @Override
    public void batchDeleteMemberRole(Long organizationId, List<MemberRole> memberRoleList) {
        memberRoleAssignService.revokeMemberRole(memberRoleList, true);
    }

    @Override
    public void batchDeleteMemberRoleInternal(Long organizationId, List<MemberRole> memberRoleList) {
        memberRoleAssignService.revokeMemberRole(memberRoleList, false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<MemberRole> batchAssignMemberRoleOnTenant(List<MemberRoleAssignDTO> memberRoleAssignDTOList) {
        if (CollectionUtils.isEmpty(memberRoleAssignDTOList)) {
            return Collections.emptyList();
        }

        // 验证参数
        memberRoleAssignDTOList.forEach(item -> {
            if (item.getUserId() == null && item.getLoginName() == null) {
                throw new IllegalArgumentException("At least one of user id and login name must be not null");
            }
            if (item.getRoleId() == null && item.getRoleCode() == null) {
                throw new IllegalArgumentException("At least one of role id and role code must be not null");
            }
        });

        // 循环处理
        List<MemberRole> resultMemberRoleList = new ArrayList<>(10);
        memberRoleAssignDTOList.forEach(item -> {
            User user = this.validateUserParam(item);
            Role role = this.validateRoleParam(item);

            MemberRole memberRole = new MemberRole(role.getId(), user.getId(), Constants.MemberType.USER);

            // 先查询, 再创建
            MemberRole existsMemberRole = memberRoleRepository.selectOne(memberRole);
            if (existsMemberRole != null) {
                resultMemberRoleList.add(existsMemberRole);
            } else {
                memberRole.setAssignLevel(HiamResourceLevel.ORGANIZATION.value());
                memberRole.setAssignLevelValue(role.getTenantId());

                List<MemberRole> localResultMemberRoleList = this.batchAssignMemberRoleInternal(Collections.singletonList(memberRole));
                resultMemberRoleList.addAll(localResultMemberRoleList);
            }
        });

        return resultMemberRoleList;
    }

    /**
     * 验证并返回用户信息
     */
    private User validateUserParam(MemberRoleAssignDTO memberRoleAssignDTO) {
        User user = null;
        if (memberRoleAssignDTO.getUserId() != null) {
            user = userRepository.selectByPrimaryKey(memberRoleAssignDTO.getUserId());
        } else if (memberRoleAssignDTO.getLoginName() != null) {
            User sampleUser = new User();
            sampleUser.setLoginName(memberRoleAssignDTO.getLoginName());
            user = userRepository.selectOne(sampleUser);
        }

        Assert.notNull(user, String.format("User is invalid(userId = %s, loginName = %s)",
                memberRoleAssignDTO.getUserId(),
                memberRoleAssignDTO.getLoginName()));

        return user;
    }

    /**
     * 验证角色参数
     */
    private Role validateRoleParam(MemberRoleAssignDTO memberRoleAssignDTO) {
        Role role = null;

        if (memberRoleAssignDTO.getRoleId() != null) {
            role = roleRepository.selectByPrimaryKey(memberRoleAssignDTO.getRoleId());
        } else if (memberRoleAssignDTO.getRoleCode() != null) {
            Role sampleRole = new Role();
            sampleRole.setCode(memberRoleAssignDTO.getRoleCode());
            role = roleRepository.selectOne(sampleRole);
        }

        Assert.notNull(role, String.format("Role is invalid(roleId = %s, roleCode = %s)",
                memberRoleAssignDTO.getRoleId(),
                memberRoleAssignDTO.getRoleCode()));

        return role;
    }

}
