package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.CodeRuleDistDTO;
import org.hzero.platform.domain.entity.CodeRuleDist;

/**
 * <p>
 * 编码规则分配repository
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:54
 */
public interface CodeRuleDistRepository extends BaseRepository<CodeRuleDist> {

    /**
     * 插入一个当前编码规则下全局级的分配
     *
     * @param ruleId 编码规则id
     * @return 分配对象
     */
    CodeRuleDist insertGlobalDist(Long ruleId);

    /**
     * 插入一个当前编码规则下全局级的分配
     *
     * @param ruleId   编码规则id
     * @param tenantId 租户Id
     * @return 分配对象
     */
    CodeRuleDist insertGlobalDist(Long ruleId, Long tenantId);

    /**
     * 编码规则分配插入或更新
     *
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配
     */
    CodeRuleDist insertOrUpdate(CodeRuleDist codeRuleDist);

    /**
     * 更新编码规则分配以及redis缓存
     *
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配
     */
    CodeRuleDist updateCodeRuleDist(CodeRuleDist codeRuleDist);

    /**
     * 删除编码规则分配
     *
     * @param tenantId 租户id
     * @param codeRuleDistList 规则分配List
     */
    void deleteDist(Long tenantId, List<CodeRuleDist> codeRuleDistList);

    /**
     * 根据ruleId进行查询编码规则分配
     *
     * @param ruleId 编码规则id
     * @return 编码规则分配list
     */
    List<CodeRuleDistDTO> selectCodeRuleDistList(Long ruleId);

    /**
     * 根据编码规则分配id查询出分配信息和明细list
     *
     * @param ruleDistId 编码规则分配id
     * @return 编码规则分配dto
     */
    CodeRuleDistDTO selectCodeRuleDistAndDetail(Long tenantId, Long ruleDistId);

    /**
     * 新增一个编码规则分配
     *
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配
     */
    CodeRuleDist insertCodeRuleDist(CodeRuleDist codeRuleDist);

    /**
     * 修改编码规则分配为启用
     *
     * @param ruleDistId
     * @return
     */
    int updateDistUseFlag(Long ruleDistId);
}
