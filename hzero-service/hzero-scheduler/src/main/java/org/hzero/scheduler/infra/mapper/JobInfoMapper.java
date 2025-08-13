package org.hzero.scheduler.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.scheduler.api.dto.JobInfoQueryDTO;
import org.hzero.scheduler.domain.entity.JobInfo;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:57:29
 */
public interface JobInfoMapper extends BaseMapper<JobInfo> {

    /**
     * 任务列表
     *
     * @param jobInfoQueryDTO 查询参数
     * @return 任务列表
     */
    List<JobInfo> listJobInfo(JobInfoQueryDTO jobInfoQueryDTO);

    /**
     * 查询详情
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     * @return 任务明细
     */
    JobInfo detailJobInfo(@Param("jobId") Long jobId,
                          @Param("tenantId") Long tenantId);
}
