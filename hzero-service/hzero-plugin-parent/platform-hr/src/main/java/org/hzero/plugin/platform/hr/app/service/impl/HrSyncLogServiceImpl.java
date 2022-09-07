package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.Date;

import org.hzero.plugin.platform.hr.app.service.HrSyncLogService;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
/**
 * hr基础数据同步外部系统日志应用服务默认实现
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@Service
public class HrSyncLogServiceImpl implements HrSyncLogService {

    @Autowired
    private HrSyncLogRepository hrSyncLongRepository;
    
    @Override
    public Page<HrSyncLog> listHrSyncLog(Long syncId, Date startDate, Date endDate, PageRequest pageRequest) {
        return hrSyncLongRepository.listHrSyncLog(syncId, startDate, endDate, pageRequest);
    }

}
