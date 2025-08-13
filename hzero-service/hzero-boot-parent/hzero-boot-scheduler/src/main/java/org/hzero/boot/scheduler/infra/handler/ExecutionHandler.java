package org.hzero.boot.scheduler.infra.handler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.app.service.JobLogBackService;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/04/30 15:01
 */
public class ExecutionHandler implements RejectedExecutionHandler {

    private static final ThreadLocal<JobDataDTO> JOB_DATA = new ThreadLocal<>();

    public static void setData(JobDataDTO jobDataDTO) {
        JOB_DATA.set(jobDataDTO);
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 任务执行线程不可用
        JobDataDTO data = JOB_DATA.get();
        if (data == null) {
            return;
        }
        JobLogDTO logDTO = new JobLogDTO().setLogId(data.getLogId()).setJobId(data.getJobId());
        ApplicationContextHelper.getContext().getBean(JobLogBackService.class)
                .updateLog(logDTO.setClientResult(BootSchedulerConstant.ClientResult.FAILURE)
                        .setMessageHeader("No Resources").setMessage("Not enough resources to run job."));
        JOB_DATA.remove();
    }
}
