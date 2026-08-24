package org.hzero.scheduler.infra.repository.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.domain.entity.Executor;
import org.hzero.scheduler.domain.entity.ExecutorConfig;
import org.hzero.scheduler.domain.repository.ExecutorRepository;
import org.hzero.scheduler.domain.service.IAddressService;
import org.hzero.scheduler.infra.mapper.ExecutorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
@Component
public class ExecutorRepositoryImpl extends BaseRepositoryImpl<Executor> implements ExecutorRepository {

    @Autowired
    private ExecutorMapper executorMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private IAddressService addressService;

    @Override
    public Page<Executor> pageExecutor(String executorCode, String executorName, Integer executorType, String status, Long tenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> executorMapper.listExecutors(executorCode, executorName, executorType, status, tenantId));
    }

    @Override
    public void clearOffLineAddress(Executor executor) {
        Long executorId = executor.getExecutorId();
        Executor oldExecutor = selectByPrimaryKey(executorId);
        List<String> oldAddress = addressService.getAddressList(oldExecutor);
        List<String> newAddress = addressService.getAddressList(executor);
        oldAddress.forEach(item -> {
            if (!newAddress.contains(item) && StringUtils.isNotBlank(item)) {
                // 移除下线的执行器的最大并发控制缓存
                ExecutorConfig.clearCache(redisHelper, executorId, item);
            }
        });
    }

    @Override
    public void clearOffLineAddress(Long executorId) {
        Executor oldExecutor = selectByPrimaryKey(executorId);
        List<String> oldAddress = addressService.getAddressList(oldExecutor);
        oldAddress.forEach(item -> {
            // 移除下线的执行器的最大并发控制缓存
            if (StringUtils.isNotBlank(item)) {
                ExecutorConfig.clearCache(redisHelper, executorId, item);
            }
        });
    }
}
