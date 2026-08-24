package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.repository.RoleAuthDataRepository;
import org.hzero.iam.infra.mapper.RoleAuthDataMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色单据权限管理 资源库实现
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@Component
public class RoleAuthDataRepositoryImpl extends BaseRepositoryImpl<RoleAuthData> implements RoleAuthDataRepository {
    private RoleAuthDataMapper roleAuthDataMapper;

    @Autowired
    public RoleAuthDataRepositoryImpl(RoleAuthDataMapper roleAuthDataMapper) {
        this.roleAuthDataMapper = roleAuthDataMapper;
    }

    @Override
    public List<CompanyOuInvorgNodeDTO> listCompanyUoInvorg(Long tenantId, Long roleId, String dataCode, String dataName) {
        return roleAuthDataMapper.listCompanyUoInvorg(tenantId, roleId, dataCode, dataName);
    }

    @Override
    public Page<RoleAuthDataDTO> pageRoleAuthDataAssignedRole(Long dataId, RoleAuthData roleAuthData, PageRequest pageRequest) {
        Page<RoleAuthDataDTO> result = new Page<>();
        Page<RoleAuthData> page = PageHelper.doPageAndSort(pageRequest,()->roleAuthDataMapper.listAuthDataAssignedRole(dataId,roleAuthData.getAuthorityTypeCode(),roleAuthData));
        if(CollectionUtils.isNotEmpty(page)){
            BeanUtils.copyProperties(page,result);
            result.setContent(page.stream()
                    .map(item-> {
                        RoleAuthDataDTO roleAuthDataDTO = new RoleAuthDataDTO();
                        roleAuthDataDTO.setRoleAuthData(item);
                        return roleAuthDataDTO;
                    }).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public Long selectRoleAuthDataId(Long tenantId, Long roleId, Long targetRoleId, String authorityTypeCode) {
        RoleAuthData dbRoleAuthData = findRoleAuthDataByCondition(tenantId, targetRoleId, authorityTypeCode);
        // 先查询源角色表中的数据，判断includeAllFlag是否为1，如果是则需复制到当前角色上
        RoleAuthData originRoleAuthData = findRoleAuthDataByCondition(tenantId, roleId, authorityTypeCode);
        if (Objects.isNull(originRoleAuthData)) {
            return null;
        }
        boolean isIncludeAllFlag = Objects.equals(originRoleAuthData.getIncludeAllFlag(), BaseConstants.Digital.ONE);
        if (Objects.nonNull(dbRoleAuthData)) {
            // 查询条件查询的数据是唯一的，直接获取第一条数据返回即可
            if (isIncludeAllFlag) {
                dbRoleAuthData.setIncludeAllFlag(BaseConstants.Digital.ONE);
                // 将includeAllFlag复制到当前权限维度头上
                updateByPrimaryKeySelective(dbRoleAuthData);
            }
            return dbRoleAuthData.getAuthDataId();
        } else {
            RoleAuthData roleAuthData = new RoleAuthData();
            // 先判断源角色表中的数据，判断includeAllFlag是否为1，若是1则需复制到当前角色上
            if (isIncludeAllFlag) {
                roleAuthData.setIncludeAllFlag(BaseConstants.Digital.ONE);
            }
            // 目标角色在hiam_role_auth_data表中不存在，新增一条数据并返回
            roleAuthData.setTenantId(tenantId);
            roleAuthData.setAuthorityTypeCode(authorityTypeCode);
            roleAuthData.setRoleId(targetRoleId);
            insertSelective(roleAuthData);
            return roleAuthData.getAuthDataId();
        }
    }

    @Override
    public RoleAuthData select(Long tenantId, Long roleId, String authorityTypeCode) {
       return roleAuthDataMapper.selectByUniqueKey(tenantId,roleId,authorityTypeCode);
    }

    /**
     * 查询RoleAuthData数据
     */
    private RoleAuthData findRoleAuthDataByCondition(Long tenantId, Long roleId, String authorityTypeCode) {
        RoleAuthData roleAuthData = new RoleAuthData(roleId, tenantId, authorityTypeCode);
        return selectOne(roleAuthData);
    }
}
