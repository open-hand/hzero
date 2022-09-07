package org.hzero.platform.app.service;

import java.time.LocalDate;
import java.util.List;

import org.hzero.platform.api.dto.AuditLoginExportForOrg;
import org.hzero.platform.api.dto.AuditLoginExportForSite;
import org.hzero.platform.domain.entity.AuditLogin;

/**
 * 登录日志审计应用服务
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
public interface AuditLoginService {
    /**
     * 平台级导出导出
     *
     * @param auditLogin 过滤信息
     * @return 结果集
     */
    List<AuditLoginExportForSite> exportForSite(AuditLogin auditLogin);

    /**
     * 租户级导出导出
     *
     * @param auditLogin 过滤信息
     * @return 结果集
     */
    List<AuditLoginExportForOrg> exportForOrg(AuditLogin auditLogin);

    /**
     * 清理日志
     *
     * @param clearType        清理类型
     * @param tenantId   租户Id
     */
    void clearLog(String clearType, Long tenantId);

    /**
     * 异步清理日志
     *
     * @param localDate 时间
     * @param tenantId  租户Id
     */
    void asyncClearLog(LocalDate localDate, Long tenantId);
}
