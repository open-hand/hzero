package org.hzero.admin.domain.repository;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 网关限流设置行明细资源库
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public interface GatewayRateLimitLineRepository extends BaseRepository<GatewayRateLimitLine> {
    /**
     * 模糊查询
     *
     * @param pageRequest       分页条件
     * @param gatewayRateLimitLine 限制条件
     * @return 返回结果
     */
    Page<GatewayRateLimitLine> pageByCondition(PageRequest pageRequest, GatewayRateLimitLine gatewayRateLimitLine);

    /**
     * 批量删除限流配置项及其关联的维度配置
     * @param list
     * @return
     */
    @Override
    int batchDelete(List<GatewayRateLimitLine> list);

}
