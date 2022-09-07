package org.hzero.scheduler.infra.mapper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.scheduler.api.dto.JobLogQueryDTO;
import org.hzero.scheduler.domain.entity.JobLog;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
public interface JobLogMapper extends BaseMapper<JobLog> {


    /**
     * 集合查询
     *
     * @param queryDTO 查询条件
     * @return 结果
     */
    List<JobLog> listLog(JobLogQueryDTO queryDTO);

    /**
     * 根据时间查询
     *
     * @param time     时间
     * @param jobId    任务Id
     * @param tenantId 租户Id
     * @return 结果
     */
    List<JobLog> listLogByTime(@Param("time") LocalDate time,
                               @Param("jobId") Long jobId,
                               @Param("tenantId") Long tenantId);

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
    void updateLogByJobId(@Param("jobId") Long jobId,
                          @Param("endTime") Date endTime);

    /**
     * 更新执行器下线的任务状态
     *
     * @param jobId   任务Id
     * @param endTime 结束时间
     */
    void updateLogOffline(@Param("jobId") Long jobId,
                          @Param("endTime") Date endTime);

    /**
     * 更新下线执行器的任务状态
     *
     * @param address       地址
     * @param endTime       结束时间
     * @param messageHeader 消息
     */
    void updateLogByAddress(@Param("address") String address,
                            @Param("endTime") Date endTime,
                            @Param("messageHeader") String messageHeader);

    /**
     * 批量删除
     *
     * @param logIdList 日志Id
     */
    void batchDeleteById(@Param("logIdList") List<Long> logIdList);

    /**
     * 更新两天前的仍在运行中的任务日志
     *
     * @param now  现在时间
     * @param time 需要更新的时间
     */
    void updateErrorLog(@Param("now") Date now,
                        @Param("time") Date time);
}
