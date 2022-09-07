package org.hzero.scheduler.domain.service;

import java.util.List;

import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.scheduler.domain.entity.JobInfo;

/**
 * 任务操作接口
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/08 14:42
 */
public interface IJobService {

    /**
     * 获取触发器状态
     *
     * @param jobStatus 任务状态
     * @return 任务Id
     */
    List<String> getTriggerStatus(String jobStatus);

    /**
     * 获取任务状态
     *
     * @param triggerStatus 触发器状态
     * @return 任务状态
     */
    String getJobStatus(String triggerStatus);

    /**
     * 获取任务状态
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     * @return 任务状态
     */
    String getJobStatus(Long jobId, Long tenantId);

    /**
     * 添加任务
     *
     * @param jobInfo 任务参数
     */
    void addJob(JobInfo jobInfo);

    /**
     * 更新任务
     *
     * @param jobInfo 任务参数
     */
    void updateJob(JobInfo jobInfo);

    /**
     * 删除任务
     *
     * @param jobId      任务ID
     * @param executorId 执行器Id
     * @param tenantId   租户Id
     */
    void removeJob(Long jobId, Long executorId, Long tenantId);

    /**
     * 立即执行
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     */
    void trigger(Long jobId, Long tenantId);

    /**
     * 暂停任务
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     */
    void pauseJob(Long jobId, Long tenantId);

    /**
     * 恢复任务
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     */
    void resumeJob(Long jobId, Long tenantId);

    /**
     * 发送告警邮件
     *
     * @param logId 日志Id
     */
    void sendEmail(Long logId);

    /**
     * 客户端日志回写
     *
     * @param jobLogDTO 日志
     */
    void callback(JobLogDTO jobLogDTO);
}
