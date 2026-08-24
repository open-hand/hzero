package org.hzero.iam.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.api.dto.SecGrpPermissionSearchDTO;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAcl;
import org.hzero.iam.domain.repository.SecGrpAclRepository;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.infra.common.utils.HiamMenuUtils;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.MenuMapper;
import org.hzero.iam.infra.mapper.SecGrpAclMapper;
import org.hzero.iam.infra.util.BatchSqlHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全组访问权限 资源库实现
 *
 * @author bojiangzhou 2020/02/17 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Component
public class SecGrpAclRepositoryImpl extends BaseRepositoryImpl<SecGrpAcl> implements SecGrpAclRepository {
    @Autowired
    private SecGrpAclMapper secGrpAclMapper;
    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> listSecGrpAssignableAcl(@Nullable Long tenantId, @NotNull Long secGrpId) {
        SecGrp secGrp = secGrpRepository.querySecGrp(tenantId, secGrpId);

        SecGrpPermissionSearchDTO searchDTO = new SecGrpPermissionSearchDTO();
        //层级和安全组保持一致
        searchDTO.setSecGrpLevel(secGrp.getSecGrpLevel());
        searchDTO.setSecGrpId(secGrpId);
        //查询的时候CurrentRoleId取安全组绑定的角色ID
        searchDTO.setRoleId(secGrp.getRoleId());
        searchDTO.setTenantId(secGrp.getTenantId());

        return HiamMenuUtils.formatMenuListToTree(menuMapper.selectSecGrpAssignablePermissionSet(searchDTO), true);
    }

    @Override
    public List<Menu> listSecGrpAssignedAcl(@Nullable Long tenantId, @NotNull Long secGrpId, SecGrpPermissionSearchDTO queryDTO) {
        Assert.notNull(queryDTO.getRoleId(), "Param roleId should not null.");
        Assert.notNull(queryDTO.getSecGrpSource(), "Param secGrpSource should not null.");
        queryDTO.setSecGrpId(secGrpId);
        return HiamMenuUtils.formatMenuListToTree(menuMapper.selectSecGrpAssignedPermissionSet(queryDTO), true);
    }

    @Override
    public List<Menu> listRoleSecGrpAcl(Long roleId, Long secGrpId) {
        return HiamMenuUtils.formatMenuListToTree(menuMapper.selectRoleSecGrpAssignablePermissionSet(roleId, secGrpId), true);
    }

    @Override
    public List<SecGrpAcl> selectAclInGrp(Long secGrpId) {
        return secGrpAclMapper.selectAclInGrp(secGrpId);
    }

    @Override
    public List<SecGrpAcl> selectAclInRole(Long roleId) {
        return secGrpAclMapper.selectAclInRole(roleId);
    }

    @Override
    public List<SecGrpAcl> selectRoleSecGrpAcls(Long roleId, List<Long> secGrpIds) {
        return secGrpAclMapper.selectRoleSecGrpAcls(roleId, secGrpIds);
    }

    @Override
    public List<SecGrpAcl> selectSecGrpAclBindPermissionIdInRoleAndSubRole(Long roleId, List<Long> permissionIds, Integer includeRevokeFlag) {
        return secGrpAclMapper.selectSecGrpAclBindPermissionIdInRoleAndSubRole(roleId, permissionIds, includeRevokeFlag);
    }

    @Override
    public List<SecGrpAcl> selectSecGrpAclBindPermissionIdInRole(Long roleId, List<Long> permissionIds, Integer includeRevokeFlag) {
        return secGrpAclMapper.selectSecGrpAclBindPermissionIdInRole(roleId, permissionIds, includeRevokeFlag);
    }

    @Override
    public void batchAdd(List<SecGrpAcl> acls) {
        if (CollectionUtils.isEmpty(acls)) {
            return;
        }
        UserUtils.setDataUser(acls);

        BatchSqlHelper.batchExecute(acls, 6,
                (dataList) -> secGrpAclMapper.batchInsertBySql(dataList),
                "BatchInsertSecGrpAcl");
    }

    @Override
    public void batchRemove(Long secGrpId, Set<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        secGrpAclMapper.batchDeleteBySql(secGrpId, permissionIds);
    }

    @Override
    public Set<Long> listRoleNotIncludedAcl(Long roleId, Long excludeSecGrpId, Set<Long> permissionIds) {
        Set<Long> includedPermissionIds = secGrpAclMapper.selectRoleOtherSecGrpAcls(roleId, excludeSecGrpId, permissionIds)
                .stream().map(SecGrpAcl::getPermissionId).collect(Collectors.toSet());
        return permissionIds.stream().filter(item -> !includedPermissionIds.contains(item)).collect(Collectors.toSet());
    }

    @Override
    public List<SecGrpAcl> listRoleCreatedSecGrpAcl(Long roleId, Set<Long> permissionIds) {
        return secGrpAclMapper.selectRoleCreatedSecGrpAcl(roleId, permissionIds);
    }
}
