package org.hzero.report.app.service;

import org.hzero.report.domain.entity.ReportPermission;

/**
 * 报表权限应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
public interface ReportPermissionService {

    /**
     * 创建报表权限
     *
     * @param reportPermission 权限
     */
    void createReportPermission(ReportPermission reportPermission);

    /**
     * 更新报表权限
     *
     * @param reportPermission 权限
     * @return 更新的权限
     */
    ReportPermission updateReportPermission(ReportPermission reportPermission);

    /**
     * 删除报表权限
     *
     * @param reportPermissionId 报表权限ID
     */
    void removeReportPermission(Long reportPermissionId);

}
