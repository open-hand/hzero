package org.hzero.boot.platform.code.feign.failback;

import java.util.List;

import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.boot.platform.code.entity.CodeRule;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;
import org.hzero.boot.platform.code.entity.CodeRuleDist;
import org.hzero.boot.platform.code.feign.CodeRuleRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 编码规则feign调用出错回调类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 17:12
 */
@Component
public class CodeRuleRemoteServiceImpl implements CodeRuleRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(CodeRuleRemoteServiceImpl.class);

    @Override
    public CodeRuleDetail selectByPrimaryKey(Long tenantId, Long ruleDetailId) {
        logger.error("Get CodeRuleDetail failed where ruleDetailId = {}", ruleDetailId);
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_DETAIL_NOT_FOUNT);
    }

    @Override
    public void updateByPrimaryKey(Long tenantId, CodeRuleDetail codeRuleDetail) {
        logger.error("Update CodeRuleDetail failed");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_DETAIL_UPDATE_FAILED);
    }

    @Override
    public void updateDistUseFlag(Long tenantId, Long ruleDistId) {
        logger.error("Update CodeRuleDist Use Flag failed");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_DIST_UPDATE_FAILED);
    }

    @Override
    public ResponseEntity<String> getCodeRule(Long tenantId, long codeRuleId) {
        logger.error("Error getCodeRule");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> getCodeRule(Long tenantId, String ruleCode) {
        logger.error("Error getCodeRule");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> saveCodeRule(long tenantId, CodeRule codeRule) {
        logger.error("Error saveCodeRule");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> saveCodeRuleDist(long tenantId, CodeRuleDist codeRuleDist) {
        logger.error("Error saveCodeRuleDist");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> saveCodeRuleDetail(long tenantId, CodeRuleDetail codeRuleDetail) {
        logger.error("Error saveCodeRuleDetail");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> deleteCodeRule(long tenantId, List<CodeRule> codeRule) {
        logger.error("Error deleteCodeRule");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> deleteCodeRuleDist(long tenantId, List<CodeRuleDist> codeRuleDist) {
        logger.error("Error deleteCodeRuleDist");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> deleteCodeRuleDetail(long tenantId, List<CodeRuleDetail> codeRuleDetail) {
        logger.error("Error deleteCodeRuleDetail");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }

    @Override
    public ResponseEntity<String> listCodeRule(Long organizationId, Long tenantId, String ruleCode, String ruleLevel, String levelCode, String levelValue,
                                               String previousRuleLevel, String previousLevelValue) {
        logger.error("Error queryCodeRuleDetails");
        throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_CLIENT_REQUEST);
    }
}
