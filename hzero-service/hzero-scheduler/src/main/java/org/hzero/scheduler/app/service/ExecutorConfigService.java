package org.hzero.scheduler.app.service;

import org.hzero.scheduler.domain.entity.ExecutorConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 执行器配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-03-19 20:38:59
 */
public interface ExecutorConfigService {

    /**
     * 执行器配置列表
     *
     * @param executorId 执行器Id
     * @return 配置列表
     */
    List<ExecutorConfig> listConfig(Long executorId);

    /**
     * 校验执行器的最大并发量
     *
     * @param executorId 执行器Id
     */
    void checkConfig(Long executorId);

    /**
     * 新建或更新执行器
     *
     * @param executorConfigs 执行器配置
     * @return 执行器配置
     */
    List<ExecutorConfig> insertOrUpdateConfig(List<ExecutorConfig> executorConfigs, Long tenantId);

    /**
     * 批量删除
     *
     * @param executorConfigs 执行器配置
     */
    void batchDelete(List<ExecutorConfig> executorConfigs);

    /**
     * 任务-执行器权重配置
     *
     * @param tenantId   租户
     * @param jobId      任务Id
     * @param executorId 执行器Id
     * @return 权重信息
     * @throws IOException exception
     */
    Map<String, Integer> jobExecutorConfig(Long tenantId, Long jobId, Long executorId) throws IOException;

    /**
     * 任务-执行器权重配置
     *
     * @param tenantId     租户
     * @param executableId 可执行Id
     * @param executorId   执行器Id
     * @return 权重信息
     * @throws IOException exception
     */
    Map<String, Integer> executableExecutor(Long tenantId, Long executableId, Long executorId) throws IOException;
}
