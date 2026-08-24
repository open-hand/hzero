package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.iam.domain.entity.SecGrpAclDashboard;
import org.hzero.iam.domain.repository.SecGrpAclDashboardRepository;
import org.hzero.iam.infra.mapper.SecGrpAclDashboardMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 安全组工作台配置 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@Component
public class SecGrpAclDashboardRepositoryImpl extends BaseRepositoryImpl<SecGrpAclDashboard> implements SecGrpAclDashboardRepository {

    @Autowired
    private SecGrpAclDashboardMapper secGrpAclDashboardMapper;

    @Override
	@ProcessLovValue
    public Page<SecGrpAclDashboard> listSecGrpDashboard(Long tenantId, Long secGrpId, SecGrpAclDashboard queryDTO, PageRequest pageRequest) {
        queryDTO.setTenantId(tenantId);
        queryDTO.setSecGrpId(secGrpId);
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpAclDashboardMapper.selectSecGrpDashboard(queryDTO));
    }

    @Override
    @ProcessLovValue
    public Page<SecGrpAclDashboard> listSecGrpAssignableDashboard(@NotNull Long secGrpId, SecGrpAclDashboard queryDTO, PageRequest pageRequest) {
        queryDTO.setSecGrpId(secGrpId);
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpAclDashboardMapper.selectSecGrpAssignableDashboard(queryDTO));
    }

    @Override
    public List<SecGrpAclDashboard> listDashboardBySecGrpIds(List<Long> secGrpIds) {
        return secGrpAclDashboardMapper.selectSecGrpAclDashboardByGrpIds(secGrpIds);
    }
}
