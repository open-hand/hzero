package org.hzero.message.app.service.impl;

import java.util.Objects;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.app.service.WeChatEnterpriseService;
import org.hzero.message.domain.entity.WeChatEnterprise;
import org.hzero.message.domain.repository.WeChatEnterpriseRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.wechat.enterprise.dto.TokenDTO;
import org.hzero.wechat.enterprise.enums.SecretTypeEnum;
import org.hzero.wechat.enterprise.service.WechatTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * 企业微信配置应用服务默认实现
 *
 * @author fanghan.liu@hand-china.com 2019-10-15 14:31:46
 */
@Service
public class WeChatEnterpriseServiceImpl implements WeChatEnterpriseService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatEnterpriseServiceImpl.class);

    private final WeChatEnterpriseRepository wechatEnterpriseRepository;
    private final WechatTokenService wechatTokenService;
    private final RedisHelper redisHelper;
    private final EncryptClient encryptClient;

    @Autowired
    public WeChatEnterpriseServiceImpl(WeChatEnterpriseRepository wechatEnterpriseRepository,
                                       WechatTokenService wechatTokenService,
                                       RedisHelper redisHelper,
                                       EncryptClient encryptClient) {
        this.wechatEnterpriseRepository = wechatEnterpriseRepository;
        this.wechatTokenService = wechatTokenService;
        this.redisHelper = redisHelper;
        this.encryptClient = encryptClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeChatEnterprise updateWeChatEnterprise(WeChatEnterprise weChatEnterprise) {
        if (StringUtils.hasText(weChatEnterprise.getCorpsecret())) {
            // 开启加密存储
            DataSecurityHelper.open();
            // 密码解密
            weChatEnterprise.setCorpsecret(encryptClient.decrypt(weChatEnterprise.getCorpsecret()));
            wechatEnterpriseRepository.updateOptional(weChatEnterprise, WeChatEnterprise.FIELD_SERVER_NAME,
                    WeChatEnterprise.FIELD_AUTH_TYPE, WeChatEnterprise.FIELD_CORPID,
                    WeChatEnterprise.FIELD_CORPSECRET, WeChatEnterprise.FIELD_AUTH_ADDRESS,
                    WeChatEnterprise.FIELD_CALLBACK_URL, WeChatEnterprise.FIELD_ENABLED_FLAG,
                    WeChatEnterprise.FIELD_AGENT_ID);
        } else {
            wechatEnterpriseRepository.updateOptional(weChatEnterprise, WeChatEnterprise.FIELD_SERVER_NAME,
                    WeChatEnterprise.FIELD_AUTH_TYPE, WeChatEnterprise.FIELD_CORPID,
                    WeChatEnterprise.FIELD_AUTH_ADDRESS, WeChatEnterprise.FIELD_CALLBACK_URL,
                    WeChatEnterprise.FIELD_ENABLED_FLAG, WeChatEnterprise.FIELD_AGENT_ID);
        }
        // 清除缓存
        WeChatEnterprise.clearCache(redisHelper, weChatEnterprise.getTenantId(), weChatEnterprise.getServerCode());
        return weChatEnterprise;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeChatEnterprise createWeChatEnterprise(WeChatEnterprise weChatEnterprise) {
        // 唯一性校验
        Assert.isTrue(UniqueHelper.valid(weChatEnterprise), BaseConstants.ErrorCode.DATA_EXISTS);
        // 密码解密
        weChatEnterprise.setCorpsecret(encryptClient.decrypt(weChatEnterprise.getCorpsecret()));
        // 开启加密
        DataSecurityHelper.open();
        wechatEnterpriseRepository.insertSelective(weChatEnterprise);
        // 清除缓存
        WeChatEnterprise.clearCache(redisHelper, weChatEnterprise.getTenantId(), weChatEnterprise.getServerCode());
        return weChatEnterprise;
    }

    @Override
    public WeChatEnterprise detailWeChatEnterprise(Long tenantId, String serverCode) {
        WeChatEnterprise weChatEnterprise = getWeChatEnterpriseWithCache(tenantId, serverCode);
        if (weChatEnterprise == null) {
            weChatEnterprise = getWeChatEnterpriseWithCache(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        }
        if (weChatEnterprise != null) {
            try {
                weChatEnterprise.setCorpsecret(DataSecurityHelper.decrypt(weChatEnterprise.getCorpsecret()));
            } catch (Exception e) {
                logger.warn("weChatEnterprise password decrypt failed!");
            }
        }
        return weChatEnterprise;
    }

    private WeChatEnterprise getWeChatEnterpriseWithCache(Long tenantId, String serverCode) {
        // 查询租户缓存
        WeChatEnterprise weChatEnterprise = WeChatEnterprise.getCache(redisHelper, tenantId, serverCode);
        if (weChatEnterprise == null) {
            weChatEnterprise = wechatEnterpriseRepository.selectOne(new WeChatEnterprise().setTenantId(tenantId).setServerCode(serverCode));
            if (weChatEnterprise != null && Objects.equals(weChatEnterprise.getEnabledFlag(), BaseConstants.Flag.YES)) {
                // 刷新缓存
                WeChatEnterprise.refreshCache(redisHelper, weChatEnterprise);
            } else {
                weChatEnterprise = null;
            }
        }
        return weChatEnterprise;
    }

    @Override
    public String getToken(Long tenantId, String serverCode) {
        WeChatEnterprise weChatEnterprise = detailWeChatEnterprise(tenantId, serverCode);
        Assert.notNull(weChatEnterprise, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(Objects.equals(weChatEnterprise.getEnabledFlag(), BaseConstants.Flag.YES),
                BaseConstants.ErrorCode.DATA_INVALID);
        switch (weChatEnterprise.getAuthType()) {
            case HmsgConstant.WeChatAuthType.WE_CHAT:
                TokenDTO tokenDTO = wechatTokenService.getTokenWithCache(weChatEnterprise.getCorpid(),
                        weChatEnterprise.getCorpsecret(), SecretTypeEnum.APP);
                if (StringUtils.hasText(tokenDTO.getAccess_token())) {
                    return tokenDTO.getAccess_token();
                } else {
                    throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
                }
            case HmsgConstant.WeChatAuthType.THIRD:
                TokenDTO result = wechatTokenService.getTokenFromThirdPart(weChatEnterprise.getAuthAddress());
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
        WeChatEnterprise weChatEnterprise = detailWeChatEnterprise(tenantId, serverCode);
        Assert.notNull(weChatEnterprise, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(Objects.equals(weChatEnterprise.getEnabledFlag(), BaseConstants.Flag.YES),
                BaseConstants.ErrorCode.DATA_INVALID);
        return weChatEnterprise.getAgentId();
    }

    @Override
    public void deleteWeChatEnterprise(WeChatEnterprise weChatEnterprise) {
        weChatEnterprise.validateCodeExistPredefined(wechatEnterpriseRepository);
        wechatEnterpriseRepository.deleteByPrimaryKey(weChatEnterprise.getServerId());
        // 删除缓存
        WeChatEnterprise.clearCache(redisHelper, weChatEnterprise.getTenantId(), weChatEnterprise.getServerCode());
    }
}
