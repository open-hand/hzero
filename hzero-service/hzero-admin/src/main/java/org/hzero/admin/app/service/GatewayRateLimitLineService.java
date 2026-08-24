package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.api.dto.GatewayRateLimitLineDto;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;

import java.util.List;

/**
 * 网关限流设置行明细应用服务
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
public interface GatewayRateLimitLineService {
    /**
     * 查询分页
     * @param line 限制条件
     * @param pageRequest 分页
     * @return 返回满足条件的结果
     */
    Page<GatewayRateLimitLine> pageByCondition(GatewayRateLimitLineDto line, PageRequest pageRequest);

    /**
     * 批量插入或者更新
     * @param gatewayRateLimitLineList 带操作的数据
     * @return 返回插入的数据
     */
    List<GatewayRateLimitLine> batchInsertOrUpdate(List<GatewayRateLimitLine> gatewayRateLimitLineList);

    /**
     * 查看行明细
     * @param rateLimitLineId
     * @return
     */
    GatewayRateLimitLine detail(Long rateLimitLineId);

    /**
     * 批量删除，并禁用配置
     * @param gatewayRateLimitLines
     * @return
     */
    int batchDelete(List<GatewayRateLimitLine> gatewayRateLimitLines);

    /**
     * 该限流配置是否允许更新（限流维度字段）或删除
     * @param rateLimitLineId
     * @return
     */
    boolean allowChange(Long rateLimitLineId);

    /**
     * 该限流配置是否允许更新（限流维度字段）或删除
     * @param rateLimitLine
     * @return
     */
    boolean allowChange(GatewayRateLimitLine rateLimitLine);

    /**
     * 该限流配置是否允许更新（限流维度字段）或删除
     * @param rateLimitLines
     * @return
     */
    boolean allowChange(List<GatewayRateLimitLine> rateLimitLines);

    /**
     * 根据ID更新限流配置
     * @param gatewayRateLimitLine
     * @return
     */
    GatewayRateLimitLine updateByPrimaryKey(GatewayRateLimitLine gatewayRateLimitLine);

    /**
     * 根据ID批量更新限流配置
     * @param gatewayRateLimitLines
     * @return
     */
    List<GatewayRateLimitLine> batchUpdateByPrimaryKey(List<GatewayRateLimitLine> gatewayRateLimitLines);

    /**
     * 创建限流行
     * @param gatewayRateLimitLine
     */
    GatewayRateLimitLine create(GatewayRateLimitLine gatewayRateLimitLine);

    GatewayRateLimitLine insertSelective(GatewayRateLimitLine gatewayRateLimitLine);
}
