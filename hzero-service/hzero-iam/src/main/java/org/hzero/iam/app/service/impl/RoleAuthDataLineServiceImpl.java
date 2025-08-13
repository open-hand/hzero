package org.hzero.iam.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.app.service.RoleAuthDataLineService;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.RoleAuthDataLine;
import org.hzero.iam.domain.repository.RoleAuthDataLineRepository;
import org.hzero.iam.domain.service.role.AbstractAuthorityCommonService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 角色单据权限管理行应用服务默认实现
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@Service
public class RoleAuthDataLineServiceImpl extends AbstractAuthorityCommonService implements RoleAuthDataLineService {
    private RoleAuthDataLineRepository roleAuthDataLineRepository;
    private static final Page<RoleAuthDataLine> EMPTY = new Page<>();

    @Autowired
    public RoleAuthDataLineServiceImpl(RoleAuthDataLineRepository roleAuthDataLineRepository) {
        this.roleAuthDataLineRepository = roleAuthDataLineRepository;
    }

    @Override
    public List<RoleAuthDataLine> pageRoleAuthDataLine(RoleAuthData roleAuthData, String dataCode, String dataName, PageRequest pageRequest) {
        if (roleAuthData == null) {
            return EMPTY;
        }
        return roleAuthDataLineRepository.pageRoleAuthDataLine(roleAuthData.getAuthDataId(), roleAuthData.getTenantId(), dataCode, dataName, pageRequest);
    }

    @Override
    public RoleAuthDataDTO createRoleAuthDataLine(RoleAuthDataDTO roleAuthData) {
        if (CollectionUtils.isEmpty(roleAuthData.getRoleAuthDataLineList())) {
            return roleAuthData;
        }
        validRepeat(roleAuthData);
//        roleAuthData.getRoleAuthDataLineList().forEach(roleAuthDataLine ->
//                roleAuthDataLineRepository.insert(roleAuthDataLine.setAuthDataId(roleAuthData.getRoleAuthData().getAuthDataId()))
//        );
        roleAuthData.getRoleAuthDataLineList().forEach(roleAuthDataLine ->
                saveDefalutRoleAuthDataLine(roleAuthDataLine.setAuthDataId(roleAuthData.getRoleAuthData().getAuthDataId())));
        return roleAuthData;
    }

    @Override
    public void deleteRoleAuthDataLine(List<RoleAuthDataLine> roleAuthDataLineList) {
//        roleAuthDataLineRepository.batchDelete(roleAuthDataLineList);
        batchRemoveDefaultRoleAuthDataLine(roleAuthDataLineList);
    }

    @Override
    public Page<RoleAuthDataLine> pagePurOrg(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return roleAuthDataLineRepository.pagePurOrg(tenantId, roleId, dataCode, dataName, pageRequest);
    }

    @Override
    public Page<RoleAuthDataLine> pagePurAgent(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return roleAuthDataLineRepository.pagePurAgent(tenantId, roleId, dataCode, dataName, pageRequest);
    }

    @Override
    public Page<RoleAuthDataLine> pageLov(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return roleAuthDataLineRepository.pageLov(tenantId, roleId, dataCode, dataName, pageRequest);
    }

    @Override
    public Page<RoleAuthDataLine> pageLovView(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest) {
        return roleAuthDataLineRepository.pageLovView(tenantId, roleId, dataCode, dataName, pageRequest);
    }

    @Override
    public void batchInsert(List<RoleAuthDataLine> roleAuthDataLineList) {
        roleAuthDataLineRepository.batchInsert(roleAuthDataLineList);
    }

    @Override
    public List<RoleAuthDataLine> listRoleAuthDataLine(Long authDataId, Long tenantId) {
        return roleAuthDataLineRepository.select(new RoleAuthDataLine().setAuthDataId(authDataId).setTenantId(tenantId));
    }

    @Override
    public Page<RoleAuthDataLine> pageDatasource(Long tenantId, Long roleId, String dataCode, String dataName,
            PageRequest pageRequest) {
        return roleAuthDataLineRepository.pageDatasource(tenantId, roleId, dataCode, dataName, pageRequest);
    }

    @Override
    public Page<RoleAuthDataLine> pageDataGroup(Long tenantId, Long roleId, String groupCode, String groupName, PageRequest pageRequest) {
        return roleAuthDataLineRepository.pageDataGroup(tenantId,roleId,groupCode,groupName,pageRequest);
    }

    private void validRepeat(RoleAuthDataDTO roleAuthData) {
        Assert.isTrue(roleAuthDataLineRepository.selectByCondition(Condition.builder(RoleAuthDataLine.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RoleAuthDataLine.FIELD_AUTH_DATA_ID, roleAuthData.getRoleAuthData().getAuthDataId())
                        .andIn(RoleAuthDataLine.FIELD_DATA_ID, roleAuthData.getRoleAuthDataLineList().stream().map(RoleAuthDataLine::getDataId).collect(Collectors.toList())))
                .build()).isEmpty(), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
    }
}
