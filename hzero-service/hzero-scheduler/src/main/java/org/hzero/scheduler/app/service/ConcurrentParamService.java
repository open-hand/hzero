package org.hzero.scheduler.app.service;

import org.hzero.scheduler.domain.entity.ConcurrentParam;

/**
 * 并发程序参数Service
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/11 10:43
 */
public interface ConcurrentParamService {

    /**
     * 新建并发程序参数
     *
     * @param concurrentParam 并发对象
     * @return 新建的对象
     */
    ConcurrentParam createConcurrentParam(ConcurrentParam concurrentParam);

    /**
     * 更新
     *
     * @param concurrentParam 并发对象
     * @return 被更新的对象
     */
    ConcurrentParam updateConcurrentParam(ConcurrentParam concurrentParam);
}
