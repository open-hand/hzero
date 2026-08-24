package org.hzero.scheduler.domain.service;

import java.util.Set;

/**
 * 服务uri获取
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/08 16:32
 */
public interface IUrlService {

    /**
     * 获取客户端ip和端口号
     *
     * @param executorStrategy 执行器策略
     * @param executorId       执行器Id
     * @param jobId            任务Id
     * @param errorUrls        排除的url
     * @return url
     */
    String getUrl(String executorStrategy, Long executorId, Long jobId, Set<String> errorUrls);

    /**
     * 清理轮询相关缓存
     *
     * @param executorId 执行器Id
     * @param jobId      任务Id
     */
    void clearPolling(Long executorId, Long jobId);
}
