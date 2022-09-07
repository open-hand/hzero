package org.hzero.scheduler.infra.repository.impl;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.api.dto.JobLogQueryDTO;
import org.hzero.scheduler.domain.entity.JobLog;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.infra.mapper.JobLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
@Component
public class JobLogRepositoryImpl extends BaseRepositoryImpl<JobLog> implements JobLogRepository {

    private final JobLogMapper jobLogMapper;

    @Autowired
    public JobLogRepositoryImpl(JobLogMapper jobLogMapper) {
        this.jobLogMapper = jobLogMapper;
    }

    @Override
    public Page<JobLog> pageLog(JobLogQueryDTO queryDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> jobLogMapper.listLog(queryDTO));
    }

    @Override
    public Page<JobLog> listLog(LocalDate time, Long jobId, Long tenantId, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> jobLogMapper.listLogByTime(time, jobId, tenantId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLog(JobLogDTO jobLogDTO) {
        jobLogMapper.updateLog(jobLogDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLogByJobId(Long jobId, Date endTime) {
        jobLogMapper.updateLogByJobId(jobId, endTime);
    }

    @Override
    public void updateLogOffline(Long jobId, Date endTime) {
        jobLogMapper.updateLogOffline(jobId, endTime);
    }

    @Override
    public void updateLogByAddress(String address, Date endTime, String messageHeader) {
        jobLogMapper.updateLogByAddress(address, endTime, messageHeader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteById(List<Long> logIdList) {
        jobLogMapper.batchDeleteById(logIdList);
    }

    @Override
    public void updateErrorLog() {
        Date date = new Date();
        jobLogMapper.updateErrorLog(date, getDateBefore(date));
    }

    private static Date getDateBefore(Date d) {
        int day = 2;
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }
}
