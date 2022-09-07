package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.api.dto.SyncEmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统日志Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
public interface HrSyncLogMapper extends BaseMapper<HrSyncLog> {
    
    List<HrSyncLog> queryAll(@Param("organizationId") Long organizationId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 查询当前租户组织架构里的员工
     *
     * @param tenantId   租户Id
     * @return           放回同步对象
     */
    List<SyncEmployeeDTO> queryAllEmployees(@Param("tenantId") Long tenantId);
    
    int updateLog(HrSyncLog hrSyncLog);
    
    /**
     * 分页查询同步日志
     * 
     * @param syncId          同步ID
     * @param startDate       同步时间筛选，起始时间
     * @param endDate         同步时间筛选，结束时间
     * @return 分页同步日志列表
     */
    Page<HrSyncLog> selectHrSyncLog(@Param("syncId") Long syncId,
                                  @Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);

}
