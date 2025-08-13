package org.hzero.iam.infra.repository.impl;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.service.RootUserService;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author allen 2018/7/6
 */
@Repository
public class TenantRepositoryImpl extends BaseRepositoryImpl<Tenant> implements TenantRepository {

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public List<TenantDTO> selectSelfTenants(TenantDTO params) {
        CustomUserDetails self = UserUtils.getUserDetails();
        params.setUserId(self.getUserId());

        if (RootUserService.isRootUser()) {
            return tenantMapper.selectRootTenant(params);
        } else {
            return tenantMapper.selectUserTenant(params);
        }
    }

    @Override
    public Page<TenantDTO> selectSelfTenants(TenantDTO params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> selectSelfTenants(params));
    }

    @Override
    public Page<Tenant> pagingTenantsList(Tenant tenant, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> tenantMapper.selectTenantsList(tenant));
    }

    @Override
    public Tenant selectTenantDetails(long tenantId) {
        return tenantMapper.selectTenantDetails(tenantId);
    }

    @Nonnull
    @Override
    public Page<Tenant> pagingHavingCustomMenuTenants(Tenant tenant, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> tenantMapper.selectHavingCustomMenuTenant(tenant));
    }

    @Override
    public Tenant selectTenantByUserId(Long userId) {
        return this.tenantMapper.selectTenantByUserId(userId);
    }

    @Override
    public int assignTenantToUser(Long tenantId, Long userId) {
        return this.tenantMapper.assignTenantToUser(tenantId, userId);
    }

    @Override
    public int checkRepeatCount(Tenant tenant) {
        if (tenant == null || (StringUtils.isEmpty(tenant.getTenantName()) && StringUtils.isEmpty(tenant.getTenantNum()))) {
            return 0;
        }
        return this.tenantMapper.checkRepeatCount(tenant);
    }

    @Override
    public int countByTenantNum(@Nonnull String tenantNum) {
        return selectCountByCondition(
                Condition.builder(Tenant.class)
                        .where(Sqls.custom().andEqualTo(Tenant.TENANT_NUM, tenantNum))
                        .build()
        );
    }
}
