package org.hzero.scheduler.app.service;

import org.hzero.scheduler.domain.entity.Executor;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
public interface ExecutorService {

    /**
     * 分页查询
     *
     * @param pageRequest  分页
     * @param executorCode 编码
     * @param executorName 名称
     * @param executorType 类型
     * @param status       状态
     * @param tenantId     租户Id
     * @return 分页数据
     */
    Page<Executor> pageExecutor(PageRequest pageRequest, String executorCode, String executorName, Integer executorType, String status, Long tenantId);

    /**
     * 创建执行器
     *
     * @param executor 执行器
     * @return 新建的执行器
     */
    Executor createExecutor(Executor executor);

    /**
     * 更新执行器
     *
     * @param executor 执行器
     * @return 更新的执行器
     */
    Executor updateExecutor(Executor executor);

    /**
     * 删除执行器
     *
     * @param executorId 主键
     */
    void deleteExecutor(Long executorId);

    /**
     * 刷新执行器
     *
     * @param executorCode 执行器编码
     * @param serverName   服务名称
     * @return 刷新结果
     */
    String refreshExecutor(String executorCode, String serverName);
}
