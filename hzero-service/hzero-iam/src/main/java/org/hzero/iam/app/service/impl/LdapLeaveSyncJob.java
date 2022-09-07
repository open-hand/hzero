package org.hzero.iam.app.service.impl;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.hzero.iam.domain.service.ldap.LdapSyncConfigDomainService;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LDAP离职用户同步定时任务
 *
 * @author yuqing.zhang@hand-china.com 2020/05/11 13:49
 */
@JobHandler(Constants.LdapSyncJobHandler.LDAP_SYNC_LEAVE)
public class LdapLeaveSyncJob implements IJobHandler {

    @Autowired
    private LdapSyncConfigDomainService ldapSyncConfigDomainService;

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
        try {
            Long ldapSyncConfigId = Long.parseLong(map.get("ldapSyncConfigId"));
            tool.info("ldapLeaveSyncJob starting: ldapSyncConfigId=" + ldapSyncConfigId);
            ldapSyncConfigDomainService.syncLdap(ldapSyncConfigId);
            tool.info("ldapLeaveSyncJob succeed");
        } catch (Exception e) {
            tool.error(ExceptionUtils.getStackTrace(e));
            return ReturnT.FAILURE;
        }
        return ReturnT.SUCCESS;
    }
}
