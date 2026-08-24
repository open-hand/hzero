package org.hzero.boot.platform.rule.feign.impl;

import org.hzero.boot.platform.rule.constant.RuleConstants;
import org.hzero.boot.platform.rule.feign.RemoteRuleScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * hpfm  feign调用失败回调
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 17:48
 */
@Component
public class RemoteRuleScriptServiceFallBackImpl implements RemoteRuleScriptService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteRuleScriptServiceFallBackImpl.class);

    @Override
    public ResponseEntity<String> selectRuleScriptByCode(String scriptCode, Long tenantId) {
        logger.error("Get rule script configuration failed");
        throw new CommonException(RuleConstants.ErrorCode.RULE_SCRIPT_GET_RULE_FAILED);
    }
}
