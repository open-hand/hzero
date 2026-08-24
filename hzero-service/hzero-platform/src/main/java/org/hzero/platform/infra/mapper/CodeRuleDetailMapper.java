package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.CodeRuleDetailDTO;
import org.hzero.platform.domain.entity.CodeRuleDetail;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 编码规则明细mapper
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 17:58
 */
public interface CodeRuleDetailMapper extends BaseMapper<CodeRuleDetail> {

    /**
     * 根据编码规则分配id查询明细
     *
     * @param ruleDistId 编码规则分配id
     * @return 编码规则明细list
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
     * @param tenantId   租户ID
     * @param ruleCode   编码
     * @param ruleLevel  层级P/T
     * @param levelCode  GLOBAL/COM
     * @param levelValue 公司编码/GLOBAL
     * @return List<CodeRuleDetail>
     */
    List<CodeRuleDetail> selectDetailListByRuleCode(@Param("tenantId") Long tenantId,
                                                    @Param("ruleCode") String ruleCode,
                                                    @Param("ruleLevel") String ruleLevel,
                                                    @Param("levelCode") String levelCode,
                                                    @Param("levelValue") String levelValue);

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
    List<CodeRuleDetail> listCodeRuleWithPrevious(@Param("tenantId") Long tenantId,
                                                  @Param("ruleCode") String ruleCode,
                                                  @Param("ruleLevel") String ruleLevel,
                                                  @Param("levelCode") String levelCode,
                                                  @Param("levelValue") String levelValue,
                                                  @Param("previousLevelCode") String previousRuleLevel,
                                                  @Param("previousLevelValue") String previousLevelValue);
}
