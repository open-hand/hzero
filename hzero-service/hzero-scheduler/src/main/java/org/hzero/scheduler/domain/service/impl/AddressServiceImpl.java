package org.hzero.scheduler.domain.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.domain.entity.Executor;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.domain.service.IAddressService;
import org.hzero.scheduler.infra.redis.JobLock;
import org.hzero.scheduler.infra.util.AddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/17 14:13
 */
@Component
public class AddressServiceImpl implements IAddressService {

    private static final String CONTEXT = "CONTEXT";

    private final DiscoveryClient discoveryClient;
    private final JobLogRepository jobLogRepository;
    private final JobInfoRepository jobInfoRepository;

    @Autowired
    public AddressServiceImpl(DiscoveryClient discoveryClient,
                              JobLogRepository jobLogRepository,
                              JobInfoRepository jobInfoRepository) {
        this.discoveryClient = discoveryClient;
        this.jobLogRepository = jobLogRepository;
        this.jobInfoRepository = jobInfoRepository;
    }

    @Override
    public List<String> getAddressList(String serverName) {
        List<ServiceInstance> instanceList = new ArrayList<>();
        if (StringUtils.isNotBlank(serverName)) {
            instanceList = discoveryClient.getInstances(serverName);
        }
        List<String> result = new ArrayList<>();
        for (ServiceInstance instance : instanceList) {
            String contextPath = "";
            Map<String, String> metaData = instance.getMetadata();
            if (metaData != null) {
                for (Map.Entry<String, String> item : metaData.entrySet()) {
                    if (CONTEXT.equalsIgnoreCase(item.getKey())) {
                        contextPath = item.getValue();
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(contextPath) && !contextPath.startsWith(BaseConstants.Symbol.SLASH)) {
                contextPath = BaseConstants.Symbol.SLASH + contextPath;
            }
            result.add(instance.getHost() + ":" + instance.getPort() + contextPath);
        }
        // 排个序，保证每次获取到的地址列表顺序是一样的
        return result.stream().sorted(Comparator.comparing(item -> item)).collect(Collectors.toList());
    }

    @Override
    public List<String> getAddressList(Executor executor) {
        List<String> urlList;
        if (Objects.equals(executor.getExecutorType(), BaseConstants.Flag.YES)) {
            // 手动注册
            String address = executor.getAddressList();
            urlList = StringUtils.isNotBlank(address) ? AddressUtils.getAddressList(address) : new ArrayList<>();
        } else {
            // 自动注册
            urlList = getAddressList(executor.getServerName());
            // 执行器为自动注册，检测到下线，清理调度日志，清理任务锁
            if (CollectionUtils.isEmpty(urlList)) {
                List<JobInfo> jobList = jobInfoRepository.select(new JobInfo().setExecutorId(executor.getExecutorId()));
                Date now = new Date();
                for (JobInfo jobInfo : jobList) {
                    // 执行中的日志改为失败
                    jobLogRepository.updateLogOffline(jobInfo.getJobId(), now);
                    // 释放任务锁
                    JobLock.clearLock(jobInfo.getJobId());
                }
            }
        }
        return urlList;
    }

    @Override
    public boolean isServerName(String serverName) {
        List<ServiceInstance> instanceList = new ArrayList<>();
        if (StringUtils.isNotBlank(serverName)) {
            instanceList = discoveryClient.getInstances(serverName);
        }
        return CollectionUtils.isNotEmpty(instanceList);
    }
}
