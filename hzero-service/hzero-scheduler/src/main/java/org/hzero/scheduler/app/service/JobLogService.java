package org.hzero.scheduler.app.service;

import java.time.LocalDate;
import java.util.Date;

import org.hzero.boot.scheduler.api.dto.JobProgress;
import org.hzero.scheduler.domain.entity.JobLog;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
public interface JobLogService {

    /**
     * 分页查询
     *
     * @param tenantId     租户Id
     * @param jobId        任务Id
     * @param jobResult    调度结果
     * @param clientResult 执行结果
     * @param timeStart    开始时间从
     * @param timeEnd      开始时间至
     * @param pageRequest  分页
     * @return 结果
     */
    Page<JobLog> pageLog(Long tenantId, Long jobId, String jobResult, String clientResult, Date timeStart, Date timeEnd, PageRequest pageRequest);

    /**
     * 查询任务进度
     *
     * @param logId    日志Id
     * @param tenantId 租户Id
     * @return 任务进度
     */
    JobProgress jobProgress(Long logId, Long tenantId);

    /**
     * 错误日志详情
     *
     * @param logId    日志Id
     * @param tenantId 租户Id
     * @return 日志
     */
    String errorDetail(Long logId, Long tenantId);

    /**
     * 任务日志详情
     *
     * @param logId    日志Id
     * @param tenantId 租户Id
     * @return 日志
     */
    String logDetail(Long logId, Long tenantId);

    /**
     * 根据日志Id删除
     *
     * @param logId 日志Id
     */
    void deleteByLogId(Long logId);

    /**
     * 清理日志
     *
     * @param clearType 清理类型
     * @param jobId     任务Id
     * @param tenantId  租户Id
     */
    void clearLog(String clearType, Long jobId, Long tenantId);

    /**
     * 异步清理日志
     *
     * @param localDate 时间
     * @param jobId     任务Id
     * @param tenantId  租户Id
     */
    void asyncClearLog(LocalDate localDate, Long jobId, Long tenantId);
}
