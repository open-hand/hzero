package org.hzero.iam.domain.service.secgrp.authority.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAcl;
import org.hzero.iam.domain.entity.SecGrpRevoke;
import org.hzero.iam.domain.repository.SecGrpAclRepository;
import org.hzero.iam.domain.repository.SecGrpRevokeRepository;
import org.hzero.iam.domain.service.secgrp.authority.AbstractSecGrpAuthorityService;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全组权限服务——访问权限
 *
 * @author bojiangzhou 2020/02/12
 */
@Component
public class SecGrpAclAuthorityService extends AbstractSecGrpAuthorityService<SecGrpAcl> {
    @Autowired
    private SecGrpAclRepository secGrpAclRepository;
    @Autowired
    private SecGrpRevokeRepository revokeRepository;

    @Override
    public boolean support(@Nonnull SecGrpAuthorityType authorityType) {
        return SecGrpAuthorityType.ACL.equals(authorityType);
    }

    @Override
    public void copySecGrpAuthority(@Nonnull List<SecGrp> sourceSecGrps, @Nonnull SecGrp targetSecGrp) {
        List<Long> secGrpIds = sourceSecGrps.stream().map(SecGrp::getSecGrpId).collect(Collectors.toList());

        List<SecGrpAcl> secGrpAcls = secGrpAclRepository.selectRoleSecGrpAcls(targetSecGrp.getRoleId(), secGrpIds);

        if (CollectionUtils.isEmpty(secGrpAcls)) {
            return;
        }

        // 去重
        Set<Long> permissionIdSet = secGrpAcls.stream().map(SecGrpAcl::getPermissionId).collect(Collectors.toSet());
        // 插入权限
        List<SecGrpAcl> acls = permissionIdSet.parallelStream()
                .map(item -> new SecGrpAcl(targetSecGrp.getSecGrpId(), targetSecGrp.getTenantId(), item))
                .collect(Collectors.toList());
        secGrpAclRepository.batchAdd(acls);
    }

    @Override
    public void deleteAuthorityBySecGrpId(@Nonnull Long secGrpId) {
        secGrpAclRepository.delete(new SecGrpAcl(secGrpId));
    }

    @Override
    protected void addSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAcl> authorities) {
        if (CollectionUtils.isEmpty(authorities)) {
            return;
        }

        Long secGrpId = secGrp.getSecGrpId();
        Long tenantId = secGrp.getTenantId();

        // 添加安全组的访问权限
        authorities.parallelStream().forEach(acl -> {
            acl.setTenantId(tenantId);
            acl.setSecGrpId(secGrpId);
        });
        this.secGrpAclRepository.batchAdd(authorities);

        Set<Long> permissionIds = authorities.parallelStream().map(SecGrpAcl::getPermissionId).collect(Collectors.toSet());

        // 取消回收子安全组回收的权限
        cancelRevokeChildSecGrpAcl(secGrpId, permissionIds);
    }

    @Override
    protected void removeSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAcl> authorities) {
        if (CollectionUtils.isEmpty(authorities)) {
            return;
        }

        // 查询权限数据
        Set<Long> permissionIds = authorities.parallelStream().map(SecGrpAcl::getPermissionId).collect(Collectors.toSet());
        // 删除安全组访问权限
        this.secGrpAclRepository.batchRemove(secGrp.getSecGrpId(), permissionIds);

        // 回收子安全组的访问权限
        recycleChildSecGrpAcl(secGrp.getSecGrpId(), permissionIds);
    }

    @Override
    protected void enableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 查询安全组访问权限
        List<SecGrpAcl> secGrpAcls = this.secGrpAclRepository.selectAclInGrp(secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpAcls)) {
            // 取消回收安全组访问权限
            this.cancelRevokeChildSecGrpAcl(secGrpId, secGrpAcls.parallelStream()
                    .map(SecGrpAcl::getPermissionId).collect(Collectors.toSet()));
        }
    }

    @Override
    protected void disableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 查询安全组访问权限
        List<SecGrpAcl> secGrpAcls = this.secGrpAclRepository.select(SecGrpAcl.FIELD_SEC_GRP_ID, secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpAcls)) {
            // 回收安全组访问权限
            this.recycleChildSecGrpAcl(secGrpId, secGrpAcls.parallelStream()
                    .map(SecGrpAcl::getPermissionId).collect(Collectors.toSet()));
        }
    }

    @Override
    protected List<SecGrpAcl> getShieldRoleAuthority(Set<Long> authorityIds) {
        // 查询权限数据并返回
        return this.secGrpAclRepository.selectByIds(StringUtils.join(authorityIds, BaseConstants.Symbol.COMMA));
    }

    /**
     * 取消回收子安全组访问权限
     *
     * @param secGrpId      安全组ID
     * @param permissionIds 访问权限ID
     */
    private void cancelRevokeChildSecGrpAcl(Long secGrpId, Set<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        List<Role> assignedRoles = secGrpRepository.listSecGrpAssignedRole(secGrpId);
        if (CollectionUtils.isNotEmpty(assignedRoles)) {
            for (Role assignedRole : assignedRoles) {
                Long roleId = assignedRole.getId();
                // 查询角色其它安全组不包含的权限，这部分权限需要回收
                Set<Long> notIncludePermissionIds = secGrpAclRepository.listRoleNotIncludedAcl(roleId, secGrpId, permissionIds);
                if (CollectionUtils.isEmpty(notIncludePermissionIds)) {
                    continue;
                }

                // 查询角色创建的安全组中包含需回收权限的安全组
                List<SecGrpAcl> secGrpAcls = secGrpAclRepository.listRoleCreatedSecGrpAcl(roleId, notIncludePermissionIds);
                if (CollectionUtils.isEmpty(secGrpAcls)) {
                    continue;
                }

                // 按照安全组进行分组
                Map<Long, List<SecGrpAcl>> map = secGrpAcls.stream().collect(Collectors.groupingBy(SecGrpAcl::getSecGrpId));
                map.forEach((childSecGrpId, list) -> {
                    // 移除被回收的权限
                    revokeRepository.batchRemove(null,
                            childSecGrpId,
                            list.stream().map(SecGrpAcl::getSecGrpAclId).collect(Collectors.toSet()),
                            SecGrpAuthorityRevokeType.REVOKE, SecGrpAuthorityType.ACL);

                    cancelRevokeChildSecGrpAcl(childSecGrpId, list.stream()
                            .map(SecGrpAcl::getPermissionId).collect(Collectors.toSet()));
                });
            }
        }
    }

    /**
     * 回收子安全组访问权限
     *
     * @param secGrpId      安全组ID
     * @param permissionIds 权限ID
     */
    private void recycleChildSecGrpAcl(Long secGrpId, Set<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        // 查询安全组分配的角色
        List<Role> assignedRoles = secGrpRepository.listSecGrpAssignedRole(secGrpId);
        if (CollectionUtils.isNotEmpty(assignedRoles)) {
            for (Role assignedRole : assignedRoles) {
                Long roleId = assignedRole.getId();
                // 查询角色其它安全组不包含的权限，这部分权限需要回收
                Set<Long> notIncludePermissionIds = secGrpAclRepository.listRoleNotIncludedAcl(roleId, secGrpId, permissionIds);
                if (CollectionUtils.isEmpty(notIncludePermissionIds)) {
                    continue;
                }

                // 查询角色创建的安全组中包含需回收权限的安全组
                List<SecGrpAcl> secGrpAcls = secGrpAclRepository.listRoleCreatedSecGrpAcl(roleId, notIncludePermissionIds);
                if (CollectionUtils.isEmpty(secGrpAcls)) {
                    continue;
                }

                // 按照安全组进行分组
                Map<Long, List<SecGrpAcl>> map = secGrpAcls.stream().collect(Collectors.groupingBy(SecGrpAcl::getSecGrpId));
                map.forEach((childSecGrpId, list) -> {
                    // 添加到权限屏蔽(回收)
                    List<SecGrpRevoke> revokes = list.parallelStream().map(item -> SecGrpRevoke.build(childSecGrpId, item.getTenantId(),
                            item.getSecGrpAclId(), SecGrpAuthorityRevokeType.REVOKE, SecGrpAuthorityType.ACL)
                    ).collect(Collectors.toList());
                    revokeRepository.batchAdd(revokes);

                    recycleChildSecGrpAcl(childSecGrpId, list.stream().map(SecGrpAcl::getPermissionId).collect(Collectors.toSet()));
                });
            }
        }
    }
}
