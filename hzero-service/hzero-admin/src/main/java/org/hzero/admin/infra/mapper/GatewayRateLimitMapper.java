package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.admin.domain.entity.GatewayRateLimit;

import java.util.List;

/**
 * gateway限流设置Mapper
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public interface GatewayRateLimitMapper extends BaseMapper<GatewayRateLimit> {
    /**
     * 模糊查询
     *
     * @param gatewayRateLimit
     * @return
     */
    List<GatewayRateLimit> pageByCondition(@Param("gatewayRateLimit") GatewayRateLimit gatewayRateLimit);
}
