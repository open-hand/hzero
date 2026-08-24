package org.hzero.scheduler.infra.job;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.api.dto.UserInfo;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.boot.scheduler.infra.tool.ProgressCache;
import org.hzero.boot.scheduler.infra.util.ExceptionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.properties.CoreProperties;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.domain.entity.ExecutorConfig;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.entity.JobLog;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.domain.service.IAddressService;
import org.hzero.scheduler.domain.service.IJobService;
import org.hzero.scheduler.domain.service.IUrlService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.scheduler.infra.redis.JobLock;
import org.hzero.scheduler.infra.util.RpcClient;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

/**
 * quartz-job执行类
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/08 15:13
 */
public class MyJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyJob.class);

    /**
     * 默认最大重试次数
     */
    private Integer retryNumber = 3;

    private final IJobService jobService = ApplicationContextHelper.getContext().getBean(IJobService.class);
    private final JobLogRepository jobLogRepository = ApplicationContextHelper.getContext().getBean(JobLogRepository.class);
    private final IUrlService urlService = ApplicationContextHelper.getContext().getBean(IUrlService.class);
    private final IAddressService addressService = ApplicationContextHelper.getContext().getBean(IAddressService.class);
    private final RedisHelper redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
    private final ObjectMapper objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
    private Integer retry = 1;
    private String executorStrategy;
    private String failStrategy;

    @Override
    public void execute(JobExecutionContext context) {
        String triggerName = context.getTrigger().getKey().getName();
        Long jobId = Long.valueOf(context.getJobDetail().getKey().getName());
        LOGGER.info(" Scheduler Job Start.  JobId : {} ", jobId);
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobLog jobLog = new JobLog();
        String address;
        Long executorId = jobDataMap.getLongValue(JobDataDTO.FIELD_EXECUTOR_ID);
        Integer serial = (jobDataMap.get(JobInfo.FIELD_SERIAL) == null || StringUtils.isBlank(String.valueOf(jobDataMap.get(JobInfo.FIELD_SERIAL)))) ?
                BaseConstants.Flag.YES : Integer.valueOf(String.valueOf(jobDataMap.get(JobInfo.FIELD_SERIAL)));
        if (Objects.equals(serial, BaseConstants.Flag.YES) && !JobLock.addLock(jobId)) {
            // 任务为串行，若有任务未结束，跳过本次执行
            LOGGER.info(" Scheduler Job Locked.  JobId : {} ", jobId);
            return;
        }
        try {
            // 解析jobDataMap参数
            String jobCode = jobDataMap.getString(JobDataDTO.FIELD_JOB_CODE);
            Long tenantId = jobDataMap.getLongValue(JobDataDTO.FIELD_TENANT_ID);
            UserInfo userInfo;
            if (jobDataMap.containsKey(JobDataDTO.FIELD_USER_INFO)) {
                userInfo = objectMapper.readValue(String.valueOf(jobDataMap.get(JobDataDTO.FIELD_USER_INFO)), UserInfo.class);
            } else {
                // 默认用户信息
                userInfo = new UserInfo();
            }
            String jobType = jobDataMap.getString(JobDataDTO.FIELD_JOB_TYPE);
            String jobHandler = jobDataMap.getString(JobDataDTO.FIELD_JOB_HANDLER);
            String paramStr = jobDataMap.getString(JobDataDTO.FIELD_PARAM);
            this.failStrategy = jobDataMap.getString(JobInfo.FIELD_FAIL_STRATEGY);
            this.executorStrategy = jobDataMap.getString(JobInfo.FIELD_EXECUTOR_STRATEGY);
            if (jobDataMap.containsKey(HsdrConstant.StrategyParam.RETRY_NUMBER)) {
                retryNumber = Integer.parseInt(String.valueOf(jobDataMap.get(HsdrConstant.StrategyParam.RETRY_NUMBER)));
            }
            // 不可用的url列表
            Set<String> errorList = new HashSet<>();
            // 获取任务执行地址
            address = urlService.getUrl(executorStrategy, executorId, jobId, errorList);
            Date now = new Date();
            if (StringUtils.isNotBlank(address)) {
                // 任务开始，新建日志记录
                jobLog.setJobId(jobId)
                        .setTenantId(tenantId)
                        .setExecutorId(executorId)
                        .setStartTime(now)
                        .setJobResult(HsdrConstant.JobResult.SUCCESS)
                        .setClientResult(HsdrConstant.ClientResult.DOING)
                        .setAddress(address);
                jobLogRepository.insertSelective(jobLog);
                // 组装任务执行对象
                JobDataDTO jobDataDTO = new JobDataDTO()
                        .setJobId(jobId)
                        .setLogId(jobLog.getLogId())
                        .setJobCode(jobCode)
                        .setJobHandler(jobHandler)
                        .setJobType(jobType)
                        .setTenantId(tenantId)
                        .setRunTime(now.getTime())
                        .setUserInfo(userInfo)
                        .setParam(paramStr);
                // 获取token
                String token = jobDataMap.containsKey(HsdrConstant.TOKEN) ? jobDataMap.getString(HsdrConstant.TOKEN) : buildToken(userInfo);
                // 向客户端发送任务执行请求
                send(address, jobLog, token, jobDataDTO, errorList);
            } else {
                // 没有可用的执行器地址
                jobLog.setJobId(jobId)
                        .setTenantId(tenantId)
                        .setExecutorId(executorId)
                        .setStartTime(now)
                        .setJobResult(HsdrConstant.JobResult.FAILURE)
                        .setMessageHeader("No normal executor found.")
                        .setMessage("Can not find a normal executor, please check the executor currently in use!");
                jobLogRepository.insertSelective(jobLog);
                jobService.sendEmail(jobLog.getLogId());
                // 解除串行任务锁
                JobLock.clearLock(jobLog.getJobId());
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getMessage(e));
            if (jobLog.getLogId() != null) {
                // 更新日志
                jobLogRepository.updateByPrimaryKey(jobLog.setJobResult(HsdrConstant.JobResult.FAILURE).setClientResult(null)
                        .setMessageHeader(e.getMessage()).setMessage(ExceptionUtils.getMessage(e)));
                jobService.sendEmail(jobLog.getLogId());
            }
        }

        // 若任务是最后一次执行，清理该任务的所有缓存信息
        if (context.getNextFireTime() == null && Objects.equals(String.valueOf(jobId), triggerName)) {
            clearJobInfo(executorId, jobId);
        }
    }

    /**
     * 向客户端发送请求
     */
    private void send(String address, JobLog jobLog, String token, JobDataDTO jobDataDTO, Set<String> errorList) {
        String data;
        try {
            data = objectMapper.writeValueAsString(jobDataDTO);
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.PARAMETER_ERROR, e);
        }
        if (StringUtils.isNotBlank(address)) {
            String url = "http://" + address + BootSchedulerConstant.EXECUTOR_PATH;
            try {
                if (addressService.isServerName(address)) {
                    RpcClient.runJobWithRestTemplate(url, token, jobDataDTO);
                } else {
                    RpcClient.runJob(url, token, data);
                }
            } catch (Exception e) {
                failureStrategy(address, jobLog, token, jobDataDTO, errorList, e);
            }
        } else {
            // 没有可用的执行器
            jobLog.setJobResult(HsdrConstant.JobResult.FAILURE)
                    .setClientResult(null)
                    .setMessageHeader("No normal executor found.")
                    .setMessage("Can not find a normal executor, please check the executor currently in use!");
            jobLogRepository.updateByPrimaryKey(jobLog);
            jobService.sendEmail(jobLog.getLogId());
            // 解除串行任务锁
            JobLock.clearLock(jobLog.getJobId());
        }
    }

    /**
     * 调度失败任务处理
     */
    private void failureStrategy(String address, JobLog jobLog, String token, JobDataDTO jobDataDTO, Set<String> errorList, Exception e) {
        switch (failStrategy) {
            case HsdrConstant.FailStrategy.RETRY:
                this.retry = this.retry + 1;
                // 5秒后重试
                if (this.retry < retryNumber) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (Exception ex) {
                        LOGGER.error("wait to run job error!");
                    }
                    send(address, jobLog, token, jobDataDTO, errorList);
                } else {
                    jobLog.setMessageHeader(e.getMessage()).setMessage(ExceptionUtils.getMessage(e));
                    failCallback(jobLogRepository, jobService, jobLog);
                }
                break;
            case HsdrConstant.FailStrategy.TRANSFER:
                errorList.add(address);
                String newAddress = urlService.getUrl(executorStrategy, jobLog.getExecutorId(), jobLog.getJobId(), errorList);
                jobLog.setAddress(newAddress);
                jobLogRepository.updateByPrimaryKey(jobLog);
                send(newAddress, jobLog, token, jobDataDTO, errorList);
                break;
            default:
                jobLog.setMessageHeader(e.getMessage()).setMessage(ExceptionUtils.getMessage(e));
                failCallback(jobLogRepository, jobService, jobLog);
                break;
        }
    }

    /**
     * 失败回调
     */
    private void failCallback(JobLogRepository jobLogRepository, IJobService jobService, JobLog jobLog) {
        ProgressCache progressCache = ApplicationContextHelper.getContext().getBean(ProgressCache.class);
        jobLogRepository.updateByPrimaryKey(jobLog.setJobResult(HsdrConstant.JobResult.FAILURE).setClientResult(null));
        // 解除串行任务锁
        JobLock.clearLock(jobLog.getJobId());
        progressCache.clearRedisCache(jobLog.getLogId(), redisHelper);
        jobService.sendEmail(jobLog.getLogId());
    }

    /**
     * 清理该任务的缓存信息
     */
    private void clearJobInfo(Long executorId, Long jobId) {
        // 清理轮询策略缓存
        urlService.clearPolling(executorId, jobId);

        // 清理最大并发控制缓存
        ExecutorConfig.deleteCache(redisHelper, executorId, jobId);
    }

    /**
     * 构建token
     */
    private String buildToken(UserInfo userInfo) throws IOException {
        CustomUserDetails customUserDetails = userInfo.buildCustomUserDetails();
        CoreProperties coreProperties = ApplicationContextHelper.getContext().getBean(CoreProperties.class);
        Signer signer = new MacSigner(coreProperties.getOauthJwtKey());
        return "Bearer " + JwtHelper.encode(objectMapper.writeValueAsString(customUserDetails), signer).getEncoded();
    }
}