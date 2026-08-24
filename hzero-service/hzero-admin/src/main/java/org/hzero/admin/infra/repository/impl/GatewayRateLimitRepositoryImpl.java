package org.hzero.admin.infra.repository.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimit;
import org.hzero.admin.domain.repository.GatewayRateLimitRepository;
import org.hzero.admin.infra.mapper.GatewayRateLimitMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 网关限流设置 资源库实现
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Component
public class GatewayRateLimitRepositoryImpl extends BaseRepositoryImpl<GatewayRateLimit> implements GatewayRateLimitRepository {

    @Autowired
    private GatewayRateLimitMapper gatewayRateLimitMapper;

    @Override
    public Page<GatewayRateLimit> pageByCondition(GatewayRateLimit gatewayRateLimit, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> gatewayRateLimitMapper.pageByCondition(gatewayRateLimit));
    }
}
