package org.hzero.scheduler.domain.service.impl;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.api.dto.UserInfo;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.properties.CoreProperties;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.config.SchedulerConfiguration;
import org.hzero.scheduler.domain.entity.Executor;
import org.hzero.scheduler.domain.entity.ExecutorConfig;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.entity.JobLog;
import org.hzero.scheduler.domain.repository.ExecutorConfigRepository;
import org.hzero.scheduler.domain.repository.ExecutorRepository;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.domain.service.IJobService;
import org.hzero.scheduler.domain.service.IUrlService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.scheduler.infra.job.MyJob;
import org.hzero.scheduler.infra.redis.JobLock;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * quartz 基础服务
 *
 * @author shuangfei.zhu@hand-china.com
 */
@Service
public class JobServiceImpl implements IJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private MessageClient messageClient;
    @Autowired
    private JobLogRepository jobLogRepository;
    @Autowired
    private JobInfoRepository jobInfoRepository;
    @Autowired
    private ExecutorRepository executorRepository;
    @Autowired
    private ExecutorConfigRepository configRepository;
    @Autowired
    private IUrlService urlService;
    @Autowired
    private SchedulerConfiguration schedulerConfig;
    @Qualifier("Scheduler")
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<String> getTriggerStatus(String jobStatus) {
        List<String> triggerStatusList = new ArrayList<>();
        switch (jobStatus) {
            case HsdrConstant.JobStatus.NONE:
                triggerStatusList.add(HsdrConstant.TriggerStatus.DELETED);
                break;
            case HsdrConstant.JobStatus.NORMAL:
                triggerStatusList.add(HsdrConstant.TriggerStatus.WAITING);
                triggerStatusList.add(HsdrConstant.TriggerStatus.ACQUIRED);
                triggerStatusList.add(HsdrConstant.TriggerStatus.EXECUTING);
                break;
            case HsdrConstant.JobStatus.PAUSED:
                triggerStatusList.add(HsdrConstant.TriggerStatus.PAUSED);
                triggerStatusList.add(HsdrConstant.TriggerStatus.PAUSED_BLOCKED);
                break;
            case HsdrConstant.JobStatus.COMPLETE:
                triggerStatusList.add(HsdrConstant.TriggerStatus.COMPLETE);
                break;
            case HsdrConstant.JobStatus.ERROR:
                triggerStatusList.add(HsdrConstant.TriggerStatus.ERROR);
                break;
            case HsdrConstant.JobStatus.BLOCKED:
                triggerStatusList.add(HsdrConstant.TriggerStatus.BLOCKED);
                break;
            default:
                break;
        }
        return triggerStatusList;
    }

    @Override
    public String getJobStatus(String triggerStatus) {
        if (StringUtils.isBlank(triggerStatus)) {
            return HsdrConstant.JobStatus.NONE;
        }
        switch (triggerStatus) {
            case HsdrConstant.TriggerStatus.WAITING:
            case HsdrConstant.TriggerStatus.EXECUTING:
            case HsdrConstant.TriggerStatus.ACQUIRED:
                return HsdrConstant.JobStatus.NORMAL;
            case HsdrConstant.TriggerStatus.COMPLETE:
                return HsdrConstant.JobStatus.COMPLETE;
            case HsdrConstant.TriggerStatus.BLOCKED:
                return HsdrConstant.JobStatus.BLOCKED;
            case HsdrConstant.TriggerStatus.ERROR:
                return HsdrConstant.JobStatus.ERROR;
            case HsdrConstant.TriggerStatus.PAUSED:
            case HsdrConstant.TriggerStatus.PAUSED_BLOCKED:
                return HsdrConstant.JobStatus.PAUSED;
            case HsdrConstant.TriggerStatus.DELETED:
                return HsdrConstant.JobStatus.NONE;
            default:
                return triggerStatus;
        }
    }

    @Override
    public String getJobStatus(Long jobId, Long tenantId) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(jobId), String.valueOf(tenantId));
            return scheduler.getTriggerState(triggerKey).name();
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID, e);
        }
    }

    @Override
    public void addJob(JobInfo jobInfo) {
        // 执行器最大并发量控制
        Executor executor = executorRepository.selectByPrimaryKey(jobInfo.getExecutorId());
        Assert.notNull(executor, HsdrErrorCode.EXECUTOR_NOT_FIND);
        List<String> address = StringUtils.isNotBlank(executor.getAddressList()) ? Arrays.asList(executor.getAddressList().split(HsdrConstant.COMMA)) : new ArrayList<>();
        address.forEach(item -> ExecutorConfig.addCache(redisHelper, configRepository, jobInfo.getExecutorId(), item, jobInfo.getJobId()));

        try {
            // 创建工作
            JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                    .withDescription(jobInfo.getDescription())
                    // jobId 做name, group使用租户Id
                    .withIdentity(String.valueOf(jobInfo.getJobId()), String.valueOf(jobInfo.getTenantId()))
                    .usingJobData(getMap(jobInfo))
                    .build();
            // 触发器
            Trigger trigger = buildTrigger(jobInfo);
            // 粘合工作和触发器
            Assert.notNull(trigger, HsdrErrorCode.CREATE_JOB);
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动调度器
            scheduler.start();
            LOGGER.debug("-----------------  add job success, jobId : {}  tenantId: {}", jobInfo.getJobId(), jobInfo.getTenantId());
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.Quartz.ADD_JOB, e);
        }
    }

    /**
     * 构建触发器
     *
     * @param jobInfo 任务信息
     * @return 触发器
     */
    private Trigger buildTrigger(JobInfo jobInfo) {
        TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(jobInfo.getJobId()), String.valueOf(jobInfo.getTenantId()));
        Trigger trigger = null;
        if (Objects.equals(jobInfo.getCycleFlag(), BaseConstants.Flag.YES)) {
            String cron = jobInfo.getJobCron();
            // 若cron没有指定，使用默认cron，模拟永不自动执行
            if (StringUtils.isBlank(cron)) {
                cron = HsdrConstant.CRON;
            }
            // 创建周期任务触发器
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing();
            if (jobInfo.getStartDate() != null && jobInfo.getEndDate() != null) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(cronScheduleBuilder)
                        .startAt(jobInfo.getStartDate())
                        .endAt(jobInfo.getEndDate())
                        .build();
            }
            if (jobInfo.getStartDate() != null && jobInfo.getEndDate() == null) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(cronScheduleBuilder)
                        .startAt(jobInfo.getStartDate())
                        .build();
            }
            if (jobInfo.getStartDate() == null && jobInfo.getEndDate() != null) {
                long currentTime = System.currentTimeMillis() + 30 * 1000;
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(cronScheduleBuilder)
                        .startAt(new Date(currentTime))
                        .endAt(jobInfo.getEndDate())
                        .build();
            }
            // 任务延迟30秒执行，留给quartz足够准备时间
            if (jobInfo.getStartDate() == null && jobInfo.getEndDate() == null) {
                long currentTime = System.currentTimeMillis() + 30 * 1000;
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(cronScheduleBuilder)
                        .startAt(new Date(currentTime))
                        .build();
            }
        } else {
            if (jobInfo.getStartDate() == null) {
                // 创建瞬时任务触发器
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(simpleSchedule().withRepeatCount(0))
                        .startNow()
                        .build();
            } else {
                // 创建瞬时任务触发器
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(simpleSchedule().withRepeatCount(0))
                        .startAt(jobInfo.getStartDate())
                        .build();
            }

        }
        return trigger;
    }

    /**
     * 构建任务参数
     */
    private JobDataMap getMap(JobInfo jobInfo) throws IOException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobDataDTO.FIELD_JOB_CODE, jobInfo.getJobCode());
        jobDataMap.put(JobDataDTO.FIELD_TENANT_ID, jobInfo.getTenantId());
        jobDataMap.put(JobDataDTO.FIELD_EXECUTOR_ID, jobInfo.getExecutorId());
        jobDataMap.put(JobDataDTO.FIELD_JOB_TYPE, jobInfo.getGlueType());
        jobDataMap.put(JobDataDTO.FIELD_JOB_HANDLER, jobInfo.getJobHandler());
        jobDataMap.put(JobInfo.FIELD_FAIL_STRATEGY, jobInfo.getFailStrategy());
        jobDataMap.put(JobInfo.FIELD_EXECUTOR_STRATEGY, jobInfo.getExecutorStrategy());
        jobDataMap.put(JobInfo.FIELD_SERIAL, jobInfo.getSerial() == null ? BaseConstants.Flag.NO : jobInfo.getSerial());
        if (jobInfo.getStartDate() != null) {
            jobDataMap.put(JobDataDTO.FIELD_START_DATE, jobInfo.getStartDate());
        }
        if (jobInfo.getEndDate() != null) {
            jobDataMap.put(JobDataDTO.FIELD_END_DATE, jobInfo.getEndDate());
        }
        jobDataMap.put(JobDataDTO.FIELD_PARAM, jobInfo.getJobParam());
        if (StringUtils.isNotBlank(jobInfo.getStrategyParam())) {
            Map<String, Object> strategyParam = objectMapper.readValue(jobInfo.getStrategyParam(), new TypeReference<Map<String, Object>>() {
            });
            jobDataMap.putAll(strategyParam);
        }
        // 设置额外参数
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        UserInfo userInfo;
        if (customUserDetails != null) {
            // 页面新建的任务，有customUserDetails
            userInfo = new UserInfo(customUserDetails);
            jobDataMap.put(JobDataDTO.FIELD_USER_INFO, objectMapper.writeValueAsString(userInfo));
        } else {
            // 服务自动初始化的
            jobDataMap.put(JobDataDTO.FIELD_USER_INFO, jobInfo.getExpandParam());
            userInfo = objectMapper.readValue(jobInfo.getExpandParam(), UserInfo.class);
        }
        String token = buildToken(userInfo);
        jobDataMap.put(HsdrConstant.TOKEN, token);
        return jobDataMap;
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

    @Override
    public void updateJob(JobInfo jobInfo) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(jobInfo.getJobId()), String.valueOf(jobInfo.getTenantId()));
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                // 删除原有任务
                JobKey jobKey = JobKey.jobKey(String.valueOf(jobInfo.getJobId()), String.valueOf(jobInfo.getTenantId()));
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(jobKey);
                if (scheduler.checkExists(jobKey)) {
                    throw new CommonException(HsdrErrorCode.UPDATE_JOB);
                } else {
                    // 添加任务
                    addJob(jobInfo);
                }
            }
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.Quartz.UPDATE_JOB, e);
        }
    }

    @Override
    public void removeJob(Long jobId, Long executorId, Long tenantId) {
        // 清除轮询策略相关缓存
        urlService.clearPolling(executorId, jobId);
        // 清除最大并发控制相关缓存
        ExecutorConfig.deleteCache(redisHelper, executorId, jobId);
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(jobId), String.valueOf(tenantId));
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobId), String.valueOf(tenantId));
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
            LOGGER.debug("-----------------  delete job success, jobId : {}  tenantId: {}", jobId, tenantId);
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.Quartz.DELETE_JOB, e);
        }

    }

    @Override
    public void trigger(Long jobId, Long tenantId) {
        try {
            scheduler.triggerJob(JobKey.jobKey(String.valueOf(jobId), String.valueOf(tenantId)));
            LOGGER.debug("-----------------  trigger job success, jobId : {}  tenantId: {}", jobId, tenantId);
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.Quartz.TRIGGER_JOB, e);
        }
    }

    @Override
    public void pauseJob(Long jobId, Long tenantId) {
        try {
            scheduler.pauseJob(JobKey.jobKey(String.valueOf(jobId), String.valueOf(tenantId)));
            LOGGER.debug("-----------------  pause job success, jobId : {}  tenantId: {}", jobId, tenantId);
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.Quartz.PAUSE_JOB, e);
        }
    }

    @Override
    public void resumeJob(Long jobId, Long tenantId) {
        try {
            scheduler.resumeJob(JobKey.jobKey(String.valueOf(jobId), String.valueOf(tenantId)));
            LOGGER.debug("-----------------  resume job success, jobId : {}  tenantId: {}", jobId, tenantId);
        } catch (Exception e) {
            throw new CommonException(HsdrErrorCode.Quartz.RESUME_JOB, e);
        }
    }


    @Override
    public void sendEmail(Long logId) {
        JobLog jobLog = jobLogRepository.selectByPrimaryKey(logId);
        if (jobLog != null) {
            JobInfo jobInfo = jobInfoRepository.selectByPrimaryKey(jobLog.getJobId());
            if (jobInfo != null && StringUtils.isNotBlank(jobInfo.getAlarmEmail())) {
                Executor executor = executorRepository.selectByPrimaryKey(jobInfo.getExecutorId());
                String[] emailList = jobInfo.getAlarmEmail().split(BaseConstants.Symbol.COMMA);
                List<Receiver> receiverList = new ArrayList<>();
                for (String email : emailList) {
                    receiverList.add(new Receiver().setEmail(email));
                }
                Map<String, String> args = new HashMap<>(BaseConstants.Digital.SIXTEEN);
                args.put(Executor.FIELD_EXECUTOR_NAME, executor.getExecutorName());
                args.put(JobLog.FIELD_ADDRESS, jobLog.getAddress());
                args.put(JobInfo.FIELD_JOB_ID, String.valueOf(jobInfo.getJobId()));
                args.put(JobInfo.FIELD_JOB_CODE, jobInfo.getJobCode());
                args.put(JobInfo.FIELD_DESCRIPTION, jobInfo.getDescription());
                args.put(JobLog.FIELD_JOB_RESULT, jobLog.getJobResult());
                args.put(JobLog.FIELD_CLIENT_RESULT, jobLog.getClientResult());
                args.put(JobLog.FIELD_MESSAGE, jobLog.getMessage());
                messageClient.sendMessage(jobInfo.getTenantId(), schedulerConfig.getAlarmEmail().getMessageCode(), receiverList, args);
            }
        }
    }

    @Override
    public void callback(JobLogDTO jobLogDTO) {
        try {
            jobLogDTO.setEndTime(new Date());
            if (Objects.equals(jobLogDTO.getLogMessage(), StringUtils.EMPTY)) {
                jobLogDTO.setLogMessage(null);
            }
            jobLogRepository.updateLog(jobLogDTO);
            if (Objects.equals(jobLogDTO.getClientResult(), HsdrConstant.ClientResult.FAILURE)) {
                sendEmail(jobLogDTO.getLogId());
            }
        } catch (Exception e) {
            LOGGER.error("client callback {} error : {}", jobLogDTO, e.getMessage());
        } finally {
            // 解除任务锁
            JobLock.clearLock(jobLogDTO.getJobId());
        }
    }
}
