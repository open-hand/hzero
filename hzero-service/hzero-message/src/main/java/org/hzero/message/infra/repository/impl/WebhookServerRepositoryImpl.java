package org.hzero.message.infra.repository.impl;

import org.apache.commons.lang.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.domain.entity.WebhookServer;
import org.hzero.message.domain.repository.WebhookServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.mapper.WebhookServerMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * webhook配置 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
@Component
public class WebhookServerRepositoryImpl extends BaseRepositoryImpl<WebhookServer> implements WebhookServerRepository {

    @Autowired
    private WebhookServerMapper serverMapper;

    @Override
    @ProcessLovValue
    public Page<WebhookServer> pageWebHookList(PageRequest pageRequest, WebhookServer webhookServer, boolean includeSiteIfQueryByTenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> serverMapper.selectWebHookList(webhookServer, includeSiteIfQueryByTenantId));
    }

    @Override
    @ProcessLovValue
    public WebhookServer getWebHookDetails(Long tenantId, Long serverId) {
        return serverMapper.selectWebHookDetails(tenantId, serverId);
    }

    @Override
    public void validWebHookParams(WebhookServer webhookServer) {
        if (webhookServer.getSecret() != null && HmsgConstant.WebHookServerType.JSON.equals(webhookServer.getServerType())) {
            // 校验 webhook 秘钥格式，Json 秘钥格式为 Authorization:content
            Assert.isTrue(webhookServer.getSecret().contains(BaseConstants.Symbol.COLON), HmsgConstant.ErrorCode.WEBHOOK_JSON_SECRET_NOT_MATCH);
        }
        // 1. 校验Code唯一性
        if (webhookServer.getServerId() == null) {
            int codeCount = this.selectCount(new WebhookServer().setTenantId(webhookServer.getTenantId())
                            .setServerCode(webhookServer.getServerCode()));
            if (codeCount > 0) {
                throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
            }
        } else {
            WebhookServer dbWebhookServer = this.selectByPrimaryKey(webhookServer.getServerId());
            if (StringUtils.isBlank(webhookServer.getWebhookAddress()) || StringUtils
                            .equals(dbWebhookServer.getWebhookAddress(), webhookServer.getWebhookAddress())) {
                // 若未修改过地址则不校验(未修改地址时前端不会传递地址信息，或者传入的地址和原本的一致)
                webhookServer.setWebhookAddress(dbWebhookServer.getWebhookAddress());
                if (StringUtils.isBlank(webhookServer.getSecret())) {
                    // 更新时为传递秘钥，此时为未修改秘钥信息，将数据库中的秘钥信息塞进去
                    webhookServer.setSecret(dbWebhookServer.getSecret());
                }
                return;
            }
        }
        // 2. 校验 webhookAddress 唯一性
        int addrCount = this.selectCount(new WebhookServer().setTenantId(webhookServer.getTenantId())
                        .setServerType(webhookServer.getServerType())
                        .setWebhookAddress(webhookServer.getWebhookAddress()));
        if (addrCount > 0) {
            throw new CommonException(HmsgConstant.ErrorCode.WEBHOOK_ADDRESS_REPEAT, webhookServer.getTenantId(),
                            webhookServer.getServerType(), webhookServer.getWebhookAddress());
        }
    }

    @Override
    public WebhookServer getWebHookByCodeAndTenant(Long tenantId, String serverCode) {
        return selectOne(new WebhookServer().setTenantId(tenantId).setServerCode(serverCode));
    }
}
