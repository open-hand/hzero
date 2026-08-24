package org.hzero.admin.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ApiMonitorRule;
import org.hzero.admin.domain.repository.ApiMonitorRuleRepository;
import org.hzero.admin.infra.mapper.ApiMonitorRuleMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Repository;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 3:27 下午
 */
@Repository
public class ApiMonitorRuleRepositoryImpl extends BaseRepositoryImpl<ApiMonitorRule> implements ApiMonitorRuleRepository {
    @Override
    public Page<ApiMonitorRule> pageAndSort(PageRequest pageRequest, ApiMonitorRule queryParam) {
        return PageHelper.doPageAndSort(pageRequest, () -> selectByCondition(Condition.builder(ApiMonitorRule.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ApiMonitorRule.FIELD_API_MONITOR_RULE_ID, queryParam.getMonitorRuleId(), true)
                        .andLike(ApiMonitorRule.FIELD_URL_PATTERN, queryParam.getUrlPattern(), true))
                .build()));
    }
}
