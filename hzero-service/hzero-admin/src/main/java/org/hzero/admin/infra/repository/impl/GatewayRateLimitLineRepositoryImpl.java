package org.hzero.admin.infra.repository.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
import org.hzero.admin.domain.repository.GatewayRateLimitLineRepository;
import org.hzero.admin.infra.mapper.GatewayRateLimitDimensionMapper;
import org.hzero.admin.infra.mapper.GatewayRateLimitLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 网关限流设置行明细 资源库实现
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Component
public class GatewayRateLimitLineRepositoryImpl extends BaseRepositoryImpl<GatewayRateLimitLine> implements GatewayRateLimitLineRepository {

    @Autowired
    private GatewayRateLimitLineMapper gatewayRateLimitLineMapper;
    @Autowired
    private GatewayRateLimitDimensionMapper gatewayRateLimitDimensionMapper;

    @Override
    public Page<GatewayRateLimitLine> pageByCondition(PageRequest pageRequest, GatewayRateLimitLine gatewayRateLimitLine) {
        return PageHelper.doPageAndSort(pageRequest, () -> gatewayRateLimitLineMapper.pageByCondition(gatewayRateLimitLine));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int batchDelete(List<GatewayRateLimitLine> list) {
        int effect = super.batchDelete(list);
        for (GatewayRateLimitLine line : list){
            GatewayRateLimitDimension deleteCondition = new GatewayRateLimitDimension();
            deleteCondition.setRateLimitLineId(line.getRateLimitLineId());
            gatewayRateLimitDimensionMapper.delete(deleteCondition);
        }
        return effect;
    }
}
