package org.hzero.message.app.service.impl;

import java.util.Objects;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.dd.dto.TokenDTO;
import org.hzero.dd.service.DingTokenService;
import org.hzero.message.app.service.DingTalkServerService;
import org.hzero.message.domain.entity.DingTalkServer;
import org.hzero.message.domain.repository.DingTalkServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * 钉钉配置应用服务默认实现
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
@Service
public class DingTalkServerServiceImpl implements DingTalkServerService {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkServerServiceImpl.class);

    private final DingTalkServerRepository dingTalkServerRepository;
    private final DingTokenService dingTokenService;
    private final RedisHelper redisHelper;
    private final EncryptClient encryptClient;

    @Autowired
    public DingTalkServerServiceImpl(DingTalkServerRepository dingTalkServerRepository,
                                     DingTokenService dingTokenService,
                                     RedisHelper redisHelper,
                                     EncryptClient encryptClient) {
        this.dingTalkServerRepository = dingTalkServerRepository;
        this.dingTokenService = dingTokenService;
        this.redisHelper = redisHelper;
        this.encryptClient = encryptClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DingTalkServer addDingTalkServer(DingTalkServer dingTalkServer) {
        dingTalkServer.validate();
        // 唯一性校验
        Assert.isTrue(UniqueHelper.valid(dingTalkServer), BaseConstants.ErrorCode.DATA_EXISTS);
        // 密码解密
        dingTalkServer.setAppSecret(encryptClient.decrypt(dingTalkServer.getAppSecret()));
        // 开启数据加密
        DataSecurityHelper.open();
        dingTalkServerRepository.insertSelective(dingTalkServer);
        // 清理缓存
        DingTalkServer.clearCache(redisHelper, dingTalkServer.getTenantId(), dingTalkServer.getServerCode());
        return dingTalkServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DingTalkServer updateDingTalkServer(DingTalkServer dingTalkServer) {
        dingTalkServer.validate();
        if (StringUtils.hasText(dingTalkServer.getAppSecret())) {
            // 开启加密存储
            DataSecurityHelper.open();
            // 密码解密
            dingTalkServer.setAppSecret(encryptClient.decrypt(dingTalkServer.getAppSecret()));
            dingTalkServerRepository.updateOptional(dingTalkServer, DingTalkServer.FIELD_SERVER_NAME,
                    DingTalkServer.FIELD_AUTH_TYPE, DingTalkServer.FIELD_APP_KEY,
                    DingTalkServer.FIELD_APP_SECRET, DingTalkServer.FIELD_AUTH_ADDRESS,
                    DingTalkServer.FIELD_AGENT_ID, DingTalkServer.FIELD_ENABLED_FLAG);
        } else {
            dingTalkServerRepository.updateOptional(dingTalkServer, DingTalkServer.FIELD_SERVER_NAME,
                    DingTalkServer.FIELD_AUTH_TYPE, DingTalkServer.FIELD_APP_KEY,
                    DingTalkServer.FIELD_AUTH_ADDRESS, DingTalkServer.FIELD_AGENT_ID,
                    DingTalkServer.FIELD_ENABLED_FLAG);
        }
        // 清理缓存
        DingTalkServer.clearCache(redisHelper, dingTalkServer.getTenantId(), dingTalkServer.getServerCode());
        return dingTalkServer;
    }

    @Override
    public DingTalkServer detailDingTalkServer(Long tenantId, String serverCode) {
        DingTalkServer dingTalkServer = getDingTalkServerWithCache(tenantId, serverCode);
        if (dingTalkServer == null) {
            dingTalkServer = getDingTalkServerWithCache(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        }
        if (dingTalkServer != null) {
            try {
                dingTalkServer.setAppSecret(DataSecurityHelper.decrypt(dingTalkServer.getAppSecret()));
            } catch (Exception e) {
                logger.warn("DingTalk secret decrypt failed!");
            }
        }
        return dingTalkServer;
    }

    private DingTalkServer getDingTalkServerWithCache(Long tenantId, String serverCode) {
        // 查询租户缓存
        DingTalkServer dingTalkServer = DingTalkServer.getCache(redisHelper, tenantId, serverCode);
        if (dingTalkServer == null) {
            dingTalkServer = dingTalkServerRepository.selectOne(new DingTalkServer().setTenantId(tenantId).setServerCode(serverCode));
            if (dingTalkServer != null && Objects.equals(dingTalkServer.getEnabledFlag(), BaseConstants.Flag.YES)) {
                // 刷新缓存
                DingTalkServer.refreshCache(redisHelper, dingTalkServer);
            } else {
                dingTalkServer = null;
            }
        }
        return dingTalkServer;
    }

    @Override
    public String getToken(Long tenantId, String serverCode) {
        DingTalkServer dingTalkServer = detailDingTalkServer(tenantId, serverCode);
        Assert.notNull(dingTalkServer, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(Objects.equals(dingTalkServer.getEnabledFlag(), BaseConstants.Flag.YES),
                BaseConstants.ErrorCode.DATA_INVALID);
        switch (dingTalkServer.getAuthType()) {
            case HmsgConstant.DingTalkAuthType.DING_TALK:
                TokenDTO tokenDTO = dingTokenService.getTokenWithCache(dingTalkServer.getAppKey(), dingTalkServer.getAppSecret());
                if (StringUtils.hasText(tokenDTO.getAccess_token())) {
                    return tokenDTO.getAccess_token();
                } else {
                    throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
                }
            case HmsgConstant.DingTalkAuthType.THIRD:
                TokenDTO result = dingTokenService.getTokenFromThirdPart(dingTalkServer.getAuthAddress());
                if (result != null && StringUtils.hasText(result.getAccess_token())) {
                    return result.getAccess_token();
                } else {
                    throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
                }
            default:
                throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
        }
    }

    @Override
    public Long getDefaultAgentId(Long tenantId, String serverCode) {
        DingTalkServer dingTalkServer = detailDingTalkServer(tenantId, serverCode);
        Assert.notNull(dingTalkServer, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(Objects.equals(dingTalkServer.getEnabledFlag(), BaseConstants.Flag.YES),
                BaseConstants.ErrorCode.DATA_INVALID);
        return dingTalkServer.getAgentId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDingTalkServer(DingTalkServer dingTalkServer) {
        dingTalkServer.validateCodeExistPredefine(dingTalkServerRepository);
        dingTalkServerRepository.deleteByPrimaryKey(dingTalkServer.getServerId());
        // 删除缓存
        DingTalkServer.clearCache(redisHelper, dingTalkServer.getTenantId(), dingTalkServer.getServerCode());
    }
}
