package org.hzero.scheduler.app.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.scheduler.api.dto.JobProgress;
import org.hzero.boot.scheduler.infra.tool.ProgressCache;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.common.Criteria;
import org.hzero.scheduler.api.dto.JobLogQueryDTO;
import org.hzero.scheduler.app.service.JobLogService;
import org.hzero.scheduler.domain.entity.JobLog;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
@Service
public class JobLogServiceImpl implements JobLogService {


    private final FileClient fileClient;
    private final RedisHelper redisHelper;
    private final ProgressCache progressCache;
    private final JobLogRepository jobLogRepository;

    @Autowired
    public JobLogServiceImpl(FileClient fileClient,
                             RedisHelper redisHelper,
                             ProgressCache progressCache,
                             JobLogRepository jobLogRepository) {
        this.fileClient = fileClient;
        this.redisHelper = redisHelper;
        this.progressCache = progressCache;
        this.jobLogRepository = jobLogRepository;
    }

    @Override
    public Page<JobLog> pageLog(Long tenantId, Long jobId, String jobResult, String clientResult, Date timeStart, Date timeEnd, PageRequest pageRequest) {
        JobLogQueryDTO jobLogQueryDTO = new JobLogQueryDTO()
                .setTenantId(tenantId)
                .setJobId(jobId)
                .setJobResult(jobResult)
                .setClientResult(clientResult)
                .setTimeStart(timeStart)
                .setTimeEnd(timeEnd);
        return jobLogRepository.pageLog(jobLogQueryDTO, pageRequest);
    }

    @Override
    public JobProgress jobProgress(Long logId, Long tenantId) {
        JobLog jobLog = jobLogRepository.selectOne(new JobLog().setLogId(logId).setTenantId(tenantId));
        Assert.notNull(jobLog, BaseConstants.ErrorCode.NOT_FOUND);
        switch (jobLog.getClientResult()) {
            case HsdrConstant.ClientResult.SUCCESS:
                return new JobProgress().setMessage("Job completed").setProgress(HsdrConstant.HUNDRED);
            case HsdrConstant.ClientResult.DOING:
                return Optional.ofNullable(progressCache.getCache(logId, redisHelper)).orElse(new JobProgress().setMessage("Job completed").setProgress(HsdrConstant.HUNDRED));
            default:
                return new JobProgress().setProgress(HsdrConstant.HUNDRED);
        }
    }

    @Override
    public String errorDetail(Long logId, Long tenantId) {
        JobLog jobLog = jobLogRepository.selectByPrimaryKey(logId);
        if (jobLog != null) {
            String message = jobLog.getMessage();
            if (StringUtils.isBlank(message)) {
                message = jobLog.getMessageHeader();
            }
            return message;
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String logDetail(Long logId, Long tenantId) {
        JobLog jobLog = jobLogRepository.selectOneOptional(new JobLog().setLogId(logId).setTenantId(tenantId), new Criteria().select(JobLog.FIELD_LOG_MESSAGE));
        if (jobLog != null) {
            return jobLog.getLogMessage();
        }
        return StringUtils.EMPTY;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByLogId(Long logId) {
        // 先删除文件服务存储的日志文件
        JobLog jobLog = jobLogRepository.selectByPrimaryKey(logId);
        if (jobLog == null) {
            return;
        }
        List<String> urlList = new ArrayList<>();
        if (StringUtils.isNotBlank(jobLog.getLogUrl())) {
            urlList.add(jobLog.getLogUrl());
            fileClient.deleteFileByUrl(jobLog.getTenantId(), HZeroService.Scheduler.BUCKET_NAME, urlList);
        }
        jobLogRepository.deleteByPrimaryKey(logId);
    }

    @Override
    public void clearLog(String clearType, Long jobId, Long tenantId) {
        LocalDate now = LocalDate.now();
        JobLogService service = ApplicationContextHelper.getContext().getBean(JobLogService.class);
        switch (clearType) {
            case HsdrConstant.ClearLogType.ONE_DAY:
                service.asyncClearLog(now.minus(1, ChronoUnit.DAYS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.THREE_DAY:
                service.asyncClearLog(now.minus(3, ChronoUnit.DAYS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.ONE_WEEK:
                service.asyncClearLog(now.minus(1, ChronoUnit.WEEKS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.ONE_MONTH:
                service.asyncClearLog(now.minus(1, ChronoUnit.MONTHS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.THREE_MONTH:
                service.asyncClearLog(now.minus(3, ChronoUnit.MONTHS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.SIX_MONTH:
                service.asyncClearLog(now.minus(6, ChronoUnit.MONTHS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.ONE_YEAR:
                service.asyncClearLog(now.minus(1, ChronoUnit.YEARS), jobId, tenantId);
                break;
            case HsdrConstant.ClearLogType.ALL:
                service.asyncClearLog(null, jobId, tenantId);
                break;
            default:
                break;
        }
    }

    @Async("commonAsyncTaskExecutor")
    @Override
    public void asyncClearLog(LocalDate localDate, Long jobId, Long tenantId) {
        PageRequest pageRequest = new PageRequest(0, 1000);
        List<JobLog> list;
        while (true) {
            list = jobLogRepository.listLog(localDate, jobId, tenantId, pageRequest).getContent();
            if (CollectionUtils.isNotEmpty(list)) {
                // 删除日志文件
                List<String> urlList = list.stream().filter(item -> StringUtils.isNotBlank(item.getLogUrl())).map(JobLog::getLogUrl).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(urlList)) {
                    fileClient.deleteFileByUrl(tenantId, HZeroService.Scheduler.BUCKET_NAME, urlList);
                }
                // 删除日志记录
                jobLogRepository.batchDeleteById(list.stream().map(JobLog::getLogId).collect(Collectors.toList()));
                list.clear();
            } else {
                break;
            }
        }
    }
}