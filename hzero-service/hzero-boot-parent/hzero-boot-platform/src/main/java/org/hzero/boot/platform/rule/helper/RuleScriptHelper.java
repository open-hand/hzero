package org.hzero.boot.platform.rule.helper;

import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.rule.constant.RuleConstants;
import org.hzero.boot.platform.rule.entity.RuleScriptVO;
import org.hzero.boot.platform.rule.feign.RemoteRuleScriptService;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 获取规则引擎
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/09 11:50
 */
@Component
public class RuleScriptHelper {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RemoteRuleScriptService remoteRuleScriptService;

    public RuleScriptVO getRuleScript(Long tenantId, String scriptCode) {
        // 租户获取不到，再获取平台的
        RuleScriptVO ruleScript;
        // 查询租户配置
        ruleScript = getRuleScript(redisHelper, tenantId, scriptCode);
        if (ruleScript == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
            // 获取平台配置
            ruleScript = getRuleScript(redisHelper, BaseConstants.DEFAULT_TENANT_ID, scriptCode);
        }
        Assert.notNull(ruleScript, RuleConstants.ErrorCode.RULE_SCRIPT_GET_RULE_FAILED);
        return ruleScript;
    }

    private RuleScriptVO getRuleScript(RedisHelper redis, Long tenantId, String scriptCode) {
        // 查询缓存
        RuleScriptVO ruleScript = null;
        // 切换redis
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String redisData = getCache(redis, tenantId, scriptCode);
        // 重置redis
        redisHelper.clearCurrentDatabase();
        if (StringUtils.isNotBlank(redisData)) {
            if (Objects.equals(redisData, "error")) {
                return null;
            } else {
                ruleScript = redis.fromJson(redisData, RuleScriptVO.class);
            }
        }
        if (ruleScript == null) {
            // feign调用查数据库
            ruleScript = remoteGetRuleScript(tenantId, scriptCode);
        }
        // 判断是否被禁用
        if (ruleScript != null && Objects.equals(ruleScript.getEnabledFlag(), BaseConstants.Flag.NO)) {
            ruleScript = null;
        }
        return ruleScript;
    }

    /**
     * 远程获取规则引擎
     *
     * @param tenantId   租户Id
     * @param scriptCode 脚本编码编码
     * @return 规则引擎
     */
    private RuleScriptVO remoteGetRuleScript(Long tenantId, String scriptCode) {
        String result = remoteRuleScriptService.selectRuleScriptByCode(scriptCode, tenantId).getBody();
        if (StringUtils.isBlank(result)) {
            return null;
        }
        try {
            return objectMapper.readValue(result, RuleScriptVO.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成redis存储key
     *
     * @param tenantId   租户Id
     * @param scriptCode 脚本编码
     * @return key
     */
    private String getCacheKey(Long tenantId, String scriptCode) {
        return HZeroService.Platform.CODE + ":ruleScripts:" + scriptCode + ":" + tenantId;
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param scriptCode  脚本编码
     * @param tenantId    租户Id
     */
    private String getCache(RedisHelper redisHelper, Long tenantId, String scriptCode) {
        return redisHelper.strGet(getCacheKey(tenantId, scriptCode));
    }
}
