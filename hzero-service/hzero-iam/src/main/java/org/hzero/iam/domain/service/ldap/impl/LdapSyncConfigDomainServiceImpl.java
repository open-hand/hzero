package org.hzero.iam.domain.service.ldap.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;

import org.hzero.boot.scheduler.configure.SchedulerConfig;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.hzero.iam.api.dto.ExecutorDTO;
import org.hzero.iam.api.dto.JobInfoDTO;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapSyncConfig;
import org.hzero.iam.domain.repository.LdapSyncConfigRepository;
import org.hzero.iam.domain.service.ldap.LdapSyncConfigDomainService;
import org.hzero.iam.domain.service.ldap.LdapSyncUserTask;
import org.hzero.iam.domain.service.ldap.LdapUserService;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.LdapSyncFrequency;
import org.hzero.iam.infra.constant.LdapSyncType;
import org.hzero.iam.infra.feign.SchedulerFeignClient;

/**
 * Ldap 同步配置 领域服务实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/09 16:10
 */
@Component
public class LdapSyncConfigDomainServiceImpl implements LdapSyncConfigDomainService {

    private final LdapSyncConfigRepository ldapSyncConfigRepository;
    private final SchedulerFeignClient schedulerFeignClient;
    private final ObjectMapper objectMapper;
    private final LdapSyncUserTask ldapSyncUserTask;
    private final LdapSyncUserTask.FinishFallback finishFallback;
    private final LdapUserService ldapUserService;
    private final SchedulerConfig schedulerConfig;

    @Autowired
    public LdapSyncConfigDomainServiceImpl(LdapSyncConfigRepository ldapSyncConfigRepository,
                    SchedulerFeignClient schedulerFeignClient, ObjectMapper objectMapper,
                    LdapSyncUserTask ldapSyncUserTask, LdapSyncUserTask.FinishFallback finishFallback,
                    LdapUserService ldapUserService, SchedulerConfig schedulerConfig) {
        this.ldapSyncConfigRepository = ldapSyncConfigRepository;
        this.schedulerFeignClient = schedulerFeignClient;
        this.objectMapper = objectMapper;
        this.ldapSyncUserTask = ldapSyncUserTask;
        this.finishFallback = finishFallback;
        this.ldapUserService = ldapUserService;
        this.schedulerConfig = schedulerConfig;
    }

    @Override
    public LdapSyncConfig createLdapSyncConfig(Long tenantId, LdapSyncConfig ldapSyncConfig) {
        // 校验 Ldap 同步配置在租户下是否唯一
        validateUniqueConfig(tenantId, ldapSyncConfig.getSyncType());
        ldapSyncConfig.setTenantId(tenantId);
        ldapSyncConfigRepository.insertSelective(ldapSyncConfig);
        // 启用状态新建调度任务
        if (BaseConstants.Flag.YES.equals(ldapSyncConfig.getEnabledFlag())) {
            createJobInfoUpdateJobId(ldapSyncConfig);
        }
        return ldapSyncConfig;
    }

    @Override
    public LdapSyncConfig updateLdapUserConfig(Long tenantId, LdapSyncConfig ldapSyncConfig) {
        LdapSyncConfig oldConfig = ldapSyncConfigRepository.selectByPrimaryKey(ldapSyncConfig.getLdapSyncConfigId());
        if (BaseConstants.Flag.YES.equals(oldConfig.getEnabledFlag())
                        && BaseConstants.Flag.YES.equals(ldapSyncConfig.getEnabledFlag())) {
            // 启用->启用：更新调度任务
            updateJobInfo(tenantId, ldapSyncConfig);
        } else if (BaseConstants.Flag.NO.equals(oldConfig.getEnabledFlag())
                        && BaseConstants.Flag.YES.equals(ldapSyncConfig.getEnabledFlag())) {
            // 停用->启用：创建调度任务 / 更新调度任务
            if (oldConfig.getSyncJobId() == null) {
                createJobInfoUpdateJobId(ldapSyncConfig);
            } else {
                JobInfoDTO jobInfoDTO = new JobInfoDTO();
                jobInfoDTO.setJobId(ldapSyncConfig.getSyncJobId());
                ResponseEntity<Void> response = schedulerFeignClient.resumeJob(tenantId, jobInfoDTO);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new CommonException(Constants.ErrorCode.LDAP_SYNC_RESUME_JOB_FAILED);
                }
                updateJobInfo(tenantId, ldapSyncConfig);
            }
        } else if (BaseConstants.Flag.YES.equals(oldConfig.getEnabledFlag())
                        && BaseConstants.Flag.NO.equals(ldapSyncConfig.getEnabledFlag())) {
            // 启用->停用：暂停调度任务
            JobInfoDTO jobInfoDTO = new JobInfoDTO();
            jobInfoDTO.setJobId(ldapSyncConfig.getSyncJobId());
            ResponseEntity<Void> response = schedulerFeignClient.pauseJob(tenantId, jobInfoDTO);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CommonException(Constants.ErrorCode.LDAP_SYNC_PAUSE_JOB_FAILED);
            }
        }
        ldapSyncConfigRepository.updateOptional(ldapSyncConfig, LdapSyncConfig.FIELD_START_DATE,
                        LdapSyncConfig.FIELD_END_DATE, LdapSyncConfig.FIELD_FREQUENCY,
                        LdapSyncConfig.FIELD_ENABLED_FLAG, LdapSyncConfig.FIELD_CUSTOM_FILTER);
        return ldapSyncConfig;
    }

    @Override
    public LdapSyncConfig queryLdapUserConfig(Long tenantId, LdapSyncType ldapSyncType) {
        LdapSyncConfig ldapSyncConfig = new LdapSyncConfig();
        ldapSyncConfig.setTenantId(tenantId);
        ldapSyncConfig.setSyncType(ldapSyncType.value());
        return ldapSyncConfigRepository.selectOne(ldapSyncConfig);
    }

    @Override
    public void syncLdap(Long ldapSyncConfigId) {
        LdapSyncConfig ldapSyncConfig = ldapSyncConfigRepository.selectByPrimaryKey(ldapSyncConfigId);
        Assert.notNull(ldapSyncConfig, BaseConstants.ErrorCode.DATA_INVALID);
        // 验证LDAP
        Ldap ldap = ldapUserService.validateLdap(ldapSyncConfig.getTenantId(), ldapSyncConfig.getLdapId());
        ldap.setCustomFilter(ldapSyncConfig.getCustomFilter());
        // 测试LDAP连接
        Map<String, Object> map = ldapUserService.validateLdapConnection(ldap);
        LdapTemplate ldapTemplate = (LdapTemplate) map.get(LdapConnectServiceImpl.LDAP_TEMPLATE);
        if (LdapSyncType.SYNC_USER.value().equals(ldapSyncConfig.getSyncType())) {
            ldapSyncUserTask.syncLDAPUser(ldapTemplate, ldap, finishFallback, Constants.LdapHistorySyncType.A);
        } else {
            ldapSyncUserTask.syncDisabledLDAPUser(ldapTemplate, ldap, finishFallback, Constants.LdapHistorySyncType.A);
        }
    }

    /**
     * 校验 Ldap 同步配置在租户下是否唯一
     *
     * @param tenantId 租户ID
     * @param ldapSyncType 同步类型
     */
    private void validateUniqueConfig(Long tenantId, String ldapSyncType) {
        LdapSyncConfig temp = new LdapSyncConfig();
        temp.setTenantId(tenantId);
        temp.setSyncType(ldapSyncType);
        int count = ldapSyncConfigRepository.selectCount(temp);
        if (count > 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
    }

    /**
     * 创建调度任务
     *
     * @param ldapSyncConfig LDAP同步设置
     * @return JobInfoDTO
     */
    private JobInfoDTO createJobInfo(LdapSyncConfig ldapSyncConfig) {
        JobInfoDTO jobInfoDTO = getJobInfoDTO(ldapSyncConfig);
        // feign调用创建调度任务
        ResponseEntity<String> response = schedulerFeignClient.createJob(ldapSyncConfig.getTenantId(), jobInfoDTO);
        JobInfoDTO responseJobInfo = ResponseUtils.getResponse(response, JobInfoDTO.class);
        Assert.notNull(responseJobInfo, Constants.ErrorCode.LDAP_SYNC_CREATE_JOB_FAILED);
        return responseJobInfo;
    }

    /**
     * 构建调度任务DTO
     *
     * @param ldapSyncConfig LDAP同步配置
     * @return JobInfoDTO
     */
    private JobInfoDTO getJobInfoDTO(LdapSyncConfig ldapSyncConfig) {
        JobInfoDTO jobInfoDTO = new JobInfoDTO();
        jobInfoDTO.setExecutorId(getExecutorId(ldapSyncConfig.getTenantId()));
        jobInfoDTO.setJobCode(LdapSyncType.SYNC_USER.value().equals(ldapSyncConfig.getSyncType())
                        ? Constants.LdapSyncJobCode.LDAP_SYNC
                        : Constants.LdapSyncJobCode.LDAP_SYNC_LEAVE);
        jobInfoDTO.setJobCron(getLdapSyncCron(ldapSyncConfig));
        jobInfoDTO.setDescription(LdapSyncType.SYNC_USER.value().equals(ldapSyncConfig.getSyncType())
                        ? Constants.LdapSyncJobDescription.LDAP_SYNC
                        : Constants.LdapSyncJobDescription.LDAP_SYNC_LEAVE);
        jobInfoDTO.setExecutorStrategy(Constants.LdapSyncJobParameters.EXECUTOR_STRATEGY);
        jobInfoDTO.setFailStrategy(Constants.LdapSyncJobParameters.FAIL_STRATEGY);
        jobInfoDTO.setGlueType(Constants.LdapSyncJobParameters.GLUE_TYPE);
        jobInfoDTO.setJobHandler(LdapSyncType.SYNC_USER.value().equals(ldapSyncConfig.getSyncType())
                        ? Constants.LdapSyncJobHandler.LDAP_SYNC
                        : Constants.LdapSyncJobHandler.LDAP_SYNC_LEAVE);
        jobInfoDTO.setTenantId(ldapSyncConfig.getTenantId());
        jobInfoDTO.setStartDate(ldapSyncConfig.getStartDate());
        jobInfoDTO.setEndDate(ldapSyncConfig.getEndDate());
        jobInfoDTO.setCycleFlag(BaseConstants.Flag.YES);
        HashMap<String, String> requestParam = new HashMap<>(BaseConstants.Digital.TWO);
        requestParam.put(LdapSyncConfig.FIELD_LDAP_SYNC_CONFIG_ID, ldapSyncConfig.getLdapSyncConfigId().toString());
        try {
            jobInfoDTO.setJobParam(objectMapper.writeValueAsString(requestParam));
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID, e);
        }
        return jobInfoDTO;
    }

    /**
     * 获取当前服务执行器ID
     *
     * @return 执行器ID
     */
    private Long getExecutorId(Long tenantId) {
        final String executorCode = schedulerConfig.getExecutorCode();
        ResponseEntity<String> response = schedulerFeignClient.pageExecutor(tenantId, executorCode);
        List<ExecutorDTO> executorList =
                        ResponseUtils.getResponse(response, new TypeReference<Page<ExecutorDTO>>() {}).getContent();
        Assert.notEmpty(executorList, Constants.ErrorCode.LDAP_SYNC_QUERY_EXECUTOR_FAILED);
        return executorList.get(0).getExecutorId();
    }

    /**
     * 创建调度任务并更新配置JOB ID
     *
     * @param ldapSyncConfig LDAP同步配置
     */
    private void createJobInfoUpdateJobId(LdapSyncConfig ldapSyncConfig) {
        Long jobId = createJobInfo(ldapSyncConfig).getJobId();
        ldapSyncConfig.setSyncJobId(jobId);
        ldapSyncConfigRepository.updateOptional(ldapSyncConfig, LdapSyncConfig.FIELD_SYNC_JOB_ID);
    }

    /**
     * 生成LDAP同步CRON表达式
     *
     * @param ldapSyncConfig LDAP同步配置
     * @return cron
     */
    private String getLdapSyncCron(LdapSyncConfig ldapSyncConfig) {
        final Date startDate = ldapSyncConfig.getStartDate();
        String[] currTime = new SimpleDateFormat(BaseConstants.Pattern.TIME_SS).format(startDate)
                        .split(BaseConstants.Symbol.COLON);
        Assert.isTrue(currTime.length > 2, BaseConstants.ErrorCode.ERROR);
        String cron;
        switch (LdapSyncFrequency.valueOf(ldapSyncConfig.getFrequency())) {
            case DAY:
                cron = String.format(Constants.CRON_FORMAT, currTime[2], currTime[1], currTime[0],
                                BaseConstants.Symbol.STAR, BaseConstants.Symbol.STAR, BaseConstants.Symbol.QUESTION);
                break;
            case WEEK:
                SimpleDateFormat dateFm = new SimpleDateFormat("E", Locale.ENGLISH);
                String currWeek = dateFm.format(startDate);
                cron = String.format(Constants.CRON_FORMAT, currTime[2], currTime[1], currTime[0],
                                BaseConstants.Symbol.QUESTION, BaseConstants.Symbol.STAR, currWeek);
                break;
            case MONTH:
                String[] currDay = new SimpleDateFormat(BaseConstants.Pattern.DATE).format(startDate)
                                .split(BaseConstants.Symbol.MIDDLE_LINE);
                // 大于28号改成月末
                Assert.isTrue(currDay.length > 2, BaseConstants.ErrorCode.ERROR);
                String date = Integer.parseInt(currDay[2]) > 28 ? "L" : currDay[2];
                cron = String.format(Constants.CRON_FORMAT, currTime[2], currTime[1], currTime[0], date,
                                BaseConstants.Symbol.STAR, BaseConstants.Symbol.QUESTION);
                break;
            default:
                throw new CommonException(Constants.ErrorCode.LDAP_SYNC_FREQUENCY_TYPE_WRONG);
        }
        return cron;
    }

    /**
     * 更新调度任务
     *
     * @param tenantId 租户ID
     * @param ldapSyncConfig LDAP同步配置
     */
    private void updateJobInfo(Long tenantId, LdapSyncConfig ldapSyncConfig) {
        final Long jobId = ldapSyncConfig.getSyncJobId();
        ResponseEntity<String> detailResponse = schedulerFeignClient.detailJob(tenantId, jobId);
        JobInfoDTO jobInfoDTO = ResponseUtils.getResponse(detailResponse, JobInfoDTO.class);
        Assert.notNull(jobInfoDTO, Constants.ErrorCode.LDAP_SYNC_QUERY_JOB_FAILED);
        // 调度任务终止 或 调度任务完成，重新创建调度任务
        if (Constants.LdapSyncJobStatus.NONE.equals(jobInfoDTO.getJobStatus())
                        || Constants.LdapSyncJobStatus.COMPLETE.equals(jobInfoDTO.getJobStatus())) {
            createJobInfoUpdateJobId(ldapSyncConfig);
            return;
        }
        // 更新调度任务
        jobInfoDTO.setJobCron(getLdapSyncCron(ldapSyncConfig));
        jobInfoDTO.setStartDate(ldapSyncConfig.getStartDate());
        jobInfoDTO.setEndDate(ldapSyncConfig.getEndDate());
        ResponseEntity<String> updateResponse = schedulerFeignClient.updateJob(tenantId, jobInfoDTO);
        ResponseUtils.getResponse(updateResponse, JobInfoDTO.class);
    }
}
