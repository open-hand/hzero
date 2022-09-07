package org.hzero.message.app.service.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.WebhookServerService;
import org.hzero.message.domain.entity.WebhookServer;
import org.hzero.message.domain.repository.WebhookServerRepository;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * webhook配置应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
@Service
public class WebhookServerServiceImpl extends BaseAppService implements WebhookServerService {

    @Autowired
    private WebhookServerRepository serverRepository;

    @Override
    public Page<WebhookServer> pageWebHookList(PageRequest pageRequest, WebhookServer webhookServer, Long tenantId, boolean includeSiteIfQueryByTenantId) {
        if (tenantId != null) {
            // 租户级直接获取当前租户下该配置的内容
            webhookServer.setTenantId(tenantId);
        }
        // 不返回URL和secret信息，处于安全性考虑，该URL不可随便开放给别人看
        return serverRepository.pageWebHookList(pageRequest, webhookServer, includeSiteIfQueryByTenantId);
    }

    @Override
    public WebhookServer getWebHookDetails(Long tenantId, Long serverId) {
        WebhookServer webHookDetails = serverRepository.getWebHookDetails(tenantId, serverId);
        if (Objects.isNull(webHookDetails)) {
            // 若查不到数据则报错，此时两种情况，1.数据被删除；2.当前租户传入的 tenantId 与数据库中该条数据的 tenantId 不匹配
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        return webHookDetails;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WebhookServer createWebHook(Long tenantId, WebhookServer webhookServer) {
        if (tenantId != null) {
            webhookServer.setTenantId(tenantId);
        }
        validObject(webhookServer);
        // 校验数据
        serverRepository.validWebHookParams(webhookServer);
        if (StringUtils.isNotBlank(webhookServer.getSecret())) {
            webhookServer.setSecret(DataSecurityHelper.encrypt(webhookServer.getSecret()));
        }
        serverRepository.insertSelective(webhookServer);
        return webhookServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WebhookServer updateWebHook(Long tenantId, WebhookServer webhookServer) {
        SecurityTokenHelper.validToken(webhookServer);
        if (tenantId != null) {
            webhookServer.setTenantId(tenantId);
        }
        validObject(webhookServer, WebhookServer.Update.class);
        // 校验数据
        serverRepository.validWebHookParams(webhookServer);
        if (StringUtils.isNotBlank(webhookServer.getSecret())) {
            webhookServer.setSecret(DataSecurityHelper.encrypt(webhookServer.getSecret()));
        }
        serverRepository.updateOptional(webhookServer, WebhookServer.UPDATE_FIELDS);
        return webhookServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWebHook(WebhookServer webhookServer) {
        SecurityTokenHelper.validToken(webhookServer);
        serverRepository.deleteByPrimaryKey(webhookServer);
    }

}
