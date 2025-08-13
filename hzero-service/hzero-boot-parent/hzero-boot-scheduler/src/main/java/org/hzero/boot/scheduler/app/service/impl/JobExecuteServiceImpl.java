package org.hzero.boot.scheduler.app.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.api.dto.JobProgress;
import org.hzero.boot.scheduler.app.service.JobExecuteService;
import org.hzero.boot.scheduler.app.service.JobLogBackService;
import org.hzero.boot.scheduler.configure.SchedulerConfig;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.ExecutionHandler;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.handler.IJobHandlerAllowStop;
import org.hzero.boot.scheduler.infra.registry.JobRegistry;
import org.hzero.boot.scheduler.infra.registry.ThreadRegistry;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.hzero.boot.scheduler.infra.util.ExceptionUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 任务执行
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 14:22
 */
@Service
public class JobExecuteServiceImpl implements JobExecuteService {

    private static final Logger logger = LoggerFactory.getLogger(JobExecuteServiceImpl.class);

    private final JobLogBackService jobLogBackService;
    private final FileClient fileClient;
    private final ObjectMapper objectMapper;
    private final SchedulerConfig schedulerConfig;
    private final ExecutorService executor;

    @Autowired
    public JobExecuteServiceImpl(JobLogBackService jobLogBackService,
                                 FileClient fileClient,
                                 ObjectMapper objectMapper,
                                 SchedulerConfig schedulerConfig,
                                 @Qualifier("jobAsyncExecutor") ExecutorService executor) {
        this.jobLogBackService = jobLogBackService;
        this.fileClient = fileClient;
        this.objectMapper = objectMapper;
        this.schedulerConfig = schedulerConfig;
        this.executor = executor;
    }

    @Override
    public void jobExecute(JobDataDTO jobDataDTO, SecurityContext context) {
        ExecutionHandler.setData(jobDataDTO);
        executor.execute(() -> {
            // 写入授权信息
            SecurityContextHolder.setContext(context);
            try {

                String jobHandler = jobDataDTO.getJobHandler();
                Object handler = JobRegistry.getJobHandler(jobHandler);
                Long logId = jobDataDTO.getLogId();
                Long jobId = jobDataDTO.getJobId();
                JobLogDTO logDTO = new JobLogDTO().setLogId(logId).setJobId(jobId);
                if (handler == null) {
                    // 查找任务失败
                    jobLogBackService.updateLog(logDTO.setClientResult(BootSchedulerConstant.ClientResult.FAILURE)
                            .setMessageHeader("No jobHandler").setMessage("The same jobHandler was not found."));
                    return;
                }
                IJobHandler iJobHandler = null;
                Thread thread = Thread.currentThread();
                SchedulerTool tool = new SchedulerTool(HZeroService.Scheduler.REDIS_DB, logId, jobDataDTO);
                try {
                    if (handler instanceof IJobHandlerAllowStop) {
                        iJobHandler = (IJobHandlerAllowStop) handler;
                        // 缓存中写入线程和iJobHandler
                        ThreadRegistry.addJobHandler(thread.getId(), iJobHandler);
                        ThreadRegistry.addThread(thread, jobId);
                    } else {
                        iJobHandler = (IJobHandler) handler;
                    }
                    jobRun(iJobHandler, jobDataDTO, logDTO, tool);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getMessage(e));
                    // 任务执行出现异常时执行
                    if (jobHandler != null) {
                        iJobHandler.onException(tool);
                    }
                    JobProgress progress = tool.getJobProgress();
                    jobLogBackService.updateLog(logDTO.setClientResult(BootSchedulerConstant.ClientResult.FAILURE)
                            .setMessageHeader(e.getMessage())
                            .setMessage("Job progress:" + progress.getProgress() + "%, message: " + progress.getMessage() + "\n" + ExceptionUtils.getMessage(e)));
                } finally {
                    // 关闭日志
                    tool.closeLogger();
                    // 清除线程以及iJobHandler的缓存
                    ThreadRegistry.deleteThread(thread);
                    ThreadRegistry.deleteJobHandler(thread.getId());
                }
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
    }

    private void jobRun(IJobHandler iJobHandler, JobDataDTO jobDataDTO, JobLogDTO logDTO, SchedulerTool tool) throws Exception {
        String url = null;
        // 初始化任务进度
        tool.updateProgress(BaseConstants.Digital.ZERO, "Job init.");
        // 任务开始前执行
        iJobHandler.onCreate(tool);
        Map<String, String> map = StringUtils.isNotBlank(jobDataDTO.getParam()) ?
                objectMapper.readValue(jobDataDTO.getParam(), new TypeReference<Map<String, String>>() {
                }) : new HashMap<>(BaseConstants.Digital.SIXTEEN);
        ReturnT result = iJobHandler.execute(map, tool);
        // 清除进度缓存
        tool.clearProgress(jobDataDTO.getLogId());
        if (tool.getByteArrayOutputStream().size() > BaseConstants.Digital.ZERO) {
            byte[] log = tool.getByteArrayOutputStream().toByteArray();
            if (schedulerConfig.isUploadLog()) {
                String suffix = StringUtils.isNotBlank(tool.getFileSuffix()) ? tool.getFileSuffix() : ".txt";
                // 上传文件
                try {
                    url = fileClient.uploadFile(jobDataDTO.getTenantId(), HZeroService.Scheduler.BUCKET_NAME, BootSchedulerConstant.LOG_DIR,
                            jobDataDTO.getJobCode() + jobDataDTO.getRunTime() + suffix, null, log);
                } catch (Exception e) {
                    logger.warn("file upload failed.");
                    // feign调用失败，日志记录到数据库，但只记录2000长度
                    logDTO.setLogMessage(new String(log, StandardCharsets.UTF_8));
                }
            } else {
                logDTO.setLogMessage(new String(log, StandardCharsets.UTF_8));
            }
        }
        // 任务正常执行结束后执行
        iJobHandler.onFinish(tool, result);
        logDTO.setOutputFile(tool.getOutputFile());
        switch (result) {
            case SUCCESS:
                jobLogBackService.updateLog(logDTO.setClientResult(BootSchedulerConstant.ClientResult.SUCCESS).setLogUrl(url));
                break;
            case FAILURE:
                jobLogBackService.updateLog(logDTO.setClientResult(BootSchedulerConstant.ClientResult.WARNING).setLogUrl(url));
                break;
            default:
                jobLogBackService.updateLog(logDTO.setClientResult(BootSchedulerConstant.ClientResult.FAILURE)
                        .setMessageHeader("empty.").setMessage("empty Result.").setLogUrl(url));
                break;
        }
    }

    @Override
    public String stopJob(Long jobId) {
        List<Thread> list = ThreadRegistry.getThread(jobId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Thread thread : list) {
                IJobHandlerAllowStop iJobHandler = getHandler(thread);
                if (iJobHandler == null) {
                    continue;
                }
                if (iJobHandler.allowStop()) {
                    ThreadRegistry.deleteThread(thread);
                    ThreadRegistry.deleteJobHandler(thread.getId());
                    // 任务停止补偿方法
                    iJobHandler.onStop();
                    iJobHandler.onStop(thread);
                } else {
                    // 有一个线程不可终止，任务就不可终止
                    return BootSchedulerConstant.Response.FAILURE;
                }
            }
        }
        return BootSchedulerConstant.Response.SUCCESS;
    }

    private IJobHandlerAllowStop getHandler(Thread thread) {
        if (!thread.isAlive()) {
            // 清除内存中的无用数据
            ThreadRegistry.deleteThread(thread);
            ThreadRegistry.deleteJobHandler(thread.getId());
        }
        Object handler = ThreadRegistry.getJobHandler(thread.getId());
        if (handler instanceof IJobHandlerAllowStop) {
            return (IJobHandlerAllowStop) handler;
        } else {
            return null;
        }
    }

    @Override
    public String pauseJob(Long jobId) {
        List<Thread> list = ThreadRegistry.getThread(jobId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Thread thread : list) {
                IJobHandlerAllowStop iJobHandler = getHandler(thread);
                if (iJobHandler == null) {
                    continue;
                }
                if (iJobHandler.allowStop()) {
                    ThreadRegistry.deleteThread(thread);
                    ThreadRegistry.deleteJobHandler(thread.getId());
                    // 任务暂停补偿方法
                    iJobHandler.onPause();
                    iJobHandler.onPause(thread);
                } else {
                    // 有一个线程不可终止，任务就不可终止
                    return BootSchedulerConstant.Response.FAILURE;
                }
            }
        }
        return BootSchedulerConstant.Response.SUCCESS;
    }
}
