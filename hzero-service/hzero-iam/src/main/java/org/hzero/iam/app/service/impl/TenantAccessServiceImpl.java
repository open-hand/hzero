package org.hzero.iam.app.service.impl;

import org.hzero.iam.app.service.TenantAccessService;
import org.hzero.iam.domain.entity.TenantAccess;
import org.hzero.iam.domain.repository.TenantAccessRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 租户访问审计应用服务默认实现
 *
 * @author qingsheng.chen@hand-china.com 2019-03-06 13:53:40
 */
@Service
public class TenantAccessServiceImpl implements TenantAccessService {
    private TenantAccessRepository tenantAccessRepository;

    public TenantAccessServiceImpl(TenantAccessRepository tenantAccessRepository) {
        this.tenantAccessRepository = tenantAccessRepository;
    }

    @Override
    public void storeUserTenant(long userId, Long tenantId) {
        TenantAccess insert = new TenantAccess().setUserId(userId).setTenantId(tenantId);
        TenantAccess update = tenantAccessRepository.selectOne(insert);
        if (update == null) {
            tenantAccessRepository.insert(insert.setAccessDatetime(new Date())
                    .setAccessCount(1));
        } else {
            tenantAccessRepository.updateByPrimaryKey(update.setAccessDatetime(new Date())
                    .setAccessCount(update.getAccessCount() + 1));
        }
    }
}
