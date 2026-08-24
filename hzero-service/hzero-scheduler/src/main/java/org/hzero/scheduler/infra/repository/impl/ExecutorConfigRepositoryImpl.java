package org.hzero.scheduler.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.domain.entity.ExecutorConfig;
import org.hzero.scheduler.domain.repository.ExecutorConfigRepository;
import org.springframework.stereotype.Component;

/**
 * 执行器配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-03-19 20:38:59
 */
@Component
public class ExecutorConfigRepositoryImpl extends BaseRepositoryImpl<ExecutorConfig> implements ExecutorConfigRepository {


    @Override
    public ExecutorConfig selectByUnique(Long executorId, String address) {
        return selectOne(new ExecutorConfig().setExecutorId(executorId).setAddress(address));
    }
}
