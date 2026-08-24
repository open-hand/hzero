package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author XCXCXCXCX
 * @version 1.0
 * @date 2019/11/18 3:59 下午
 */
public interface GatewayRateLimitDimensionRepository extends BaseRepository<GatewayRateLimitDimension> {

    /**
     * 分页查询
     *
     * @param rateLimitLineId 限流配置ID
     * @param pageRequest   分页
     * @return 返回结果集
     */
    Page<GatewayRateLimitDimension> pageByCondition(Long rateLimitLineId, PageRequest pageRequest);

    /**
     * 根据主键更新
     *
     * @param gatewayRateLimitDimension
     * @return
     */
    GatewayRateLimitDimension updateOneByPrimaryKey(GatewayRateLimitDimension gatewayRateLimitDimension);


    /**
     * 根据条件删除，主键或索引字段
     *
     * @param gatewayRateLimitDimension
     * @return
     */
    int deleteByCondition(GatewayRateLimitDimension gatewayRateLimitDimension);

}
