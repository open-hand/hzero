package org.hzero.platform.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.platform.api.dto.CodeRuleDTO;
import org.hzero.platform.api.dto.CodeRuleParamDTO;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;

/**
 * <p>
 * 编码规则app service接口
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:02
 */
public interface CodeRuleService {

    /**
     * 新增和更新编码规则头
     *
     * @param codeRule 编码规则
     * @return 编码规则
     */
    CodeRule insertOrUpdate(CodeRule codeRule);

    /**
     * 根据编码规则code获取编码
     *
     * @param level       应用维度
     * @param tenantId    租户id
     * @param ruleCode    编码规则
     * @param levelCode   应用层级
     * @param levelValue  应用层级值
     * @param variableMap 变量map
     * @return 编码值
     */
    String generateCode(String level, Long tenantId, String ruleCode, String levelCode, String levelValue,
                        Map<String, String> variableMap);

    /**
     * 根据编码规则code获取平台级编码
     *
     * @param codeRuleDTO codeRuleDTO
     * @return 编码值
     */
    String generatePlatformLevelCode(CodeRuleParamDTO codeRuleDTO);

    /**
     * 根据编码规则code获取平台级编码
     *
     * @param ruleCode    编码规则
     * @param variableMap 变量map
     * @return 编码值
     */
    String generatePlatformLevelCode(String ruleCode, Map<String, String> variableMap);

    /**
     * 根据编码规则code获取租户级编码
     *
     * @param tenantId    租户id
     * @param ruleCode    编码规则
     * @param levelCode   应用层级
     * @param levelValue  应用层级
     * @param variableMap 变量map
     * @return 编码值
     */
    String generateTenantLevelCode(Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap);

    /**
     * 查询获取编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId   租户ID
     * @param codeRuleId 编码规则ID
     * @return 编码规则
     */
    CodeRule getCodeRule(Long tenantId, long codeRuleId);

    /**
     * 查询获取编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param ruleCode 编码规则编码
     * @return 编码规则
     */
    CodeRule getCodeRule(Long tenantId, String ruleCode);

    /**
     * 根据编码获取规则明细
     *
     * @param tenantId           租户ID
     * @param ruleCode           编码
     * @param ruleLevel          层级 P/T
     * @param levelCode          GLOBAL/COM
     * @param levelValue         层级值
     * @param previousRuleLevel  实际请求层级
     * @param previousLevelValue 实际层级值
     * @return List<CodeRuleDetail>
     */
    List<CodeRuleDetail> listCodeRuleWithPrevious(Long tenantId, String ruleCode, String ruleLevel, String levelCode, String levelValue, String previousRuleLevel, String previousLevelValue);

    /**
     * 同步编码规则
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail 编码规则明细
     * @return 更新记录数量
     */
    int updateDetail(Long tenantId, CodeRuleDetail codeRuleDetail);

    /**
     * 批量获取编码规则详情集合
     *
     * @param tenantId      租户Id
     * @param ruleCodeList  编码规则编码集合
     * @return  编码规则详情集合
     */
    List<CodeRuleDTO> getCodeRuleList(Long tenantId, List<String> ruleCodeList);
}
