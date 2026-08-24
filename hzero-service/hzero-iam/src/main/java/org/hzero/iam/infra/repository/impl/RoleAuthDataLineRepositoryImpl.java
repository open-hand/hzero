package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.hzero.iam.domain.entity.RoleAuthDataLine;
import org.hzero.iam.domain.repository.RoleAuthDataLineRepository;
import org.hzero.iam.infra.mapper.RoleAuthDataLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 角色单据权限管理行 资源库实现
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@Component
public class RoleAuthDataLineRepositoryImpl extends BaseRepositoryImpl<RoleAuthDataLine> implements RoleAuthDataLineRepository {
    private RoleAuthDataLineMapper roleAuthDataLineMapper;

    @Autowired
    public RoleAuthDataLineRepositoryImpl(RoleAuthDataLineMapper roleAuthDataLineMapper) {
        this.roleAuthDataLineMapper = roleAuthDataLineMapper;
    }

    @Override
    public List<RoleAuthDataLine> pageRoleAuthDataLine(Long authDataId, Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                roleAuthDataLineMapper.listRoleAuthDataLine(authDataId, tenantId, dataCode, dataName)
        );
    }

    @Override
    public Page<RoleAuthDataLine> pagePurOrg(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> roleAuthDataLineMapper.listRoleAuthDataLinePurOrg(tenantId, roleId, dataCode, dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pagePurAgent(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> roleAuthDataLineMapper.listRoleAuthDataLinePurAgent(tenantId, roleId, dataCode, dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageLov(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> roleAuthDataLineMapper.listRoleAuthDataLineLov(tenantId, roleId, dataCode, dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageLovView(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> roleAuthDataLineMapper.listRoleAuthDataLineLovView(tenantId, roleId, dataCode, dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageDatasource(Long tenantId, Long roleId, String dataCode, String dataName,
            PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> roleAuthDataLineMapper.listRoleAuthDataLineDatasource(tenantId, roleId, dataCode, dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageDataGroup(Long tenantId, Long roleId, String groupCode, String groupName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthGroupData(tenantId,roleId,groupCode,groupName));
    }

    @Override
    public Page<RoleAuthDataLine> pagePurOrgAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthDataLinePurOrgAll(tenantId,dataCode,dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pagePurAgentAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthDataLinePurAgentAll(tenantId,dataCode,dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageLovAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthDataLineLovAll(tenantId,dataCode,dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageLovViewAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthDataLineLovViewAll(tenantId,dataCode,dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageDatasourceAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthDataLineDatasourceAll(tenantId,dataCode,dataName));
    }

    @Override
    public Page<RoleAuthDataLine> pageDataGroupAll(Long tenantId, String groupCode, String groupName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->roleAuthDataLineMapper.listRoleAuthGroupDataAll(tenantId,groupCode,groupName));
    }

    @Override
    public List<RoleAuthDataLine> selectCompliantRoleAuthDatas(Long organizationId, Long roleId, Long copyRoleId,
            String compDocType) {
        return roleAuthDataLineMapper.selectCompliantRoleAuthDatas(organizationId, roleId, copyRoleId, compDocType);
    }

    @Override
    public List<Long> selectCompanyAssignOu(Long roleId, Long tenantId, Long companyId) {
        return roleAuthDataLineMapper.selectCompanyAssignOu(roleId, tenantId, companyId);
    }

    @Override
    public List<Long> selectOuAssignInvOrg(Long roleId, Long tenantId, List<Long> ouIds) {
        return roleAuthDataLineMapper.selectOuAssignInvOrg(roleId, tenantId, ouIds);
    }
}
