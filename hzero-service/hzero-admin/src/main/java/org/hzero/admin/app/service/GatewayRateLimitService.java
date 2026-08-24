package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.api.dto.GatewayRateLimitDto;
import org.hzero.admin.domain.entity.GatewayRateLimit;

import java.util.List;


/**
 * 网关限流设置应用服务
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public interface GatewayRateLimitService {

    String RATE_LIMITER_FILTER_NAME = "RequestRateLimiter";

    /**
     * 查询明细
     *
     * @param limitId     限流Id
     * @param pageRequest 分页
     * @return 返回满足条件的结果集
     */
    GatewayRateLimitDto queryDetail(Long limitId, PageRequest pageRequest);

    /**
     * 保存明细
     *
     * @param gatewayRateLimit 限制条件
     * @return 返回结果集
     */
    GatewayRateLimitDto saveDetail(GatewayRateLimitDto gatewayRateLimit);


    /**
     * 刷新配置
     *
     * @param rateLimitList 限制条件
     * @return
     */
    void refresh(List<GatewayRateLimit> rateLimitList);

    /**
     * 分页查询
     *
     * @param gatewayRateLimit 限制条件
     * @param pageRequest   分页
     * @return 返回结果集
     */
    Page<GatewayRateLimit> pageByCondition(GatewayRateLimit gatewayRateLimit, PageRequest pageRequest);

    /**
     * 批量更新或者创建限流规则
     *
     * @param gatewayRateLimitList 待更新的数据
     * @return 返回更新后的数据
     */
    List<GatewayRateLimit> batchInsertOrUpdate(List<GatewayRateLimit> gatewayRateLimitList);

    GatewayRateLimit selectByPrimaryKey(Long rateLimitId);

    GatewayRateLimit insertSelective(GatewayRateLimit gatewayRateLimit);

    GatewayRateLimit updateByPrimaryKeySelective(GatewayRateLimit gatewayRateLimit);
}
