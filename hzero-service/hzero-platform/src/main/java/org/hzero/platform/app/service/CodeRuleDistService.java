package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.CodeRuleDist;

/**
 * <p>
 * 编码规则分配Service
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 19:02
 */
public interface CodeRuleDistService {

    /**
     * 新增和更新编码规则分配
     *
     * @param tenantId 租户id
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配
     */
    CodeRuleDist insertOrUpdate(Long tenantId, CodeRuleDist codeRuleDist);
}
