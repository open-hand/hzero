package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.CodeRuleDetailDTO;
import org.hzero.platform.domain.entity.CodeRuleDetail;

/**
 * <p>
 * 编码规则明细repository
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:53
 */
public interface CodeRuleDetailRepository extends BaseRepository<CodeRuleDetail> {

    /**
     * 插入或者更新编码规则明细
     *
     * @param codeRuleDetail 编码规则明细
     * @return 编码规则明细
     */
    CodeRuleDetail insertOrUpdate(CodeRuleDetail codeRuleDetail);

    /**
     * 插入编码规则明细并同步到redis
     *
     * @param codeRuleDetail 编码规则明细
     * @return 编码规则明细
     */
    CodeRuleDetail insertCodeRuleDetail(CodeRuleDetail codeRuleDetail);

    /**
     * 更新编码规则明细并同步到redis
     *
     * @param codeRuleDetail 编码规则明细
     * @return 编码规则明细
     */
    CodeRuleDetail updateCodeRuleDetail(CodeRuleDetail codeRuleDetail);

    /**
     * 删除编码规则明细
     *
     * @param tenantId           租户id
     * @param codeRuleDetailList 规则明细list
     */
    void deleteDetail(Long tenantId, List<CodeRuleDetail> codeRuleDetailList);

    /**
     * 根据编码规则分配id查询明细
     *
     * @param ruleDistId 编码规则分配id
     * @return 编码规则list
     */
    List<CodeRuleDetailDTO> selectCodeRuleDetailList(Long ruleDistId);

    /**
     * 不负责校验版本号的更新codeRuleDetail
     *
     * @param codeRuleDetail
     * @return
     */
    int updateCodeRuleDetailWithNotOVN(CodeRuleDetail codeRuleDetail);

    /**
     * 根据编码/层级/租户ID查询编码规则明细
     *
     * @param organizationId 租户ID
     * @param ruleCode       编码
     * @param ruleLevel      层级 P/T
     * @param levelCode      GLOBAL/COM
     * @param levelValue     公司编码
     * @return List<CodeRuleDetail>
     */
    List<CodeRuleDetail> selectDetailListByRuleCode(Long organizationId, String ruleCode,
                                                    String ruleLevel, String levelCode,
                                                    String levelValue);

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
}
