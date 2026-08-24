package org.hzero.platform.app.service.impl;

import org.hzero.boot.platform.rule.entity.RuleScriptVO;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.app.service.RuleScriptService;
import org.hzero.platform.domain.entity.RuleScript;
import org.hzero.platform.domain.repository.RuleScriptRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 规则脚本应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
@Service
public class RuleScriptServiceImpl implements RuleScriptService {

    @Autowired
    private RuleScriptRepository ruleScriptRepository;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public RuleScript selectRuleScriptByCode(String scriptCode, Long tenantId) {
        RuleScript ruleScript = ruleScriptRepository.selectRuleScriptByCode(scriptCode, tenantId);
        // 写入缓存
        RuleScriptVO ruleScriptVO = new RuleScriptVO();
        if (ruleScript == null) {
            ruleScriptVO = null;
        } else {
            BeanUtils.copyProperties(ruleScript, ruleScriptVO);
        }
        RuleScript.refreshCache(redisHelper, tenantId, scriptCode, ruleScriptVO);

        return ruleScript;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuleScript createRuleScript(RuleScript ruleScript) {
        ruleScriptRepository.insertSelective(ruleScript);
        // 更新缓存
        RuleScriptVO ruleScriptVO = new RuleScriptVO();
        BeanUtils.copyProperties(ruleScript, ruleScriptVO);
        RuleScript.refreshCache(redisHelper, ruleScriptVO.getTenantId(), ruleScriptVO.getScriptCode(), ruleScriptVO);
        return ruleScript;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RuleScript updateRuleScript(RuleScript ruleScript) {
        ruleScriptRepository.updateOptional(ruleScript,
                RuleScript.FIELD_SERVER_NAME,
                RuleScript.FIELD_SCRIPT_DESCRIPTION,
                RuleScript.FIELD_SCRIPT_TYPE_CODE,
                RuleScript.FIELD_CATEGORY,
                RuleScript.FIELD_SCRIPT_CONTENT,
                RuleScript.FIELD_ENABLED_FLAG);
        // 更新缓存
        RuleScriptVO ruleScriptVO = new RuleScriptVO();
        BeanUtils.copyProperties(ruleScript, ruleScriptVO);
        RuleScript.refreshCache(redisHelper, ruleScriptVO.getTenantId(), ruleScriptVO.getScriptCode(), ruleScriptVO);
        return ruleScript;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRuleScript(Long ruleScriptId) {
        RuleScript ruleScript = ruleScriptRepository.selectByPrimaryKey(ruleScriptId);
        ruleScriptRepository.deleteByPrimaryKey(ruleScriptId);
        RuleScript.clearRedisCache(redisHelper, ruleScript.getTenantId(), ruleScript.getScriptCode());
    }
}
