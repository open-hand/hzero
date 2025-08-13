package org.hzero.iam.infra.repository.impl;

import static org.hzero.iam.infra.constant.Constants.DIM_UNIQUE_KEY_TEMPLATE;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.mapper.RoleAuthorityMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 角色数据权限定义 资源库实现
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
@Component
public class RoleAuthorityRepositoryImpl extends BaseRepositoryImpl<RoleAuthority> implements RoleAuthorityRepository {

    @Autowired
    private RoleAuthorityMapper roleAuthorityMapper;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Page<RoleAuthorityDTO> listRoleAuthorityDTO(PageRequest pageRequest, Long roleId, Long tenantId, String docTypeName, String docTypeCode) {
        Role role = roleRepository.selectRoleSimpleById(roleId);
        Assert.notNull(role, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // FIX:查询角色数据权限的租户ID应使用角色所属租户ID
        Long roleTenantId = role.getTenantId();

        Page<RoleAuthorityDTO> page = PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(),
                () -> roleAuthorityMapper.listRoleAuthorityPage(roleId, roleTenantId, docTypeName));
        if (!CollectionUtils.isEmpty(page)) {
            Map<String, List<RoleAuthorityLine>> docTypeDim = roleAuthorityMapper.listDocTypeDim(roleId,
                    page.stream().map(RoleAuthorityDTO::getAuthDocTypeId).collect(Collectors.toSet()))
                    .stream()
                    .collect(Collectors.groupingBy(ral -> String.format(DIM_UNIQUE_KEY_TEMPLATE, ral.getDocTypeId(), ral.getDimensionType())));
            page.forEach(item -> item.setRoleAuthorityLines(docTypeDim.get(String.format(DIM_UNIQUE_KEY_TEMPLATE,
                    item.getAuthDocTypeId(), item.getAuthScopeCode()))));
        }
        return page;
    }

    @Override
    public Set<String> listRoleAssign(long roleId) {
        return roleAuthorityMapper.listRoleAssign(roleId, DetailsHelper.getUserDetails().getTenantId());
    }

    @Override
    public Set<String> listUserAssign(long userId) {
        return roleAuthorityMapper.listUserAssign(userId, DetailsHelper.getUserDetails().getTenantId());
    }

    @Override
    public Page<RoleAuthorityDTO> pageDocTypeAssignedRole(RoleAuthorityDTO roleAuthorityDTO, PageRequest pageRequest) {
        Page<RoleAuthorityDTO> page = PageHelper.doPageAndSort(pageRequest, () -> roleAuthorityMapper.listDocTypeAssignedRole(roleAuthorityDTO));
        if (!CollectionUtils.isEmpty(page)) {

            for (RoleAuthorityDTO roleAtuh : page) {
                List<RoleAuthorityLine> docTypeDim = roleAuthorityMapper.listDocTypeDim(roleAtuh.getRoleId(),
                        Collections.singleton(roleAtuh.getAuthDocTypeId()));
                roleAtuh.setRoleAuthorityLines(docTypeDim);
            }
        }
        return page;
    }

    @Override
    public List<String> selectCompareDimensions(Long roleId, Long copyRoleId) {
        return roleAuthorityMapper.selectCompareDimensions(roleId, copyRoleId);
    }

    @Override
    public List<RoleAuthority> selectByDocIds(List<Long> docIds) {
        return roleAuthorityMapper.selectByDocIds(docIds);
    }

    @Override
    public List<RoleAuthority> selectDocRoleAuth() {
        return roleAuthorityMapper.selectDocRoleAuth();
    }

    @Override
    public void batchDeleteByRoleAuthorityId(List<Long> roleAuthIds) {
        roleAuthorityMapper.batchDeleteByRoleAuthorityId(roleAuthIds);
    }
}
