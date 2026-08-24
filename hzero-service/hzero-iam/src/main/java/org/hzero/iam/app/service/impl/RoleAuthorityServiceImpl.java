package org.hzero.iam.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.app.service.RoleAuthorityService;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.domain.service.role.RoleAuthorityDomainService;
import org.hzero.iam.infra.common.utils.UserUtils;

/**
 * 角色数据权限定义应用服务默认实现
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
@Service
public class RoleAuthorityServiceImpl implements RoleAuthorityService {

    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;
    @Autowired
    private RoleAuthorityDomainService roleAuthorityDomainService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOrUpdateRoleAuthority(Long roleId, List<RoleAuthorityDTO> roleAuthorityDtos) {
        Assert.notNull(roleAuthorityDtos, BaseConstants.ErrorCode.NOT_NULL);
        roleAuthorityDomainService.setRoleId(roleAuthorityDtos, roleId);
        batchCreateOrUpdateRoleAuthority(roleAuthorityDtos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOrUpdateRoleAuthority(List<RoleAuthorityDTO> roleAuthorityDtos) {
        Assert.notNull(roleAuthorityDtos, BaseConstants.ErrorCode.NOT_NULL);
        roleAuthorityDtos.forEach(roleAuthorityDTO -> {
            if (roleAuthorityDTO.getRoleAuthId() == null) {
                roleAuthorityDomainService.createRoleAuthority(roleAuthorityDTO);
            } else if (roleAuthorityDTO.getDocEnabledFlag() == 1) {
                roleAuthorityDomainService.updateRoleAuthority(roleAuthorityDTO);
            } else if (roleAuthorityDTO.getDocEnabledFlag() == 0) {
                roleAuthorityDomainService.deleteRoleAuthority(roleAuthorityDTO.getRoleAuthId());
            }
        });
    }

    @Override
    @ProcessLovValue(targetField = {"", "roleAuthorityLines"})
    public Page<RoleAuthorityDTO> pageList(PageRequest pageRequest, Long roleId, Long tenantId, String docTypeName,
                    String docTypeCode) {
        return roleAuthorityRepository.listRoleAuthorityDTO(pageRequest, roleId, tenantId, docTypeName, docTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleAuthority(Long roleAuthId) {
        roleAuthorityDomainService.deleteRoleAuthority(roleAuthId);
    }

    @Override
    public Page<RoleAuthorityDTO> pageDocTypeAssignRoles(Long tenantId, Long docTypeId, String roleCode,
                    PageRequest pageRequest) {
        RoleAuthorityDTO roleAuthorityDTO = new RoleAuthorityDTO();
        roleAuthorityDTO.setAuthDocTypeId(docTypeId);
        roleAuthorityDTO.setRoleCode(roleCode);
        roleAuthorityDTO.setTenantId(tenantId);
        roleAuthorityDTO.setOrganizationId(UserUtils.getUserDetails().getOrganizationId());
        roleAuthorityDTO.setUserId(UserUtils.getUserDetails().getUserId());
        return roleAuthorityRepository.pageDocTypeAssignedRole(roleAuthorityDTO, pageRequest);
    }


}
