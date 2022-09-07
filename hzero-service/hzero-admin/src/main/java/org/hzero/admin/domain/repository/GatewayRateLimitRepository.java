package org.hzero.admin.domain.repository;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimit;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 网关限流设置资源库
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public interface GatewayRateLimitRepository extends BaseRepository<GatewayRateLimit> {
    /**
     * 模糊查询
     *
     * @param gatewayRateLimit
     * @param pageRequest
     * @return
     */
    Page<GatewayRateLimit> pageByCondition(GatewayRateLimit gatewayRateLimit, PageRequest pageRequest);
}
