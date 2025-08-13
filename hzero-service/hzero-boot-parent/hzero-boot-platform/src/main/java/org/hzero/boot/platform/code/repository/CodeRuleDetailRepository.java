package org.hzero.boot.platform.code.repository;

import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;

import java.util.List;

/**
 * <p>
 * 编码规则详情资源层接口
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 16:30
 */
public interface CodeRuleDetailRepository {

    /**
     * 获取编码规则明细List集合
     *
     * @param codeRuleKey 请求KEY
     * @return 编码规则明细List集合
     */
    List<CodeRuleDetail> listCodeRule(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey);

    /**
     * 从redis中获取编码规则明细list集合
     *
     * @param codeRuleKey 请求KEY
     * @return 编码规则明细list
     */
    List<CodeRuleDetail> listCodeRuleFromCache(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey);

    /**
     * 从数据库中获取编码规则明细List集合
     *
     * @param codeRuleKey 请求KEY
     * @return 编码规则明细list
     */
    List<CodeRuleDetail> listCodeRuleFromDatabase(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey);

    /**
     * 判断是否快速失败
     *
     * @param failFastCacheKey 快速失败KEY
     * @return 是否快速失败
     */
    boolean isFailFast(String failFastCacheKey);

    /**
     * 设置快速失败
     *
     * @param failFastKey 快速失败KEY
     */
    void failFast(String failFastKey);

    /**
     * 根据主键id更新数据库中的编码规则明细
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail 编码规则明细
     */
    void updateByPrimaryKey(Long tenantId, CodeRuleDetail codeRuleDetail);

    /**
     * 根据主键查询
     *
     * @param tenantId     租户ID
     * @param ruleDetailId 编码规则明细ID
     * @return 编码规则明细
     */
    CodeRuleDetail selectByPrimaryKey(Long tenantId, Long ruleDetailId);

    /**
     * 修改编码规则分配为启用
     *
     * @param tenantId   租户ID
     * @param ruleDistId 编码规则ID
     */
    void updateDistUseFlag(Long tenantId, Long ruleDistId);
}
