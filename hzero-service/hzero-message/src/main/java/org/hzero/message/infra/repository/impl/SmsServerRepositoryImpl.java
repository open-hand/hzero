package org.hzero.message.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.message.domain.repository.SmsServerRepository;
import org.hzero.message.infra.mapper.SmsServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信服务 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Component
public class SmsServerRepositoryImpl extends BaseRepositoryImpl<SmsServer> implements SmsServerRepository {

    private SmsServerMapper smsServerMapper;

    @Autowired
    public SmsServerRepositoryImpl(SmsServerMapper smsServerMapper) {
        this.smsServerMapper = smsServerMapper;
    }

    @Override
    public Page<SmsServer> selectSmsServer(Long tenantId, String serverCode, String serverName, String serverTypeCode, Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> smsServerMapper.selectSmsServer(tenantId, serverCode, serverName, serverTypeCode, enabledFlag, includeSiteIfQueryByTenantId));
    }

    @Override
    public SmsServer selectByCode(Long tenantId, String serverCode) {
        return smsServerMapper.selectByCode(tenantId, serverCode);
    }
}
