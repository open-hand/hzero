package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.CallServer;
import org.hzero.message.domain.repository.CallServerRepository;
import org.hzero.message.infra.mapper.CallServerMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 语音消息服务 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
@Component
public class CallServerRepositoryImpl extends BaseRepositoryImpl<CallServer> implements CallServerRepository {

    @Autowired
    private CallServerMapper callServerMapper;

    @Override
    public Page<CallServer> pageCallServer(PageRequest pageRequest, Long tenantId, Integer enabledFlag, String serverTypeCode, String serverCode, String serverName, boolean includeSiteIfQueryByTenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> callServerMapper.listCallServer(tenantId, enabledFlag, serverTypeCode, serverCode, serverName, includeSiteIfQueryByTenantId));
    }

    @Override
    public CallServer detailCallServer(Long tenantId, Long serverId) {
        return callServerMapper.detailCallServer(tenantId, serverId);
    }

    @Override
    public CallServer selectByCode(Long tenantId, String serverCode) {
        return callServerMapper.selectByCode(tenantId, serverCode);
    }
}
