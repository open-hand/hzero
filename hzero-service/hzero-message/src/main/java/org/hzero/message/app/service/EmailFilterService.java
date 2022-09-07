package org.hzero.message.app.service;

import java.util.List;

import org.hzero.message.domain.entity.EmailFilter;

/**
 * 邮箱账户黑白名单应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-04 14:23:17
 */
public interface EmailFilterService {

    /**
     * 创建及更新
     *
     * @param emailFilterList 黑白名单
     * @param organizationId  租户ID
     * @return 变更的对象
     */
    List<EmailFilter> createOrUpdateFilter(List<EmailFilter> emailFilterList, Long organizationId);

    /**
     * 删除
     *
     * @param emailFilterList 黑白名单
     */
    void deleteFilter(List<EmailFilter> emailFilterList);
}
