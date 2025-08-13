package org.hzero.scheduler.app.service.impl;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.scheduler.api.dto.ChildJobDTO;
import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.api.dto.UserInfo;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.api.dto.JobInfoQueryDTO;
import org.hzero.scheduler.app.service.JobInfoService;
import org.hzero.scheduler.app.service.JobLogService;
import org.hzero.scheduler.domain.entity.*;
import org.hzero.scheduler.domain.repository.*;
import org.hzero.scheduler.domain.service.IJobService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.scheduler.infra.util.RpcClient;
import org.hzero.scheduler.infra.util.ValidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:57:29
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobInfoServiceImpl.class);

    @Autowired
    private JobInfoRepository jobInfoRepository;
    @Autowired
    private ExecutorRepository executorRepository;
    @Autowired
    private ConcurrentRequestRepository requestRepository;
    @Autowired
    private JobLogService jobLogService;
    @Autowired
    private JobLogRepository jobLogRepository;
    @Autowired
    private IJobService jobService;
    @Autowired
    private ExecutorConfigRepository configRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Page<JobInfo> pageJobInfo(JobInfoQueryDTO jobInfoQueryDTO, PageRequest pageRequest) {
        String jobStatus = jobInfoQueryDTO.getJobStatus();
        if (StringUtils.isNotBlank(jobStatus)) {
            jobInfoQueryDTO.setTriggerStatus(jobService.getTriggerStatus(jobStatus));
            if (Objects.equals(jobStatus, HsdrConstant.JobStatus.NONE)) {
                jobInfoQueryDTO.setIncludeNull(true);
            }
        }
        Page<JobInfo> page = jobInfoRepository.pageJobInfo(jobInfoQueryDTO, pageRequest);
        page.forEach(item -> item.setJobStatus(jobService.getJobStatus(item.getJobStatus())));
        return page;
    }

    @Override
    public JobInfo detailJobInfo(Long jobId, Long tenantId) {
        JobInfo jobInfo = jobInfoRepository.detailJobInfo(jobId, tenantId);
        Assert.notNull(jobInfo, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        String executorStrategy = jobInfo.getExecutorStrategy();
        try {
            // 执行器策略为任务-执行器权重，校验执行参数中的jobWeight
            if (Objects.equals(executorStrategy, HsdrConstant.ExecutorStrategy.JOB_WEIGHT) && StringUtils.isNotBlank(jobInfo.getStrategyParam())) {
                Map<String, Object> map = objectMapper.readValue(jobInfo.getStrategyParam(), new TypeReference<Map<String, Object>>() {
                });
                String addressList = executorRepository.selectByPrimaryKey(jobInfo.getExecutorId()).getAddressList();
                if (StringUtils.isBlank(addressList)) {
                    map.remove(HsdrConstant.StrategyParam.JOB_WEIGHT);
                } else {
                    getJobWeight(map, addressList, jobInfo.getExecutorId());
                }
                jobInfo.setStrategyParam(objectMapper.writeValueAsString(map));
            }
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
        // 任务状态
        jobInfo.setJobStatus(jobService.getJobStatus(jobId, tenantId));
        return jobInfo;
    }

    /**
     * 获取执行器当前的权重信息
     */
    private void getJobWeight(Map<String, Object> map, String addressList, Long executorId) throws IOException {
        Map<String, Integer> weight = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        if (map.containsKey(HsdrConstant.StrategyParam.JOB_WEIGHT)) {
            weight = objectMapper.readValue(objectMapper.writeValueAsString(map.get(HsdrConstant.StrategyParam.JOB_WEIGHT)), new TypeReference<Map<String, Integer>>() {
            });
        }
        Map<String, Integer> newMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        String[] address = addressList.split(HsdrConstant.COMMA);
        for (String item : address) {
            Integer w = weight.get(item);
            if (w == null) {
                // 参数中没有该地址，去config表查询
                ExecutorConfig config = configRepository.selectByUnique(executorId, item);
                if (config == null) {
                    // 数据库中也没有，取默认值1
                    w = BaseConstants.Digital.ONE;
                } else {
                    w = config.getWeight();
                }
            }
            newMap.put(item, w);
        }
        map.put(HsdrConstant.StrategyParam.JOB_WEIGHT, newMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobInfo createJob(JobInfo jobInfo) {
        // 获取关联的执行器
        if (jobInfo.getExecutorId() == null) {
            Assert.isTrue(StringUtils.isNotBlank(jobInfo.getExecutorCode()), BaseConstants.ErrorCode.DATA_INVALID);
            Executor executor = executorRepository.selectOne(new Executor().setExecutorCode(jobInfo.getExecutorCode()));
            Assert.notNull(executor, HsdrErrorCode.EXECUTOR_NOT_FIND);
            jobInfo.setExecutorId(executor.getExecutorId());
        }
        if (Objects.equals(jobInfo.getCycleFlag(), BaseConstants.Flag.NO)) {
            jobInfo.setJobCron(null);
        }
        jobInfo.validate();
        ValidUtils.isJsonValid(jobInfo.getJobParam());
        // 添加额外参数
        jobInfo.setExpandParam(preParam());
        jobInfoRepository.insertSelective(jobInfo);
        // 新建定时任务
        jobService.addJob(jobInfo);
        return jobInfo;
    }

    /**
     * 组装额外参数
     */
    private String preParam() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        UserInfo userInfo = new UserInfo(customUserDetails);
        try {
            return objectMapper.writeValueAsString(userInfo);
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createChildJob(ChildJobDTO childJobDTO) {
        JobInfo jobInfo = new JobInfo();
        Executor executor = executorRepository.selectOne(new Executor().setExecutorCode(childJobDTO.getExecutorCode()));
        if (executor != null) {
            BeanUtils.copyProperties(childJobDTO, jobInfo);
            jobInfo.setExecutorId(executor.getExecutorId());
            // 目前子任务只支持非周期任务
            jobInfo.setCycleFlag(BaseConstants.Flag.NO);
            createJob(jobInfo);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobInfo updateJob(JobInfo jobInfo) {
        // 校验任务状态
        if (!Objects.equals(jobService.getJobStatus(jobInfo.getJobId(), jobInfo.getTenantId()), HsdrConstant.JobStatus.NORMAL)) {
            throw new CommonException(HsdrErrorCode.EDIT_JOB);
        }
        if (Objects.equals(jobInfo.getCycleFlag(), BaseConstants.Flag.NO)) {
            jobInfo.setJobCron(null);
        }
        // 移除任务-执行器策略的参数
        jobInfo.setStrategyParam(clearStrategyParam(jobInfo.getExecutorStrategy(), jobInfo.getStrategyParam()));
        ValidUtils.isJsonValid(jobInfo.getJobParam());
        // 添加额外参数
        jobInfo.setExpandParam(preParam());
        jobInfoRepository.updateOptional(jobInfo,
                JobInfo.FIELD_ALARM_EMAIL,
                JobInfo.FIELD_DESCRIPTION,
                JobInfo.FIELD_END_DATE,
                JobInfo.FIELD_EXECUTOR_ID,
                JobInfo.FIELD_EXECUTOR_STRATEGY,
                JobInfo.FIELD_FAIL_STRATEGY,
                JobInfo.FIELD_STRATEGY_PARAM,
                JobInfo.FIELD_GLUE_TYPE,
                JobInfo.FIELD_JOB_HANDLER,
                JobInfo.FIELD_JOB_CRON,
                JobInfo.FIELD_JOB_PARAM,
                JobInfo.FIELD_CYCLE_FLAG,
                JobInfo.FIELD_START_DATE,
                JobInfo.FIELD_SERIAL,
                JobInfo.FIELD_EXPAND_PARAM,
                JobInfo.FIELD_INIT_FLAG);
        jobService.updateJob(jobInfo);
        return jobInfo;
    }

    @Override
    public String clearStrategyParam(String executorStrategy, String strategyParam) {
        if (!Objects.equals(executorStrategy, HsdrConstant.ExecutorStrategy.JOB_WEIGHT) && StringUtils.isNotBlank(strategyParam)) {
            try {
                Map<String, Object> map = objectMapper.readValue(strategyParam, new TypeReference<Map<String, Object>>() {
                });
                map.remove(HsdrConstant.StrategyParam.JOB_WEIGHT);
                return objectMapper.writeValueAsString(map);
            } catch (IOException e) {
                throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
            }
        }
        return strategyParam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(List<JobInfo> jobInfoList) {
        jobInfoList.forEach(item -> {
            Long jobId = item.getJobId();
            jobInfoRepository.deleteByPrimaryKey(jobId);
            // 删除对应并发请求
            requestRepository.delete(new ConcurrentRequest().setJobId(jobId));
            // 删除任务的日志及文件
            jobLogService.clearLog(HsdrConstant.ClearLogType.ALL, jobId, item.getTenantId());
        });
    }

    @Async("commonAsyncTaskExecutor")
    @Override
    public void stopClient(JobInfo jobInfo, String jwtToken) {
        Long jobId = jobInfo.getJobId();
        Set<String> address = getClientAddress(jobInfo);
        if (CollectionUtils.isNotEmpty(address)) {
            address.forEach(ip -> {
                String url = "http://" + ip + BootSchedulerConstant.STOP_JOB_PATH;
                sendRequest(url, jobId, jwtToken);
            });
        }
    }

    @Async("commonAsyncTaskExecutor")
    @Override
    public void pauseClient(JobInfo jobInfo, String jwtToken) {
        Long jobId = jobInfo.getJobId();
        Set<String> address = getClientAddress(jobInfo);
        if (CollectionUtils.isNotEmpty(address)) {
            address.forEach(ip -> {
                String url = "http://" + ip + BootSchedulerConstant.PAUSE_JOB_PATH;
                sendRequest(url, jobId, jwtToken);
            });
        }
    }

    private Set<String> getClientAddress(JobInfo jobInfo) {
        // 清理异常调度日志
        jobLogRepository.updateErrorLog();

        List<JobLog> logList = jobLogRepository.select(new JobLog().setClientResult(HsdrConstant.ClientResult.DOING).setJobId(jobInfo.getJobId()));
        Executor executor = executorRepository.selectByPrimaryKey(jobInfo.getExecutorId());
        int executorType = 0;
        if (executor != null) {
            executorType = executor.getExecutorType();
        }
        Set<String> set = new HashSet<>();
        for (JobLog log : logList) {
            String address = log.getAddress();
            if (executorType == 0 && !ValidUtils.isIpAndPort(address)) {
                // 自动注册执行器的地址不是ip地址，不回调
                continue;
            }
            set.add(address);
        }
        return set;
    }

    private void sendRequest(String url, Long jobId, String jwtToken) {
        if (StringUtils.isBlank(jwtToken)) {
            LOGGER.error("Get jwt_token filed!");
        }
        String result;
        try {
            result = RpcClient.stopJob(url, jwtToken, objectMapper.writeValueAsString(new JobDataDTO().setJobId(jobId)));
        } catch (Exception e) {
            result = "exception";
        }
        if (Objects.equals(result, BootSchedulerConstant.Response.FAILURE)) {
            throw new CommonException(HsdrErrorCode.KILL_JOB_THREAD);
        }
    }

    @Async("commonAsyncTaskExecutor")
    @Override
    public void initJob() {
        // 查询需要初始化的任务
        List<JobInfo> jobInfoList = jobInfoRepository.select(new JobInfo().setInitFlag(BaseConstants.Flag.YES));
        jobInfoList.forEach(item -> {
            // 判断任务状态
            String status = jobService.getJobStatus(item.getJobId(), item.getTenantId());
            if (Objects.equals(HsdrConstant.JobStatus.NONE, status)) {
                // 将任务添加到quartz
                jobService.addJob(item);
            }
        });
    }
}
