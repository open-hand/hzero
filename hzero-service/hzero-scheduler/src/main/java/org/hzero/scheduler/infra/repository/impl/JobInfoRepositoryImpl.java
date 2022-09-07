package org.hzero.scheduler.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.api.dto.JobInfoQueryDTO;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.infra.mapper.JobInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:57:29
 */
@Component
public class JobInfoRepositoryImpl extends BaseRepositoryImpl<JobInfo> implements JobInfoRepository {

    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Override
    public Page<JobInfo> pageJobInfo(JobInfoQueryDTO jobInfoQueryDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> jobInfoMapper.listJobInfo(jobInfoQueryDTO));
    }

    @Override
    public JobInfo detailJobInfo(Long jobId, Long tenantId) {
        return jobInfoMapper.detailJobInfo(jobId, tenantId);
    }
}
