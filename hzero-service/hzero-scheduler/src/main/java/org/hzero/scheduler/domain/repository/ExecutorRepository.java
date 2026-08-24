package org.hzero.scheduler.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.domain.entity.Executor;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
public interface ExecutorRepository extends BaseRepository<Executor> {

    /**
     * 分页查询
     *
     * @param executorCode 执行器编码
     * @param executorName 执行器名称
     * @param executorType 执行器类型
     * @param status       状态
     * @param tenantId     租户Id
     * @param pageRequest  分页
     * @return 分页结果
     */
    Page<Executor> pageExecutor(String executorCode, String executorName, Integer executorType, String status, Long tenantId, PageRequest pageRequest);

    /**
     * 清理下线执行地址的最大并发控制缓存
     *
     * @param executor 执行器
     */
    void clearOffLineAddress(Executor executor);

    /**
     * 清理执行器所有地址的最大并发控制缓存
     *
     * @param executorId 执行器
     */
    void clearOffLineAddress(Long executorId);
}
