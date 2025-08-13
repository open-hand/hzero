package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.TenantCustomPoint;
import org.hzero.iam.domain.repository.TenantCustomPointRelRepository;
import org.hzero.iam.infra.mapper.TenantCustomPointMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 租户客户化端点关系 资源库实现
 *
 * @author bojiangzhou 2019-12-12 11:40:55
 */
@Component
public class TenantCustomPointRelRepositoryImpl extends BaseRepositoryImpl<TenantCustomPoint> implements TenantCustomPointRelRepository {

    @Autowired
    private TenantCustomPointMapper tenantCustomPointMapper;

    @Override
    public List<TenantCustomPoint> selectByPointCode(String customPointCode) {
        return tenantCustomPointMapper.selectByPointCode(customPointCode);
    }
}
