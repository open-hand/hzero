package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.RoleSecGrpDTO;
import org.hzero.iam.api.dto.SecGrpQueryDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.iam.infra.mapper.SecGrpMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 安全组 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Component
public class SecGrpRepositoryImpl extends BaseRepositoryImpl<SecGrp> implements SecGrpRepository {
    @Autowired
    private SecGrpMapper secGrpMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    @ProcessLovValue
    public Page<SecGrp> listSecGrp(@Nullable Long tenantId, SecGrpQueryDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setupCurrentRole();
        queryDTO.setTenantId(tenantId);

        SecGrp secGrp = new SecGrp();
        secGrp.setSecGrpSource(queryDTO.getSecGrpSource());
        Page<SecGrp> page;
        if (secGrp.isSelfBuild()) {
            page = PageHelper.doPageAndSort(pageRequest, () -> secGrpMapper.selectRoleCreatedSecGrp(queryDTO));
        } else if (secGrp.isChildren()) {
            page = PageHelper.doPageAndSort(pageRequest, () -> secGrpMapper.selectChildCreatedSecGrp(queryDTO));
        } else if (secGrp.isParentAssigned()) {
            page = PageHelper.doPageAndSort(pageRequest, () -> secGrpMapper.selectRoleAssignedSecGrp(queryDTO));
        } else {
            throw new IllegalArgumentException("Param secGrpSource invalid.");
        }

        if (page != null && CollectionUtils.isNotEmpty(page.getContent())) {
            page.getContent().forEach(item -> item.setupEditable(queryDTO.getCurrentRoleId()));
        }

        return page;
    }

    @Override
    @ProcessLovValue
    public Page<SecGrp> listSecGrpForQuickCreate(@Nullable Long tenantId, SecGrpQueryDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setTenantId(tenantId);
        queryDTO.setupCurrentRole();
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpMapper.selectAuthorizedSecGrp(null, queryDTO));
    }

    @Override
    @ProcessLovValue
    public SecGrp querySecGrp(@Nullable Long tenantId, @Nullable Long roleId, @Nullable Long secGrpId) {
        SecGrp secGrp = secGrpMapper.selectDetail(tenantId, secGrpId, roleId);
        if (null == secGrp) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        return secGrp;
    }

    @Override
    public SecGrp querySecGrp(@Nullable Long tenantId, @Nullable Long secGrpId) {
        return querySecGrp(tenantId, null, secGrpId);
    }

    @Override
    public SecGrp querySecGrp(@Nullable Long secGrpId) {
        return querySecGrp(null, null, secGrpId);
    }

    @Override
    @ProcessLovValue
    public Page<SecGrp> listRoleAssignedSecGrp(Long roleId, SecGrpQueryDTO dto, PageRequest pageRequest) {
        dto.setCurrentRoleId(roleId);
        return PageHelper.doPage(pageRequest, () -> secGrpMapper.selectRoleAssignedSecGrp(dto));
    }

    @Override
    @ProcessLovValue
    public Page<SecGrp> listRoleAssignableSecGrp(Long roleId, SecGrpQueryDTO dto, PageRequest pageRequest) {
        Role role = roleRepository.selectByPrimaryKey(roleId);
        Assert.notNull(role, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

        if (Role.ROOT_ID.equals(role.getParentRoleId())) {
            throw new CommonException("hiam.warn.secGrp.superRoleCantAssignRole");
        }
        // 判断当前用户是否有当前待分配角色的父级角色
        if (!DetailsHelper.getUserDetails().roleMergeIds().contains(role.getParentRoleId())) {
            return new Page<>(Collections.emptyList(), new PageInfo(pageRequest.getPage(), pageRequest.getSize()), 0);
        }

        // 角色可分配的安全组取角色的父级角色 创建的安全组和分配的安全组
        dto.setCurrentRoleId(role.getParentRoleId());
        // 根据角色层级限制安全组层级
        dto.setSecGrpLevel(role.getLevel());
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpMapper.selectRoleAssignableSecGrp(roleId, dto));
    }

    @Override
    public List<SecGrp> listRoleAssignableSecGrp(Long roleId, List<Long> secGrpIds) {
        Role role = roleRepository.selectByPrimaryKey(roleId);
        Assert.notNull(role, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

        if (Role.ROOT_ID.equals(role.getParentRoleId())) {
            throw new CommonException("hiam.warn.secGrp.superRoleCantAssignRole");
        }

        SecGrpQueryDTO dto = new SecGrpQueryDTO();
        // 角色可分配的安全组取角色的父级角色 创建的安全组和分配的安全组
        dto.setCurrentRoleId(role.getParentRoleId());
        dto.setSecGrpLevel(role.getLevel());
        dto.setExcludeAssigned(false);
        dto.setSecGrpIds(secGrpIds);
        return secGrpMapper.selectRoleAssignableSecGrp(roleId, dto);
    }

    @Override
    public List<SecGrp> listRoleCreatedSecGrp(Long roleId) {
        return selectByCondition(Condition.builder(SecGrp.class)
                .select(
                        SecGrp.FIELD_SEC_GRP_ID,
                        SecGrp.FIELD_SEC_GRP_CODE,
                        SecGrp.FIELD_SEC_GRP_NAME,
                        SecGrp.FIELD_TENANT_ID,
                        SecGrp.FIELD_ROLE_ID
                ).where(
                        Sqls.custom()
                                .andEqualTo(SecGrp.FIELD_ROLE_ID, roleId)
                )
                .build()
        );
    }

    @Override
    public Map<Long, List<SecGrp>> queryRoleCreatedSecGrp(Set<Long> roleIds) {
        // 安全组
        List<SecGrp> secGrps = null;
        if (CollectionUtils.isNotEmpty(roleIds)) {
            // 查询角色创建的安全组
            secGrps = this.selectByCondition(Condition.builder(SecGrp.class)
                    .select(
                            SecGrp.FIELD_SEC_GRP_ID,
                            SecGrp.FIELD_SEC_GRP_CODE,
                            SecGrp.FIELD_SEC_GRP_NAME,
                            SecGrp.FIELD_TENANT_ID,
                            SecGrp.FIELD_ROLE_ID
                    ).where(
                            Sqls.custom().andIn(SecGrp.FIELD_ROLE_ID, roleIds)
                    ).build()
            );
        }

        // 处理查询结果
        if (CollectionUtils.isNotEmpty(secGrps)) {
            // 按照角色Id进行分组
            return secGrps.stream().collect(groupingBy(SecGrp::getRoleId));
        } else {
            // 返回空map
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<Long, List<SecGrp>> queryRoleCreatedSecGrp(List<Role> roles) {
        if (CollectionUtils.isNotEmpty(roles)) {
            // 处理数据，查询并返回结果
            return this.queryRoleCreatedSecGrp(roles.stream().map(Role::getId).collect(Collectors.toSet()));
        } else {
            // 返回空Map
            return Collections.emptyMap();
        }
    }

    @Override
    public Page<RoleVO> listSecGrpAssignableRole(Long secGrpId, RoleSecGrpDTO queryDTO, PageRequest pageRequest) {
        // 查询安全组
        SecGrp secGrp = this.querySecGrp(secGrpId);

        // 查询数据并返回结果
        return this.roleRepository.selectSecGrpAssignableRole(secGrpId, secGrp.getRoleId(), queryDTO, pageRequest);
    }

    @Override
    public Page<RoleSecGrpDTO> listSecGrpAssignedRole(Long secGrpId, RoleSecGrpDTO queryDTO, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> secGrpMapper.selectSecGrpAssignedRole(secGrpId, queryDTO));
    }

    @Override
    public List<Role> listSecGrpAssignedRole(Long secGrpId) {
        List<RoleSecGrpDTO> dtos = secGrpMapper.selectSecGrpAssignedRole(secGrpId, new RoleSecGrpDTO());
        return dtos.stream().map(dto -> {
            Role role = new Role();
            role.setId(dto.getId());
            role.setCode(dto.getCode());
            role.setName(dto.getName());
            role.setTenantId(dto.getTenantId());
            role.setParentRoleId(dto.getParentRoleId());
            return role;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Role> selectDirectAssignedRolesOfSecGrp(Long secGrpId) {
        return roleMapper.selectDirectAssignedRolesOfSecGrp(secGrpId);
    }

    @Override
    public List<SecGrp> selectBuildBySelfGrpInRoleTree(Long revokeSgChildRoleId) {
        return secGrpMapper.selectBuildBySelfGrpInRoleTree(revokeSgChildRoleId);
    }

    @Override
    public List<SecGrp> selectRoleAuthorizedSecGrp(List<Long> secGrpIds, Long roleId) {
        SecGrpQueryDTO queryDTO = new SecGrpQueryDTO();
        queryDTO.setRoleId(roleId);
        queryDTO.setupCurrentRole();
        queryDTO.selectEnabled();
        return secGrpMapper.selectAuthorizedSecGrp(secGrpIds, queryDTO);
    }

    @Override
    public List<Long> listRoleIdAssignedSecGrp(Long secGrpId) {
        if (secGrpId == null) {
            return new ArrayList<>();
        }
        return secGrpMapper.listRoleIdAssignedSecGrp(secGrpId);
    }

    @Override
    public List<SecGrpAssign> listSecGrpAssign(Long secGrpId) {
        if (secGrpId == null) {
            return new ArrayList<>();
        }
        return secGrpMapper.listSecGrpAssign(secGrpId);
    }

    @Override
    @ProcessLovValue
    public Page<SecGrp> listUserAssignedSecGrp(Long userId, SecGrpQueryDTO queryDTO, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> secGrpMapper.selectUserAssignedSecGrp(userId, queryDTO));
    }

    @Override
    public List<SecGrp> listUserAssignableSecGrp(Long userId, List<Long> secGrpIds) {
        SecGrpQueryDTO queryDTO = new SecGrpQueryDTO();
        CustomUserDetails self = UserUtils.getUserDetails();
        queryDTO.setCurrentRoleId(self.getRoleId());
        queryDTO.setSecGrpIds(secGrpIds);
        queryDTO.setExcludeAssigned(false);
        return secGrpMapper.listUserAssignableSecGrp(userId, queryDTO);
    }

    @Override
    @ProcessLovValue
    public Page<SecGrp> listUserAssignableSecGrp(Long userId, SecGrpQueryDTO queryDTO, PageRequest pageRequest) {
        CustomUserDetails self = UserUtils.getUserDetails();
        queryDTO.setCurrentRoleId(self.getRoleId());
        return PageHelper.doPage(pageRequest, () -> secGrpMapper.listUserAssignableSecGrp(userId, queryDTO));
    }

}
