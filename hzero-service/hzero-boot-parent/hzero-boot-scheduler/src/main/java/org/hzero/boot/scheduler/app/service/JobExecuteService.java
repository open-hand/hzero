package org.hzero.boot.scheduler.app.service;

import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.springframework.security.core.context.SecurityContext;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 14:21
 */
public interface JobExecuteService {

    /**
     * 执行任务
     *
     * @param jobDataDTO 数据
     * @param context    授权信息
     */
    void jobExecute(JobDataDTO jobDataDTO, SecurityContext context);

    /**
     * 停止任务
     *
     * @param jobId 任务Id
     * @return 结果
     */
    String stopJob(Long jobId);

    /**
     * 暂停任务
     *
     * @param jobId 任务Id
     * @return 结果
     */
    String pauseJob(Long jobId);
}
