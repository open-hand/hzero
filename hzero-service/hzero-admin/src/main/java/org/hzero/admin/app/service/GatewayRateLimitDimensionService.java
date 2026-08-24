package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;

/**
 * @author XCXCXCXCX
 * @version 1.0
 * @date 2019/11/20 9:30 上午
 */
public interface GatewayRateLimitDimensionService {

    /**
     * 校验并新增维度配置
     * @param gatewayRateLimitDimension
     * @return
     */
    GatewayRateLimitDimension insertSelective(GatewayRateLimitDimension gatewayRateLimitDimension);

    Page<GatewayRateLimitDimension> pageByCondition(Long rateLimitLineId, PageRequest pageRequest);

    GatewayRateLimitDimension selectByPrimaryKey(Long rateLimitDimensionId);

    GatewayRateLimitDimension updateByPrimaryKeySelective(GatewayRateLimitDimension gatewayRateLimitDimension);

    int deleteByCondition(GatewayRateLimitDimension gatewayRateLimitDimension);
}
