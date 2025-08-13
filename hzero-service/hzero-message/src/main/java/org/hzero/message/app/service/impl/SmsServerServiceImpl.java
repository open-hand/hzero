package org.hzero.message.app.service.impl;

import java.util.Objects;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.app.service.SmsServerService;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.message.domain.repository.SmsServerRepository;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 短信服务应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Service
public class SmsServerServiceImpl implements SmsServerService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServerServiceImpl.class);

    private final SmsServerRepository smsServerRepository;
    private final RedisHelper redisHelper;
    private final EncryptClient encryptClient;

    @Autowired
    public SmsServerServiceImpl(SmsServerRepository smsServerRepository,
                                RedisHelper redisHelper,
                                EncryptClient encryptClient) {
        this.smsServerRepository = smsServerRepository;
        this.redisHelper = redisHelper;
        this.encryptClient = encryptClient;
    }

    @Override
    public Page<SmsServer> listSmsServer(Long tenantId, String serverCode, String serverName, String serverTypeCode,
                                         Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return smsServerRepository.selectSmsServer(tenantId, serverCode, serverName, serverTypeCode, enabledFlag, includeSiteIfQueryByTenantId,
                pageRequest);
    }

    @Override
    public SmsServer getSmsServer(Long tenantId, long serverId) {
        SmsServer smsServer =
                smsServerRepository.selectOne(new SmsServer().setServerId(serverId).setTenantId(tenantId));
        Assert.notNull(smsServer, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        smsServer.setAccessKeySecret(null);
        return smsServer;
    }

    @Override
    public SmsServer getSmsServer(long tenantId, String serverCode) {
        SmsServer smsServer = getSmsServerWithCache(tenantId, serverCode);
        if (smsServer == null) {
            // 查平台级
            smsServer = getSmsServerWithCache(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        }
        if (smsServer != null) {
            // 解密
            try {
                smsServer.setAccessKeySecret(DataSecurityHelper.decrypt(smsServer.getAccessKeySecret()));
            } catch (Exception e) {
                logger.warn("Sms accessKeySecret decrypt failed!");
            }
        }
        return smsServer;
    }

    private SmsServer getSmsServerWithCache(Long tenantId, String serverCode) {
        // 查询租户缓存
        SmsServer smsServer = SmsServer.getCache(redisHelper, tenantId, serverCode);
        if (smsServer == null) {
            smsServer = smsServerRepository.selectOne(new SmsServer().setTenantId(tenantId).setServerCode(serverCode));
            if (smsServer != null && Objects.equals(smsServer.getEnabledFlag(), BaseConstants.Flag.YES)) {
                // 刷新缓存
                SmsServer.refreshCache(redisHelper, smsServer);
            } else {
                smsServer = null;
            }
        }
        return smsServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsServer createSmsServer(SmsServer smsServer) {
        smsServer.validateServerCodeRepeat(smsServerRepository);
        // 密码解密
        smsServer.setAccessKeySecret(encryptClient.decrypt(smsServer.getAccessKeySecret()));
        DataSecurityHelper.open();
        smsServerRepository.insert(smsServer);
        // 清理缓存
        SmsServer.clearCache(redisHelper, smsServer.getTenantId(), smsServer.getServerCode());
        return smsServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsServer updateSmsServer(SmsServer smsServer) {
        if (StringUtils.hasText(smsServer.getAccessKeySecret())) {
            // 开启加密存储
            DataSecurityHelper.open();
            // 密码解密
            smsServer.setAccessKeySecret(encryptClient.decrypt(smsServer.getAccessKeySecret()));
            smsServerRepository.updateOptional(smsServer, SmsServer.FIELD_SERVER_NAME, SmsServer.FIELD_SERVER_TYPE_CODE,
                    SmsServer.FIELD_END_POINT, SmsServer.FIELD_ACCESS_KEY, SmsServer.FIELD_ACCESS_KEY_SECRET,
                    SmsServer.FIELD_SIGN_NAME, SmsServer.FIELD_ENABLED_FLAG);
        } else {
            smsServerRepository.updateOptional(smsServer, SmsServer.FIELD_SERVER_NAME, SmsServer.FIELD_SERVER_TYPE_CODE,
                    SmsServer.FIELD_END_POINT, SmsServer.FIELD_ACCESS_KEY, SmsServer.FIELD_SIGN_NAME,
                    SmsServer.FIELD_ENABLED_FLAG);
        }
        // 清理缓存
        SmsServer.clearCache(redisHelper, smsServer.getTenantId(), smsServer.getServerCode());
        return smsServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSmsServer(SmsServer smsServer) {
        smsServer.validateCodeExistPredefined(smsServerRepository);
        smsServerRepository.deleteByPrimaryKey(smsServer.getServerId());
        // 删除缓存
        SmsServer.clearCache(redisHelper, smsServer.getTenantId(), smsServer.getServerCode());
    }
}
