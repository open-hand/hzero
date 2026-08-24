package org.hzero.scheduler.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.app.service.ExecutorConfigService;
import org.hzero.scheduler.domain.entity.Executable;
import org.hzero.scheduler.domain.entity.Executor;
import org.hzero.scheduler.domain.entity.ExecutorConfig;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.hzero.scheduler.domain.repository.ExecutorConfigRepository;
import org.hzero.scheduler.domain.repository.ExecutorRepository;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.domain.service.IAddressService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.choerodon.core.exception.CommonException;

/**
 * 执行器配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-03-19 20:38:59
 */
@Service
public class ExecutorConfigServiceImpl implements ExecutorConfigService {

    @Autowired
    private ExecutorConfigRepository configRepository;
    @Autowired
    private ExecutorRepository executorRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private JobInfoRepository jobInfoRepository;
    @Autowired
    private ExecutableRepository executableRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IAddressService addressService;

    @Override
    public List<ExecutorConfig> listConfig(Long executorId) {
        List<ExecutorConfig> configList = configRepository.select(new ExecutorConfig().setExecutorId(executorId));
        List<String> dbAddressList = configList.stream().map(ExecutorConfig::getAddress).collect(Collectors.toList());
        Executor executor = executorRepository.selectByPrimaryKey(executorId);
        Assert.notNull(executor, HsdrErrorCode.EXECUTOR_NOT_FIND);
        List<String> addressList = addressService.getAddressList(executor);
        addressList.forEach(item -> {
            // config表没有当前地址的详细信息，以初始化状态填充数据
            if (!dbAddressList.contains(item)) {
                ExecutorConfig newConfig = new ExecutorConfig().setAddress(item).setExecutorId(executorId)
                        .setWeight(BaseConstants.Digital.ONE).setEnabledFlag(BaseConstants.Flag.YES);
                configList.add(newConfig);
            }
        });
        return configList;
    }

    @Override
    public void checkConfig(Long executorId) {
        Executor executor = executorRepository.selectByPrimaryKey(executorId);
        Assert.notNull(executor, HsdrErrorCode.EXECUTOR_NOT_FIND);
        StringBuilder result = new StringBuilder();
        List<String> addressList = addressService.getAddressList(executor);
        addressList.forEach(address -> {
            ExecutorConfig config = configRepository.selectByUnique(executorId, address);
            if (config != null && config.getMaxConcurrent() != null) {
                // 校验最大并发数
                int used = ExecutorConfig.getCache(redisHelper, executorId, address).size();
                if (used >= config.getMaxConcurrent()) {
                    result.append(address).append(HsdrConstant.COMMA);
                }
            }
        });
        if (StringUtils.isNotBlank(result)) {
            throw new CommonException(HsdrErrorCode.MAX_CONCURRENT, result.substring(0, result.length() - 1));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExecutorConfig> insertOrUpdateConfig(List<ExecutorConfig> executorConfigs, Long tenantId) {
        executorConfigs.forEach(config -> {
            if (config.getConfigId() == null) {
                config.validate(configRepository);
                // 新建
                config.setTenantId(tenantId);
                configRepository.insertSelective(config);
            } else {
                SecurityTokenHelper.validToken(config);
                // 更新
                configRepository.updateOptional(config,
                        ExecutorConfig.FIELD_MAX_CONCURRENT,
                        ExecutorConfig.FIELD_WEIGHT,
                        ExecutorConfig.FIELD_ENABLED_FLAG);
            }
        });
        return executorConfigs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<ExecutorConfig> executorConfigs) {
        executorConfigs.forEach(config -> {
            if (config.getConfigId() != null) {
                SecurityTokenHelper.validToken(config);
                // 删除
                configRepository.deleteByPrimaryKey(config.getConfigId());
                // 清理缓存
                ExecutorConfig.clearCache(redisHelper, config.getExecutorId(), config.getAddress());
            }
        });
    }

    @Override
    public Map<String, Integer> jobExecutorConfig(Long tenantId, Long jobId, Long executorId) throws IOException {
        List<ExecutorConfig> executorConfigList = listConfig(executorId);
        Map<String, Integer> result;
        if (CollectionUtils.isEmpty(executorConfigList)) {
            result = new HashMap<>(16);
        } else {
            result = executorConfigList.stream().collect(Collectors.toMap(ExecutorConfig::getAddress, ExecutorConfig::getWeight, (k1, k2) -> k2));
        }
        if (jobId == null) {
            return result;
        }
        JobInfo jobInfo = jobInfoRepository.selectOne(new JobInfo().setTenantId(tenantId).setJobId(jobId));
        Assert.notNull(jobInfo, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Map<String, Object> map = objectMapper.readValue(jobInfo.getStrategyParam(), new TypeReference<Map<String, Object>>() {
        });
        if (!map.containsKey(HsdrConstant.StrategyParam.JOB_WEIGHT)) {
            return result;
        }
        Map<String, Integer> jobWeight = (Map<String, Integer>) map.get(HsdrConstant.StrategyParam.JOB_WEIGHT);
        result.putAll(jobWeight);
        return result;
    }

    @Override
    public Map<String, Integer> executableExecutor(Long tenantId, Long executableId, Long executorId) throws IOException {
        List<ExecutorConfig> executorConfigList = listConfig(executorId);
        Map<String, Integer> result;
        if (CollectionUtils.isEmpty(executorConfigList)) {
            result = new HashMap<>(16);
        } else {
            result = executorConfigList.stream().collect(Collectors.toMap(ExecutorConfig::getAddress, ExecutorConfig::getWeight, (k1, k2) -> k2));
        }
        if (executableId == null) {
            return result;
        }
        Executable executable = executableRepository.selectOne(new Executable().setTenantId(tenantId).setExecutableId(executableId));
        Assert.notNull(executable, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Map<String, Object> map = objectMapper.readValue(executable.getStrategyParam(), new TypeReference<Map<String, Object>>() {
        });
        if (!map.containsKey(HsdrConstant.StrategyParam.JOB_WEIGHT)) {
            return result;
        }
        Map<String, Integer> jobWeight = (Map<String, Integer>) map.get(HsdrConstant.StrategyParam.JOB_WEIGHT);
        result.putAll(jobWeight);
        return result;
    }
}
