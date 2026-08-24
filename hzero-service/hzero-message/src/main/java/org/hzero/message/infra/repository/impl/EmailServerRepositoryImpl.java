package org.hzero.message.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.repository.EmailServerRepository;
import org.hzero.message.infra.mapper.EmailServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 邮箱服务 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Component
public class EmailServerRepositoryImpl extends BaseRepositoryImpl<EmailServer> implements EmailServerRepository {
    private EmailServerMapper emailServerMapper;

    @Autowired
    public EmailServerRepositoryImpl(EmailServerMapper emailServerMapper) {
        this.emailServerMapper = emailServerMapper;
    }

    @Override
    public Page<EmailServer> selectEmailServer(Long tenantId, String serverCode, String serverName, Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> emailServerMapper.selectEmailServer(tenantId, serverCode, serverName, enabledFlag, includeSiteIfQueryByTenantId));
    }

    @Override
    public EmailServer selectByCode(Long tenantId, String serverCode) {
        return emailServerMapper.selectByCode(tenantId, serverCode);
    }
}
