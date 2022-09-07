package org.hzero.platform.app.service.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.helper.RuleEngineHelper;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 规则引擎定时任务
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/01 10:26
 */
@JobHandler("ruleScript")
public class RuleScriptJob implements IJobHandler {

    private static final String SCRIPT_CODE = "scriptCode";
    private static final String PARAMS = "params";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
        String scriptCode = map.get(SCRIPT_CODE);
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Map<String, Object> params = null;
        try {
            if (StringUtils.isNotBlank(map.get(PARAMS))) {
                params = objectMapper.readValue(map.get(PARAMS), new TypeReference<Map<String, Object>>() {
                });
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
        ScriptResult result = RuleEngineHelper.runScript(scriptCode, tenantId, params);
        if (result.getFailed()) {
            tool.error("script run failed!!!");
        } else {
            tool.info("script run success!!!");
        }
        tool.info("script result : " + result.getContent());
        return ReturnT.SUCCESS;
    }
}
