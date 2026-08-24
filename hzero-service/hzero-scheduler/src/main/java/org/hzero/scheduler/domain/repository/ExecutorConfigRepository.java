package org.hzero.scheduler.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.domain.entity.ExecutorConfig;

/**
 * 执行器配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-03-19 20:38:59
 */
public interface ExecutorConfigRepository extends BaseRepository<ExecutorConfig> {

    /**
     * 查询配置
     *
     * @param executorId 执行器Id
     * @param address    地址
     * @return 配置
     */
    ExecutorConfig selectByUnique(Long executorId, String address);
}
