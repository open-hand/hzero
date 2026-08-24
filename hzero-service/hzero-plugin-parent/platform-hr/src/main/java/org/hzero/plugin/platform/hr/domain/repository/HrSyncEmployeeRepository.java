package org.hzero.plugin.platform.hr.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncEmployee;

/**
 * HR员工数据同步资源库
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
public interface HrSyncEmployeeRepository extends BaseRepository<HrSyncEmployee> {
    /**
     * 查询同步时需要新增的员工
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncEmployee> getCreateEmployee(String syncTypeCode, Long tenantId);
    /**
     * 查询同步时需要删除的员工
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncEmployee> getDeleteEmployee(String syncTypeCode, Long tenantId);
    /**
     * 查询同步时需要更新的员工
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncEmployee> getUpdateEmployee( String syncTypeCode, Long tenantId);
}
