package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.PermissionRuleDTO;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRule;

import java.util.List;

/**
 * 屏蔽规则Mapper
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
public interface PermissionRuleMapper extends BaseMapper<PermissionRule> {

    /**
     * 根据屏蔽范围list查询
     *
     * @param permissionRangeList 屏蔽范围list
     * @return sqlList
     */
    List<String> selectSqlValueByRangeList(@Param("list") List<PermissionRange> permissionRangeList);

    /**
     * 查询屏蔽范围
     *
     * @param rule
     * @return
     */
    List<PermissionRuleDTO> selectPermissionRule(PermissionRule rule);

    /**
     * 查询租户级屏蔽范围
     *
     * @param rule
     * @return
     */
    List<PermissionRuleDTO> selectOrgPermissionRule(PermissionRule rule);

    /**
     * 查询可以应用的屏蔽规则
     *
     * @param tenantId 租户ID
     * @param ruleCode 屏蔽规则编码
     * @param ruleName 屏蔽规则名称
     * @return 可以应用的屏蔽规则列表
     */
    List<PermissionRuleDTO> listRuleEnabled(@Param("tenantId") Long tenantId, @Param("ruleCode") String ruleCode, @Param("ruleName") String ruleName);

    /**
     * 通过唯一索引查询
     *
     * @param ruleCode 屏蔽规则编码
     * @param tenantId 屏蔽规则租户ID
     * @return 屏蔽规则
     */
    PermissionRule queryPermissionRule(@Param("ruleCode") String ruleCode, @Param("tenantId") Long tenantId);

    List<PermissionRule> listEmptyRule(@Param("ruleIdList") List<Long> ruleIdList);
}
