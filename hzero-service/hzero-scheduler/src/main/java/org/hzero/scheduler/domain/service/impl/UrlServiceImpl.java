package org.hzero.scheduler.domain.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.domain.entity.Executor;
import org.hzero.scheduler.domain.entity.ExecutorConfig;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.repository.ExecutorConfigRepository;
import org.hzero.scheduler.domain.repository.ExecutorRepository;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.domain.service.IAddressService;
import org.hzero.scheduler.domain.service.IUrlService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.util.ValidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 服务uri获取 实现类
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/08 16:32
 */
@Service
public class UrlServiceImpl implements IUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);

    private final RedisHelper redisHelper;
    private final ExecutorRepository executorRepository;
    private final JobInfoRepository jobInfoRepository;
    private final ExecutorConfigRepository configRepository;
    private final ObjectMapper objectMapper;
    private final IAddressService addressService;

    @Autowired
    public UrlServiceImpl(RedisHelper redisHelper,
                          ExecutorRepository executorRepository,
                          JobInfoRepository jobInfoRepository,
                          ExecutorConfigRepository configRepository,
                          ObjectMapper objectMapper,
                          IAddressService addressService) {
        this.redisHelper = redisHelper;
        this.executorRepository = executorRepository;
        this.jobInfoRepository = jobInfoRepository;
        this.configRepository = configRepository;
        this.objectMapper = objectMapper;
        this.addressService = addressService;
    }

    @Override
    public String getUrl(String executorStrategy, Long executorId, Long jobId, Set<String> errorUrls) {
        // 查询执行器配置，过滤禁用的执行器
        List<ExecutorConfig> configList = configRepository.select(new ExecutorConfig().setExecutorId(executorId).setEnabledFlag(BaseConstants.Flag.NO));
        if (CollectionUtils.isNotEmpty(configList)) {
            errorUrls.addAll(configList.stream().map(ExecutorConfig::getAddress).collect(Collectors.toList()));
        }
        Executor executor = executorRepository.selectByPrimaryKey(executorId);
        if (executor == null) {
            return StringUtils.EMPTY;
        }
        String result = StringUtils.EMPTY;
        switch (executorStrategy) {
            // 执行器策略在这里拓展
            case HsdrConstant.ExecutorStrategy.POLLING:
                result = polling(executor, jobId, errorUrls);
                break;
            case HsdrConstant.ExecutorStrategy.WEIGHT:
                result = weight(executor, jobId, errorUrls);
                break;
            case HsdrConstant.ExecutorStrategy.JOB_WEIGHT:
                result = jobWeight(executor, jobId, errorUrls);
                break;
            default:
                break;
        }
        if (StringUtils.isNotBlank(result) && Objects.equals(executor.getExecutorType(), BaseConstants.Flag.NO)) {
            // 自动注册的执行器获取到的地址不是ip，直接返回执行器关联的服务名
            if (!ValidUtils.isIpAndPort(result)) {
                result = executor.getServerName();
            }
        }
        return result;
    }

    /**
     * 获取可用的地址集合
     *
     * @param executor  执行器
     * @param errorUrls 排除的url
     * @return 可用的地址集合
     */
    private List<String> usableUrl(Executor executor, Set<String> errorUrls) {
        if (executor == null || !Objects.equals(executor.getStatus(), HsdrConstant.ExecutorStatus.ONLINE)) {
            // 执行器不存在或不在线，返回空
            return new ArrayList<>();
        }
        List<String> urlList = addressService.getAddressList(executor);
        if (CollectionUtils.isEmpty(urlList)) {
            return Collections.emptyList();
        }
        // 排除不可用的地址
        return urlList.stream().filter(item -> !errorUrls.contains(item)).collect(Collectors.toList());
    }

    /**
     * 轮询
     *
     * @param executor  执行器
     * @param jobId     任务Id
     * @param errorUrls 排除的url
     * @return 本次返回的url
     */
    private String polling(Executor executor, Long jobId, Set<String> errorUrls) {
        Long executorId = executor.getExecutorId();
        List<String> urlList = usableUrl(executor, errorUrls);
        if (CollectionUtils.isEmpty(urlList)) {
            return StringUtils.EMPTY;
        }
        try {
            Integer index = getCache(executorId, jobId);
            // 刷新缓存
            refreshCache(executorId, jobId, index);
            index = (index + 1) % urlList.size();
            String address = urlList.get(index);
            if (usable(address, jobId, executorId, errorUrls)) {
                return address;
            } else {
                // 重新执行方法获取新的执行地址
                return polling(executor, jobId, errorUrls);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return StringUtils.EMPTY;
        }
    }

    /**
     * 轮询策略缓存---刷新缓存
     *
     * @param executorId 执行器Id
     * @param jobId      任务Id
     * @param index      下标
     */
    private void refreshCache(Long executorId, Long jobId, Integer index) {
        String key = HZeroService.Scheduler.CODE + ":job-polling:" + executorId + ":" + jobId;
        redisHelper.strSet(key, String.valueOf(index + 1));
        redisHelper.setExpire(key, 30L, TimeUnit.DAYS);
    }

    /**
     * 轮询策略缓存---查询缓存
     *
     * @param executorId 执行器Id
     * @param jobId      任务Id
     */
    private Integer getCache(Long executorId, Long jobId) {
        String key = HZeroService.Scheduler.CODE + ":job-polling:" + executorId + ":" + jobId;
        String result = redisHelper.strGet(key);
        if (result == null) {
            return -1;
        }
        return Integer.valueOf(result);
    }

    /**
     * 清理轮询相关缓存
     *
     * @param executorId 执行器Id
     * @param jobId      任务Id
     */
    @Override
    public void clearPolling(Long executorId, Long jobId) {
        redisHelper.delKey(HZeroService.Scheduler.CODE + ":job-polling:" + executorId + ":" + jobId);
    }

    /**
     * 执行器权重策略
     *
     * @param executor  执行器
     * @param jobId     任务Id
     * @param errorUrls 排除的url
     * @return 本次返回的url
     */
    private String weight(Executor executor, Long jobId, Set<String> errorUrls) {
        Long executorId = executor.getExecutorId();
        List<String> addressList = usableUrl(executor, errorUrls);
        if (CollectionUtils.isEmpty(addressList)) {
            return StringUtils.EMPTY;
        }
        // 从config表获取执行器权重数据
        List<ExecutorConfig> configList = new ArrayList<>();
        Integer weightSum = 0;
        for (String address : addressList) {
            ExecutorConfig config = configRepository.selectByUnique(executorId, address);
            if (config == null) {
                // config表没有该执行器信息
                config = new ExecutorConfig().setWeight(BaseConstants.Digital.ONE).setAddress(address).setEnabledFlag(BaseConstants.Flag.YES);
            }
            weightSum += config.getWeight();
            configList.add(config);
        }
        // 权重算法获取执行地址
        String address = calculateWeight(configList, weightSum);
        if (usable(address, jobId, executorId, errorUrls)) {
            return address;
        } else {
            // 重新执行方法获取新的执行地址
            return weight(executor, jobId, errorUrls);
        }
    }

    /**
     * 任务-执行器权重策略
     *
     * @param executor  执行器
     * @param jobId     任务Id
     * @param errorUrls 排除的url
     * @return 本次返回的url
     */
    private String jobWeight(Executor executor, Long jobId, Set<String> errorUrls) {
        Long executorId = executor.getExecutorId();
        List<String> addressList = usableUrl(executor, errorUrls);
        JobInfo jobInfo = jobInfoRepository.selectByPrimaryKey(jobId);
        if (CollectionUtils.isEmpty(addressList) || jobInfo == null) {
            return StringUtils.EMPTY;
        }
        try {
            Map<String, Object> map = objectMapper.readValue(jobInfo.getStrategyParam(), new TypeReference<Map<String, Object>>() {
            });
            if (map.containsKey(HsdrConstant.StrategyParam.JOB_WEIGHT)) {
                // 准备数据
                Map jobWeight = (Map) map.get(HsdrConstant.StrategyParam.JOB_WEIGHT);
                List<ExecutorConfig> configList = new ArrayList<>();
                Integer weightSum = 0;
                for (String item : addressList) {
                    if (jobWeight.containsKey(item)) {
                        Integer weight = (Integer) jobWeight.get(item);
                        configList.add(new ExecutorConfig().setAddress(item).setWeight(weight).setEnabledFlag(BaseConstants.Flag.YES));
                        weightSum += weight;
                    } else {
                        // 查询数据库配置
                        ExecutorConfig config = configRepository.selectByUnique(executorId, item);
                        // config表中没有数据的执行器
                        if (config == null) {
                            config = new ExecutorConfig().setWeight(BaseConstants.Digital.ONE).setAddress(item).setEnabledFlag(BaseConstants.Flag.YES);
                        }
                        configList.add(config);
                        weightSum += config.getWeight();
                    }
                }
                // 权重算法获取执行地址
                String address = calculateWeight(configList, weightSum);
                if (usable(address, jobId, executorId, errorUrls)) {
                    return address;
                } else {
                    // 重新执行方法获取新的执行地址
                    return jobWeight(executor, jobId, errorUrls);
                }
            } else {
                // 没有任务自定义的权重数据，进行执行器权重策略
                return weight(executor, jobId, errorUrls);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return StringUtils.EMPTY;
        }
    }

    /**
     * 权重计算
     *
     * @param list      执行器配置列表
     * @param weightSum 权重和
     * @return 结果
     */
    private String calculateWeight(List<ExecutorConfig> list, Integer weightSum) {
        int n = new SecureRandom().nextInt(weightSum);
        int m = 0;
        String address = StringUtils.EMPTY;
        for (ExecutorConfig config : list) {
            if (m <= n && n < m + config.getWeight()) {
                // 权重计算结果
                address = config.getAddress();
                break;
            }
            m += config.getWeight();
        }
        // 若之前没有成功获取数据，则取第一个为替补
        if (StringUtils.isBlank(address)) {
            address = list.get(0).getAddress();
        }
        return address;
    }

    /**
     * 最大并发量校验
     *
     * @param address 执行器地址
     * @param jobId   任务Id
     * @return 是否可用
     */
    private boolean usable(String address, Long jobId, Long executorId, Set<String> errorUrls) {
        List<String> data = ExecutorConfig.getCache(redisHelper, executorId, address);
        if (data.contains(String.valueOf(jobId))) {
            // 任务可以使用该地址
            return true;
        } else {
            boolean flag = ExecutorConfig.addCache(redisHelper, configRepository, executorId, address, jobId);
            if (!flag) {
                // 将该地址加入不可用地址列表
                errorUrls.add(address);
            }
            return flag;
        }
    }
}
