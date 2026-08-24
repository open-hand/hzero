package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.WechatOfficial;
import org.hzero.message.domain.repository.WeChatOfficialRepository;
import org.hzero.message.infra.mapper.WeChatOfficialMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 微信公众号配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
@Component
public class WeChatOfficialRepositoryImpl extends BaseRepositoryImpl<WechatOfficial> implements WeChatOfficialRepository {

    @Autowired
    private WeChatOfficialMapper officialMapper;

    @Override
    public Page<WechatOfficial> pageWeChatOfficial(PageRequest pageRequest, Long tenantId, String serverCode, String serverName, String authType, Integer enabledFlag, boolean includeSiteIfQueryByTenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> officialMapper.listWeChatOfficial(tenantId, serverCode, serverName, authType, enabledFlag, includeSiteIfQueryByTenantId));
    }

    @Override
    public WechatOfficial getOfficialById(Long tenantId, Long serverId) {
        return officialMapper.getOfficialById(tenantId, serverId);
    }

    @Override
    public WechatOfficial selectByCode(Long tenantId, String serverCode) {
        return officialMapper.selectByCode(tenantId, serverCode);
    }
}
