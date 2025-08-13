package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.hzero.plugin.platform.hr.api.controller.HrSyncController;
import org.hzero.plugin.platform.hr.domain.entity.HrSync;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 规则引擎定时任务
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/01 10:26
 */
@JobHandler("hrSyncJob")
public class HrSyncJob implements IJobHandler {

    @Autowired
    private HrSyncController hrSyncController;

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
        HrSync hrSync = new HrSync();
        try {
            hrSync.setSyncId(Long.parseLong(map.get("syncId")));
            Long tenantId = Long.parseLong(map.get("tenantId"));
            String mapParam = map.get("useGeneratedUnitId");
            boolean useGeneratedUnitId;
            if (StringUtils.isBlank(mapParam)) {
                useGeneratedUnitId = true;
            } else {
                useGeneratedUnitId = Boolean.parseBoolean(mapParam);
            }
            hrSyncController.syncNow(tenantId,hrSync,useGeneratedUnitId);
        } catch (Exception e) {
            tool.error(ExceptionUtils.getStackTrace(e));
            return ReturnT.FAILURE;
        }

        return ReturnT.SUCCESS;
    }
}
