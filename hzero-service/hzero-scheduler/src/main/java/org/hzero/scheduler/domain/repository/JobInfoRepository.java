package org.hzero.scheduler.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.api.dto.JobInfoQueryDTO;
import org.hzero.scheduler.domain.entity.JobInfo;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:57:29
 */
public interface JobInfoRepository extends BaseRepository<JobInfo> {

    /**
     * 任务列表
     *
     * @param jobInfoQueryDTO 查询参数
     * @param pageRequest     分页
     * @return 任务分页
     */
    Page<JobInfo> pageJobInfo(JobInfoQueryDTO jobInfoQueryDTO, PageRequest pageRequest);

    /**
     * 查询详情
     *
     * @param jobId    任务Id
     * @param tenantId 租户Id
     * @return 任务明细
     */
    JobInfo detailJobInfo(Long jobId, Long tenantId);
}
