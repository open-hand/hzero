package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;

import java.util.List;


/**
 * 网关限流设置行明细Mapper
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public interface GatewayRateLimitLineMapper extends BaseMapper<GatewayRateLimitLine> {
    /**
     * 模糊查询
     *
     * @param gatewayRateLimitLine 限制条件
     * @return
     */
    List<GatewayRateLimitLine> pageByCondition(@Param("gatewayRateLimitLine") GatewayRateLimitLine gatewayRateLimitLine);
}
