package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.Date;
import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.plugin.platform.hr.api.dto.SyncEmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncLogRepository;
import org.hzero.plugin.platform.hr.infra.mapper.HrSyncLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统日志 资源库实现
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@Component
public class HrSyncLogRepositoryImpl extends BaseRepositoryImpl<HrSyncLog> implements HrSyncLogRepository {

    @Autowired
    private HrSyncLogMapper hrSyncLogMapper;
    
    @Override
    public List<HrSyncLog> queryAll(Long organizationId, Date startDate, Date endDate) {
        return hrSyncLogMapper.queryAll(organizationId, startDate, endDate);
    }

    @Override
    public List<SyncEmployeeDTO> queryAllEmployees(Long tenantId) {
        return hrSyncLogMapper.queryAllEmployees(tenantId);
    }

    @Override
    public int updateLog(HrSyncLog hrSyncLog) {
        return hrSyncLogMapper.updateLog(hrSyncLog);
    }

    @Override
    public Page<HrSyncLog> listHrSyncLog(Long syncId, Date startDate, Date endDate, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> hrSyncLogMapper.selectHrSyncLog(syncId, startDate, endDate));
    }

}
