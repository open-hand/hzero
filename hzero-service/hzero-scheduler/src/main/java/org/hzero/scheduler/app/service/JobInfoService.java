package org.hzero.scheduler.app.service;

import java.util.List;

import org.hzero.boot.scheduler.api.dto.ChildJobDTO;
import org.hzero.scheduler.api.dto.JobInfoQueryDTO;
import org.hzero.scheduler.domain.entity.JobInfo;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:57:29
 */
public interface JobInfoService {

    /**
     * 任务列表
     *
     * @param jobInfoQueryDTO 查询参数
     * @param pageRequest     分页
     * @return 任务分页
     */
    Page<JobInfo> pageJobInfo(JobInfoQueryDTO jobInfoQueryDTO, PageRequest pageRequest);

    /**
     * 任务明细
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     * @return 任务明细
     */
    JobInfo detailJobInfo(Long jobId, Long tenantId);

    /**
     * 创建任务
     *
     * @param jobInfo 任务信息
     * @return 新建的任务
     */
    JobInfo createJob(JobInfo jobInfo);

    /**
     * 创建子任务
     *
     * @param childJobDTO 子任务
     */
    void createChildJob(ChildJobDTO childJobDTO);

    /**
     * 更新任务
     *
     * @param jobInfo 任务信息
     * @return 修改的任务
     */
    JobInfo updateJob(JobInfo jobInfo);

    /**
     * 清理策略参数
     *
     * @param executorStrategy 执行器策略
     * @param strategyParam    策略参数
     * @return 策略参数
     */
    String clearStrategyParam(String executorStrategy, String strategyParam);

    /**
     * 删除任务
     *
     * @param jobInfoList 任务信息
     */
    void deleteJob(List<JobInfo> jobInfoList);

    /**
     * 停止客户端任务
     *
     * @param jobInfo  任务信息
     * @param jwtToken jwt
     */
    void stopClient(JobInfo jobInfo, String jwtToken);

    /**
     * 暂停客户端任务
     *
     * @param jobInfo  任务信息
     * @param jwtToken jwt
     */
    void pauseClient(JobInfo jobInfo, String jwtToken);

    /**
     * 初始化任务
     */
    void initJob();
}
