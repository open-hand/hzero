package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.CodeRuleDetail;

/**
 * <p>
 * 编码规则明细service
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:02
 */
public interface CodeRuleDetailService {

    /**
     * 新增和更新编码规则明细
     *
     * @param tenantId 租户id
     * @param codeRuleDetail 编码规则明细
     * @return 编码规则明细
     */
    CodeRuleDetail insertOrUpdate(Long tenantId, CodeRuleDetail codeRuleDetail);
}
