package org.hzero.scheduler.domain.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.api.dto.JobLogQueryDTO;
import org.hzero.scheduler.domain.entity.JobLog;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
public interface JobLogRepository extends BaseRepository<JobLog> {

    /**
     * 分页查询
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页
     * @return 结果
     */
    Page<JobLog> pageLog(JobLogQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询所有
     *
     * @param time        时间
     * @param jobId       任务Id
     * @param tenantId    租户Id
     * @param pageRequest 分页
     * @return 结果
     */
    Page<JobLog> listLog(LocalDate time, Long jobId, Long tenantId, PageRequest pageRequest);

    /**
     * 更新
     *
     * @param jobLogDTO 日志
     */
    void updateLog(JobLogDTO jobLogDTO);

    /**
     * 更新停止任务的状态
     *
     * @param jobId   任务Id
     * @param endTime 结束时间
     */
    void updateLogByJobId(Long jobId, Date endTime);

    /**
     * 更新执行器下线的任务状态
     *
     * @param jobId   任务Id
     * @param endTime 结束时间
     */
    void updateLogOffline(Long jobId, Date endTime);

    /**
     * 更新下线执行器的任务状态
     *
     * @param address       地址
     * @param endTime       结束时间
     * @param messageHeader message
     */
    void updateLogByAddress(String address, Date endTime, String messageHeader);

    /**
     * 批量删除
     *
     * @param logIdList 日志Id集合
     */
    void batchDeleteById(List<Long> logIdList);

    /**
     * 更新两天前的仍在运行中的任务日志
     */
    void updateErrorLog();
}
