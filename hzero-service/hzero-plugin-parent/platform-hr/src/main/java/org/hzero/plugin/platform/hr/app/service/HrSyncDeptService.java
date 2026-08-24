package org.hzero.plugin.platform.hr.app.service;

import java.io.File;
import java.util.List;

import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;

/**
 * HR部门数据同步应用服务
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
public interface HrSyncDeptService {

    /**
     * 部门增量同步对象
     *
     * @param syncTypeCode 同步类型
     * @param tenantId     租户id
     * @return
     */
    List<HrSyncDept> getSyncDept(String syncTypeCode, Long tenantId);

    /**
     * 更新本地第三方组织结构副本
     *
     * @param hrSyncDeptList 部门列表
     */
    void updateSyncDept(List<HrSyncDept> hrSyncDeptList);

    /**
     * 同步部门到本地
     *
     * @param hrSyncDeptList 第三方部门
     * @param tenantId       租户ID
     * @param syncTypeCode   同步类型
     * @param log            日志
     */
    void syncDeptToLocal(List<HrSyncDept> hrSyncDeptList, Long tenantId, String syncTypeCode, StringBuilder log);

    /**
     * 禁用部门
     *
     * @param hrSyncDeptList 同步部门列表
     * @param tenantId       租户ID
     * @param syncTypeCode   同步类型
     * @param log            日志
     */
    void disableSyncDept(List<HrSyncDept> hrSyncDeptList, Long tenantId, String syncTypeCode, StringBuilder log);
}
