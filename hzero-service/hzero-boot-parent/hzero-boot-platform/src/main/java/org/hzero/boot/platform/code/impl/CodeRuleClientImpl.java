package org.hzero.boot.platform.code.impl;

import java.util.Collections;

import org.hzero.boot.platform.code.CodeRuleClient;
import org.hzero.boot.platform.code.entity.CodeRule;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;
import org.hzero.boot.platform.code.entity.CodeRuleDist;
import org.hzero.boot.platform.code.feign.CodeRuleRemoteService;
import org.hzero.core.util.ResponseUtils;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 编码规则客户端实现
 * </p>
 *
 * @author qingsheng.chen 2019/3/5 星期二 14:45
 */
@Component
public class CodeRuleClientImpl implements CodeRuleClient {
    private CodeRuleRemoteService codeRuleRemoteService;

    public CodeRuleClientImpl(CodeRuleRemoteService codeRuleRemoteService) {
        this.codeRuleRemoteService = codeRuleRemoteService;
    }

    @Override
    public CodeRule getCodeRule(long tenantId, long codeRuleId) {
        return ResponseUtils.getResponse(codeRuleRemoteService.getCodeRule(tenantId, codeRuleId), CodeRule.class);
    }

    @Override
    public CodeRule getCodeRule(long tenantId, String ruleCode) {
        return ResponseUtils.getResponse(codeRuleRemoteService.getCodeRule(tenantId, ruleCode), CodeRule.class);
    }

    @Override
    public CodeRule saveCodeRule(long tenantId, CodeRule codeRule) {
        return ResponseUtils.getResponse(codeRuleRemoteService.saveCodeRule(tenantId, codeRule), CodeRule.class);
    }

    @Override
    public CodeRuleDist saveCodeRuleDist(long tenantId, CodeRuleDist codeRuleDist) {
        return ResponseUtils.getResponse(codeRuleRemoteService.saveCodeRuleDist(tenantId, codeRuleDist), CodeRuleDist.class);
    }

    @Override
    public CodeRuleDetail saveCodeRuleDetail(long tenantId, CodeRuleDetail codeRuleDetail) {
        return ResponseUtils.getResponse(codeRuleRemoteService.saveCodeRuleDetail(tenantId, codeRuleDetail), CodeRuleDetail.class);
    }

    @Override
    public void deleteCodeRule(long tenantId, CodeRule codeRule) {
        ResponseUtils.getResponse(codeRuleRemoteService.deleteCodeRule(tenantId, Collections.singletonList(codeRule)), Void.class);
    }

    @Override
    public void deleteCodeRuleDist(long tenantId, CodeRuleDist codeRuleDist) {
        ResponseUtils.getResponse(codeRuleRemoteService.deleteCodeRuleDist(tenantId, Collections.singletonList(codeRuleDist)), Void.class);
    }

    @Override
    public void deleteCodeRuleDetail(long tenantId, CodeRuleDetail codeRuleDetail) {
        ResponseUtils.getResponse(codeRuleRemoteService.deleteCodeRuleDetail(tenantId, Collections.singletonList(codeRuleDetail)), Void.class);
    }
}
