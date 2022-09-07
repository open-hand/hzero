package org.hzero.boot.platform.code.builder;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 编码规则构造器接口
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 16:33
 */
public interface CodeRuleBuilder {

    /**
     * 生成平台级编码规则，全局连续
     *
     * @param ruleCode    编码规则编码
     * @param variableMap 如果编码规则中使用了变量，变量的值会从Map中获取，如果获取不到会使用空串代替
     * @return 编码
     */
    String generateCode(String ruleCode, Map<String, String> variableMap);

    /**
     * 批量生成平台级编码规则，全局连续
     *
     * @param quantity    数量
     * @param ruleCode    编码规则编码
     * @param variableMap 如果编码规则中使用了变量，变量的值会从Map中获取，如果获取不到会使用空串代替
     * @return 编码
     */
    List<String> generateCode(int quantity, String ruleCode, Map<String, String> variableMap);

    /**
     * 生成租户级编码规则，租户内连续，<strong>不同租户之间会重复</strong>
     *
     * @param tenantId    租户id
     * @param ruleCode    编码规则编码
     * @param levelCode   应用层级编码{@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode}
     * @param levelValue  应用层级值
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#GLOBAL}，层级值等于层级编码
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#COMPANY}，生成公司级编码，层级值为公司编码
     * @param variableMap 如果编码规则中使用了变量，变量的值会从Map中获取，如果获取不到会使用空串代替
     * @return 编码
     */
    String generateCode(Long tenantId,
                        String ruleCode,
                        String levelCode,
                        String levelValue,
                        Map<String, String> variableMap);

    /**
     * 批量生成租户级编码规则，租户内连续，<strong>不同租户之间会重复</strong>
     *
     * @param quantity    数量
     * @param tenantId    租户id
     * @param ruleCode    编码规则编码
     * @param levelCode   应用层级编码{@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode}
     * @param levelValue  应用层级值
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#GLOBAL}，层级值等于层级编码
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#COMPANY}，生成公司级编码，层级值为公司编码
     * @param variableMap 如果编码规则中使用了变量，变量的值会从Map中获取，如果获取不到会使用空串代替
     * @return 编码
     */
    List<String> generateCode(int quantity,
                              Long tenantId,
                              String ruleCode,
                              String levelCode,
                              String levelValue,
                              Map<String, String> variableMap);

    /**
     * 生成编码
     * 平台级编码规则支持：平台级/租户级/公司级三种访问形式，在各自的维度内连续
     * 租户级编码规则支持：租户级/公司级两种访问形式
     * 公司级编码规则支持：公司级一种访问形式
     *
     * @param level       应用维度{@link org.hzero.boot.platform.code.constant.CodeConstants.Level}
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#PLATFORM} : 生成平台级编码规则，全局连续
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#TENANT} : 生成租户级编码规则，租户内连续
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#COMPANY} : 生成公司级编码规则，公司内连续
     * @param tenantId    租户id
     *                    level = {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#PLATFORM} : 该参数无效
     *                    level = {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#TENANT} : 该参数为需要生成编码的租户
     *                    level = {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#COMPANY} : 该参数为需要生成编码的公司所属的租户
     * @param ruleCode    编码规则编码
     * @param levelCode   应用层级{@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode}
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#GLOBAL}，全局配置，层级值等于层级编码
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#COMPANY}，公司及配置，层级值为公司编码
     * @param levelValue  应用层级值
     *                    仅在 levelCode = {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#COMPANY}时生效，为公司编码
     * @param variableMap 如果编码规则中使用了变量，变量的值会从Map中获取，如果获取不到会使用空串代替
     * @return 编码
     */
    String generateCode(String level,
                        Long tenantId,
                        String ruleCode,
                        String levelCode,
                        String levelValue,
                        Map<String, String> variableMap);

    /**
     * 生成编码
     * 平台级编码规则支持：平台级/租户级/公司级三种访问形式，在各自的维度内连续
     * 租户级编码规则支持：租户级/公司级两种访问形式
     * 公司级编码规则支持：公司级一种访问形式
     *
     * @param quantity    数量
     * @param level       应用维度{@link org.hzero.boot.platform.code.constant.CodeConstants.Level}
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#PLATFORM} : 生成平台级编码规则，全局连续
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#TENANT} : 生成租户级编码规则，租户内连续
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#COMPANY} : 生成公司级编码规则，公司内连续
     * @param tenantId    租户id
     *                    level = {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#PLATFORM} : 该参数无效
     *                    level = {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#TENANT} : 该参数为需要生成编码的租户
     *                    level = {@link org.hzero.boot.platform.code.constant.CodeConstants.Level#COMPANY} : 该参数为需要生成编码的公司所属的租户
     * @param ruleCode    编码规则编码
     * @param levelCode   应用层级{@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode}
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#GLOBAL}，全局配置，层级值等于层级编码
     *                    {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#COMPANY}，公司及配置，层级值为公司编码
     * @param levelValue  应用层级值
     *                    仅在 levelCode = {@link org.hzero.boot.platform.code.constant.CodeConstants.CodeRuleLevelCode#COMPANY}时生效，为公司编码
     * @param variableMap 如果编码规则中使用了变量，变量的值会从Map中获取，如果获取不到会使用空串代替
     * @return 编码
     */
    List<String> generateCode(int quantity,
                              String level,
                              Long tenantId,
                              String ruleCode,
                              String levelCode,
                              String levelValue,
                              Map<String, String> variableMap);

    /**
     * 加密序列转解密序列
     *
     * @param encryptSequence 加密序列
     * @return 解密序列
     */
    default long decryptSequence(String encryptSequence) {
        return Long.parseLong(encryptSequence);
    }

    /**
     * 根据编码规则code获取平台级编码
     *
     * @param ruleCode    编码规则
     * @param variableMap 变量map
     * @return 编码值
     * @deprecated {@link CodeRuleBuilder#generateCode(String, Map)}
     */
    @Deprecated
    String generatePlatformLevelCode(String ruleCode,
                                     Map<String, String> variableMap);

    /**
     * 根据编码规则code获取租户级编码
     *
     * @param tenantId    租户id
     * @param ruleCode    编码规则
     * @param levelCode   应用层级
     * @param levelValue  应用层级
     * @param variableMap 变量map
     * @return 编码值
     * @deprecated {@link CodeRuleBuilder#generateCode(Long, String, String, String, Map)}
     */
    @Deprecated
    String generateTenantLevelCode(Long tenantId,
                                   String ruleCode,
                                   String levelCode,
                                   String levelValue,
                                   Map<String, String> variableMap);

}
