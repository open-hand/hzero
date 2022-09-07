package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.EmailFilter;
import org.hzero.message.domain.repository.EmailFilterRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 邮箱账户黑白名单 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-04 14:23:17
 */
@Component
public class EmailFilterRepositoryImpl extends BaseRepositoryImpl<EmailFilter> implements EmailFilterRepository {


    @Override
    public Page<EmailFilter> pageEmailFilter(PageRequest pageRequest, String address, Long serverId) {
        return PageHelper.doPageAndSort(pageRequest, () -> selectOptional(
                new EmailFilter().setAddress(address).setServerId(serverId),
                new Criteria().select(
                        EmailFilter.FIELD_EMAIL_FILTER_ID,
                        EmailFilter.FIELD_ADDRESS,
                        AuditDomain.FIELD_OBJECT_VERSION_NUMBER)));
    }
}
