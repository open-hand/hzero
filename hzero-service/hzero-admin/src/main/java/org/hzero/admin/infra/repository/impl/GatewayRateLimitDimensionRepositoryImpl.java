package org.hzero.admin.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.admin.domain.repository.GatewayRateLimitDimensionRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.0
 * @date 2019/11/18 4:01 下午
 */
@Repository
public class GatewayRateLimitDimensionRepositoryImpl extends BaseRepositoryImpl<GatewayRateLimitDimension> implements GatewayRateLimitDimensionRepository {

    @Override
    public Page<GatewayRateLimitDimension> pageByCondition(Long rateLimitLineId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> {
            List<GatewayRateLimitDimension> list = select(GatewayRateLimitDimension.FIElD_RATE_LIMIT_LINE_ID, rateLimitLineId);
            list.forEach(GatewayRateLimitDimension::translate);
            return list;
        });
    }

    @Override
    public GatewayRateLimitDimension updateOneByPrimaryKey(GatewayRateLimitDimension gatewayRateLimitDimension) {
        GatewayRateLimitDimension dimension = selectByPrimaryKey(gatewayRateLimitDimension.getRateLimitDimId());
        dimension.setReplenishRate(gatewayRateLimitDimension.getReplenishRate());
        dimension.setBurstCapacity(gatewayRateLimitDimension.getBurstCapacity());
        dimension.setDimensionKey(gatewayRateLimitDimension.getDimensionKey());
        updateByPrimaryKey(dimension);
        return dimension;
    }

    @Override
    public int deleteByCondition(GatewayRateLimitDimension gatewayRateLimitDimension) {
        if (gatewayRateLimitDimension == null){
            return 0;
        }

        if (gatewayRateLimitDimension.getRateLimitDimId() != null){
            return deleteByPrimaryKey(gatewayRateLimitDimension);
        }
        if (gatewayRateLimitDimension.getRateLimitLineId() != null){
            GatewayRateLimitDimension deleteCondition = new GatewayRateLimitDimension();
            deleteCondition.setRateLimitLineId(gatewayRateLimitDimension.getRateLimitLineId());
            return delete(deleteCondition);
        }
        return 0;
    }
}
