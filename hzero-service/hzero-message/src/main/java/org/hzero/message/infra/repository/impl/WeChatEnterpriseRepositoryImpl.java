package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.WeChatEnterprise;
import org.hzero.message.domain.repository.WeChatEnterpriseRepository;
import org.hzero.message.infra.mapper.WeChatEnterpriseMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 企业微信配置 资源库实现
 *
 * @author fanghan.liu@hand-china.com 2019-10-15 14:31:46
 */
@Component
public class WeChatEnterpriseRepositoryImpl extends BaseRepositoryImpl<WeChatEnterprise> implements WeChatEnterpriseRepository {

    @Autowired
    private WeChatEnterpriseMapper wechatEnterpriseMapper;

    @Override
    public Page<WeChatEnterprise> pageWeChatEnterprise(PageRequest pageRequest, Long tenantId, String serverCode, String serverName, String authType, Integer enabledFlag, boolean includeSiteIfQueryByTenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> wechatEnterpriseMapper.listWeChatEnterprise(tenantId, serverCode, serverName, authType, enabledFlag, includeSiteIfQueryByTenantId));
    }

    @Override
    public WeChatEnterprise getWeChatEnterpriseById(Long tenantId, Long serverId) {
        return wechatEnterpriseMapper.getWeChatEnterpriseById(tenantId, serverId);
    }

    @Override
    public WeChatEnterprise selectByCode(Long tenantId, String serverCode) {
        return wechatEnterpriseMapper.selectByCode(tenantId, serverCode);
    }
}
