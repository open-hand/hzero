package org.hzero.plugin.platform.hr.app.service;

import java.io.File;
import java.util.List;

import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncEmployee;

/**
 * HR员工数据同步应用服务
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
public interface HrSyncEmployeeService {

    /**
     * 员工增量同步对象
     *
     * @param syncTypeCode 同步类型
     * @param tenantId     租户id
     * @return
     */
    List<HrSyncEmployee> getSyncEmployee(String syncTypeCode, Long tenantId);

    /**
     * 更新本地的三方组织结构副本
     *
     * @param hrSyncEmployees 需要更新的员工对象
     */
    void updateSyncEmployee(List<HrSyncEmployee> hrSyncEmployees);

    /**
     * 同步部门下的员工
     *
     * @param hrSyncDept         部门
     * @param hrSyncEmployeeList 部门下的员工
     * @param tenantId           租户ID
     * @param syncTypeCode       同步类型
     * @param log                日志
     */
    void syncEmployee(HrSyncDept hrSyncDept, List<HrSyncEmployee> hrSyncEmployeeList, Long tenantId, String syncTypeCode, StringBuilder log);
}
