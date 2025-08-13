package org.hzero.message.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.app.service.EmailServerService;
import org.hzero.message.domain.entity.EmailProperty;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.repository.EmailPropertyRepository;
import org.hzero.message.domain.repository.EmailServerRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 邮箱服务应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Service
public class EmailServerServiceImpl extends BaseServiceImpl<EmailServer> implements EmailServerService {

    private static Logger logger = LoggerFactory.getLogger(EmailServerServiceImpl.class);

    private EmailServerRepository emailServerRepository;
    private EmailPropertyRepository emailPropertyRepository;
    private RedisHelper redisHelper;
    private EncryptClient encryptClient;

    @Autowired
    public EmailServerServiceImpl(EmailServerRepository emailServerRepository,
                                  EmailPropertyRepository emailPropertyRepository, RedisHelper redisHelper,
                                  EncryptClient encryptClient) {
        this.emailServerRepository = emailServerRepository;
        this.emailPropertyRepository = emailPropertyRepository;
        this.redisHelper = redisHelper;
        this.encryptClient = encryptClient;
    }

    @Override
    public Page<EmailServer> listEmailServer(Long tenantId, String serverCode, String serverName, Integer enabledFlag,
                                             boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return emailServerRepository.selectEmailServer(tenantId, serverCode, serverName, enabledFlag, includeSiteIfQueryByTenantId, pageRequest);
    }

    @Override
    public EmailServer getEmailServer(Long tenantId, long serverId) {
        EmailServer emailServer = emailServerRepository
                .selectOne(new EmailServer().setTenantId(tenantId).setServerId(serverId))
                .setEmailProperties(emailPropertyRepository.select(EmailProperty.FIELD_SERVER_ID, serverId));
        emailServer.setPasswordEncrypted(null);
        return emailServer;
    }

    @Override
    public EmailServer getEmailServer(long tenantId, String serverCode) {
        // 查询租户缓存
        EmailServer emailServer = getEmailServerWithCache(tenantId, serverCode);
        if (emailServer == null) {
            // 查询平台级
            emailServer = getEmailServerWithCache(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        }
        // 解密邮箱密码
        if (emailServer != null) {
            try {
                emailServer.setPasswordEncrypted(DataSecurityHelper.decrypt(emailServer.getPasswordEncrypted()));
            } catch (Exception e) {
                logger.warn("Email password decrypt failed!");
            }
        }
        return emailServer;
    }

    private EmailServer getEmailServerWithCache(long tenantId, String serverCode) {
        EmailServer emailServer = EmailServer.getCache(redisHelper, tenantId, serverCode);
        if (emailServer == null) {
            emailServer = emailServerRepository
                    .selectOne(new EmailServer().setTenantId(tenantId).setServerCode(serverCode));
            if (emailServer != null && Objects.equals(emailServer.getEnabledFlag(), BaseConstants.Flag.YES)) {
                emailServer.setEmailProperties(emailPropertyRepository.select(EmailProperty.FIELD_SERVER_ID,
                        emailServer.getServerId()));
                // 刷新缓存
                EmailServer.refreshCache(redisHelper, emailServer);
            } else {
                emailServer = null;
            }
        }
        return emailServer;
    }

    @Override
    public List<EmailProperty> listEmailProperty(Long tenantId, long serverId) {
        return emailPropertyRepository.select(new EmailProperty().setTenantId(tenantId).setServerId(serverId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailServer createEmailServer(EmailServer emailServer) {
        emailServer.validateServerCodeRepeat(emailServerRepository);
        // 密码解密
        emailServer.setPasswordEncrypted(encryptClient.decrypt(emailServer.getPasswordEncrypted()));
        DataSecurityHelper.open();
        emailServerRepository.insert(emailServer);
        emailServer.setEmailProperties(saveEmailProperty(emailServer.getServerId(), emailServer.getEmailProperties()));
        // 清除缓存
        EmailServer.clearCache(redisHelper, emailServer.getTenantId(), emailServer.getServerCode());
        return emailServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailServer updateEmailServer(EmailServer emailServer) {
        if (StringUtils.hasText(emailServer.getPasswordEncrypted())) {
            // 开启加密存储
            DataSecurityHelper.open();
            // 密码解密
            emailServer.setPasswordEncrypted(encryptClient.decrypt(emailServer.getPasswordEncrypted()));
            emailServerRepository.updateOptional(emailServer, EmailServer.FIELD_SERVER_NAME, EmailServer.FIELD_HOST,
                    EmailServer.FIELD_PORT, EmailServer.FIELD_PROTOCOL, EmailServer.FIELD_TRY_TIMES,
                    EmailServer.FIELD_USERNAME, EmailServer.FIELD_PASS_WORD, EmailServer.FIELD_SENDER,
                    EmailServer.FIELD_FILTER_STRATEGY, EmailServer.FIELD_ENABLED_FLAG);
        } else {
            emailServerRepository.updateOptional(emailServer, EmailServer.FIELD_SERVER_NAME, EmailServer.FIELD_HOST,
                    EmailServer.FIELD_PORT, EmailServer.FIELD_PROTOCOL, EmailServer.FIELD_TRY_TIMES,
                    EmailServer.FIELD_USERNAME, EmailServer.FIELD_SENDER, EmailServer.FIELD_FILTER_STRATEGY,
                    EmailServer.FIELD_ENABLED_FLAG);
        }
        emailServer.setEmailProperties(saveEmailProperty(emailServer.getServerId(), emailServer.getEmailProperties()));
        // 清除缓存
        EmailServer.clearCache(redisHelper, emailServer.getTenantId(), emailServer.getServerCode());
        return emailServer;
    }

    @Override
    public List<EmailServer> listEmailServersByCode(String serverCode, Long tenantId) {
        // 解密
        DataSecurityHelper.open();
        List<EmailServer> emailServerList = emailServerRepository.selectByCondition(Condition.builder(EmailServer.class)
                .notSelect(AuditDomain.FIELD_LAST_UPDATED_BY, AuditDomain.FIELD_LAST_UPDATE_DATE,
                        AuditDomain.FIELD_CREATION_DATE, AuditDomain.FIELD_CREATED_BY)
                .andWhere(Sqls.custom().andEqualTo(EmailServer.FIELD_SERVER_CODE, serverCode)
                        .andEqualTo(EmailServer.FIELD_TENANT_ID, tenantId, true))
                .build());
        if (!CollectionUtils.isEmpty(emailServerList)) {
            emailServerList.forEach(emailServer -> emailServer.setEmailProperties(
                    emailPropertyRepository.select(EmailProperty.FIELD_SERVER_ID, emailServer.getServerId())));
        }
        return emailServerList;
    }

    /**
     * 保存邮箱配置
     *
     * @param serverId          邮箱服务器信息ID
     * @param emailPropertyList 邮箱配置列表
     * @return 邮箱配置列表
     */
    private List<EmailProperty> saveEmailProperty(long serverId, List<EmailProperty> emailPropertyList) {
        emailPropertyRepository.delete(new EmailProperty().setServerId(serverId));
        if (!CollectionUtils.isEmpty(emailPropertyList)) {
            emailPropertyList.forEach(item -> emailPropertyRepository.insert(item.setServerId(serverId)));
        }
        return emailPropertyList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmailServer(EmailServer emailServer) {
        emailServer.validateCodeExistPredefined(emailServerRepository);
        emailServerRepository.deleteByPrimaryKey(emailServer.getServerId());
        // 清除缓存
        EmailServer.clearCache(redisHelper, emailServer.getTenantId(), emailServer.getServerCode());
    }
}
