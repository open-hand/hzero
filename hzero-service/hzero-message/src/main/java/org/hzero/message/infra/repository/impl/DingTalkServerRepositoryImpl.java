package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.DingTalkServer;
import org.hzero.message.domain.repository.DingTalkServerRepository;
import org.hzero.message.infra.mapper.DingTalkServerMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 钉钉配置 资源库实现
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
@Component
public class DingTalkServerRepositoryImpl extends BaseRepositoryImpl<DingTalkServer> implements DingTalkServerRepository {

    @Autowired
    private DingTalkServerMapper dingTalkServerMapper;

    @Override
    public Page<DingTalkServer> pageDingTalkServer(PageRequest pageRequest, Long tenantId, String serverCode, String serverName, String authType, Integer enabledFlag, boolean includeSiteIfQueryByTenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> dingTalkServerMapper.listDingTalkServer(tenantId, serverCode, serverName, authType, enabledFlag, includeSiteIfQueryByTenantId));
    }

    @Override
    public DingTalkServer getDingTalkServerById(Long tenantId, Long serverId) {
        return dingTalkServerMapper.getDingTalkServerById(tenantId, serverId);
    }

    @Override
    public DingTalkServer selectByCode(Long tenantId, String serverCode) {
        return dingTalkServerMapper.selectOne(new DingTalkServer().setServerCode(serverCode).setTenantId(tenantId));
    }
}
