package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.PermissionRuleDTO;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.entity.PermissionRule;

import java.util.List;

/**
 * 屏蔽规则资源库
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
public interface PermissionRuleRepository extends BaseRepository<PermissionRule> {

    /**
     * 新增权限规则
     *
     * @param permissionRule
     * @return
     */
    PermissionRule insertPermissionRule(PermissionRule permissionRule);

    /**
     * 根据屏蔽范围list查询
     *
     * @param permissionRangeList 屏蔽范围list
     * @return sqlList
     */
    List<String> selectSqlValueByRangeList(List<PermissionRange> permissionRangeList);

    List<PermissionRule> listEmptyRule(List<Long> ruleIdList);

    /**
     * 更新数据屏蔽规则
     *
     * @param permissionRule 数据屏蔽规则
     * @return 数据屏蔽规则
     */
    PermissionRule updatePermissionRule(PermissionRule permissionRule);

    /**
     * 删除数据屏蔽规则
     *
     * @param ruleId   数据屏蔽规则id
     * @param tenantId 租户id
     */
    default void deletePermissionRule(Long ruleId, Long tenantId) {
        deletePermissionRule(ruleId, tenantId, true);
    }

    /**
     * 删除数据屏蔽规则
     *
     * @param ruleId   数据屏蔽规则id
     * @param tenantId 租户id
     */
    void deletePermissionRule(Long ruleId, Long tenantId, boolean validEditable);

    /**
     * 模糊查询数据屏蔽规则
     *
     * @param pageRequest    分页
     * @param permissionRule 数据屏蔽规则
     * @return list
     */
    List<PermissionRuleDTO> selectPermissionRule(PageRequest pageRequest, PermissionRule permissionRule);

    /**
     * 模糊查询租户级的数据屏蔽规则
     *
     * @param pageRequest    分页
     * @param permissionRule 数据屏蔽规则
     * @return list
     */
    List<PermissionRuleDTO> selectOrgPermissionRule(PageRequest pageRequest, PermissionRule permissionRule);

    /**
     * 查询可以应用的屏蔽规则
     *
     * @param tenantId    租户ID
     * @param ruleCode    屏蔽规则编码
     * @param ruleName    屏蔽规则名称
     * @param pageRequest 分页参数
     * @return 可以应用的屏蔽规则列表
     */
    Page<PermissionRuleDTO> listRuleEnabled(Long tenantId, String ruleCode, String ruleName, PageRequest pageRequest);

    /**
     * 通过唯一索引查询
     *
     * @param ruleCode 屏蔽规则编码
     * @param tenantId 屏蔽规则租户ID
     * @return 屏蔽规则
     */
    PermissionRule queryPermissionRule(String ruleCode, Long tenantId);
}
