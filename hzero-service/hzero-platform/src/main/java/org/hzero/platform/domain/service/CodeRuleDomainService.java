package org.hzero.platform.domain.service;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 编码规则domain service
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/15 17:05
 */
@Component
public class CodeRuleDomainService {

    private static final Logger logger = LoggerFactory.getLogger(CodeRuleDomainService.class);

    private final CodeRuleRepository codeRuleRepository;

    private final RedisHelper redisHelper;

    private final CodeRuleDetailRepository codeRuleDetailRepository;

    private final CodeRuleDistRepository codeRuleDistRepository;

    private final CodeRuleBuilder codeRuleBuilder;

    @Autowired
    public CodeRuleDomainService(CodeRuleRepository codeRuleRepository, RedisHelper redisHelper, CodeRuleDetailRepository codeRuleDetailRepository, CodeRuleDistRepository codeRuleDistRepository, CodeRuleBuilder codeRuleBuilder) {
        this.codeRuleRepository = codeRuleRepository;
        this.redisHelper = redisHelper;
        this.codeRuleDetailRepository = codeRuleDetailRepository;
        this.codeRuleDistRepository = codeRuleDistRepository;
        this.codeRuleBuilder = codeRuleBuilder;
    }

    /**
     * 根据编码规则code获取编码
     *
     * @param level       应用维度
     * @param tenantId    租户id
     * @param ruleCode    编码规则
     * @param levelCode   应用层级
     * @param levelValue  应用层级值
     * @param variableMap 变量map
     * @return 编码规则值
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateCode(String level, Long tenantId, String ruleCode, String levelCode, String levelValue,
                               Map<String, String> variableMap) {

        return codeRuleBuilder.generateCode(level, tenantId, ruleCode, levelCode, levelValue, variableMap);
    }

    /**
     * 启动时将所有编码规则配置信息刷到redis中去
     */
    public void initCodeRuleCache() {
        SecurityTokenHelper.close();
        List<CodeRule> codeRuleList = codeRuleRepository.selectAll();
        if (CollectionUtils.isNotEmpty(codeRuleList)) {
            codeRuleList.forEach(codeRule -> {
                SecurityTokenHelper.close();
                List<CodeRuleDist> codeRuleDistList =
                        codeRuleDistRepository.select(CodeRuleDist.FIELD_RULE_ID, codeRule.getRuleId());
                if (CollectionUtils.isNotEmpty(codeRuleDistList)) {
                    codeRuleDistList.forEach(codeRuleDist -> {
                        SecurityTokenHelper.close();
                        List<CodeRuleDetail> codeRuleDetailList = codeRuleDetailRepository
                                .select(CodeRuleDetail.FIELD_RULE_DIST_ID, codeRuleDist.getRuleDistId());
                        if (CollectionUtils.isNotEmpty(codeRuleDetailList)) {
                            // 对编码规则明细进行排序
                            codeRuleDetailList.sort(Comparator.comparingLong(CodeRuleDetail::getOrderSeq));
                            codeRuleRepository.initCache(codeRule, codeRuleDist, codeRuleDetailList);
                        }
                    });
                }
            });
        }
        SecurityTokenHelper.clear();
    }
}
