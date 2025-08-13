package org.hzero.plugin.platform.hr.app.service;

import java.util.Date;

import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统日志应用服务
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
public interface HrSyncLogService {
    
    /**
     * 分页查询同步日志
     * 
     * @param syncId          同步ID
     * @param startDate       同步时间筛选，起始时间
     * @param endDate         同步时间筛选，结束时间
     * @param pageRequest     分页
     * @return 分页同步日志列表
     */
    Page<HrSyncLog> listHrSyncLog(Long syncId,Date startDate,Date endDate,PageRequest pageRequest);

}
