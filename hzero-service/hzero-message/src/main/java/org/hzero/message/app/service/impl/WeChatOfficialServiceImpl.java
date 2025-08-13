package org.hzero.message.app.service.impl;

import java.util.Objects;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.app.service.WeChatOfficialService;
import org.hzero.message.domain.entity.WechatOfficial;
import org.hzero.message.domain.repository.WeChatOfficialRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.wechat.dto.TokenDTO;
import org.hzero.wechat.service.BaseWechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * 微信公众号配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
@Service
public class WeChatOfficialServiceImpl implements WeChatOfficialService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatOfficialServiceImpl.class);

    private final RedisHelper redisHelper;
    private final EncryptClient encryptClient;
    private final BaseWechatService weChatService;
    private final WeChatOfficialRepository accountRepository;

    @Autowired
    public WeChatOfficialServiceImpl(RedisHelper redisHelper,
                                     EncryptClient encryptClient,
                                     BaseWechatService weChatService,
                                     WeChatOfficialRepository accountRepository) {
        this.redisHelper = redisHelper;
        this.encryptClient = encryptClient;
        this.weChatService = weChatService;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatOfficial addOfficial(WechatOfficial official) {
        official.validate();
        // 唯一性校验
        Assert.isTrue(UniqueHelper.valid(official), BaseConstants.ErrorCode.DATA_EXISTS);
        // 密码解密
        official.setSecret(encryptClient.decrypt(official.getSecret()));
        // 开启数据加密
        DataSecurityHelper.open();
        accountRepository.insertSelective(official);
        // 清理缓存
        WechatOfficial.clearCache(redisHelper, official.getTenantId(), official.getServerCode());
        return official;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatOfficial updateOfficial(WechatOfficial official) {
        official.validate();
        if (StringUtils.hasText(official.getSecret())) {
            // 开启加密存储
            DataSecurityHelper.open();
            // 密码解密
            official.setSecret(encryptClient.decrypt(official.getSecret()));
            accountRepository.updateOptional(official, WechatOfficial.FIELD_SERVER_NAME, WechatOfficial.FIELD_AUTH_TYPE,
                    WechatOfficial.FIELD_APPID, WechatOfficial.FIELD_SECRET, WechatOfficial.FIELD_AUTH_ADDRESS,
                    WechatOfficial.FIELD_ENABLED_FLAG);
        } else {
            accountRepository.updateOptional(official, WechatOfficial.FIELD_SERVER_NAME, WechatOfficial.FIELD_AUTH_TYPE,
                    WechatOfficial.FIELD_APPID, WechatOfficial.FIELD_AUTH_ADDRESS,
                    WechatOfficial.FIELD_ENABLED_FLAG);
        }
        // 清理缓存
        WechatOfficial.clearCache(redisHelper, official.getTenantId(), official.getServerCode());
        return official;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOfficial(WechatOfficial official) {
        official.validateCodeExistPredefined(accountRepository);
        accountRepository.deleteByPrimaryKey(official.getServerId());
        // 清理缓存
        WechatOfficial.clearCache(redisHelper, official.getTenantId(), official.getServerCode());
    }

    @Override
    public WechatOfficial getOfficial(Long tenantId, String serverCode) {
        WechatOfficial official = getWeChatOfficialWithCache(tenantId, serverCode);
        if (official == null) {
            official = getWeChatOfficialWithCache(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        }
        if (official != null) {
            try {
                official.setSecret(DataSecurityHelper.decrypt(official.getSecret()));
            } catch (Exception e) {
                logger.warn("WeChatOfficial secret decrypt failed!");
            }
        }
        return official;
    }

    private WechatOfficial getWeChatOfficialWithCache(Long tenantId, String serverCode) {
        // 查询租户缓存
        WechatOfficial official = WechatOfficial.getCache(redisHelper, tenantId, serverCode);
        if (official == null) {
            official = accountRepository.selectOne(new WechatOfficial().setTenantId(tenantId).setServerCode(serverCode));
            if (official != null && Objects.equals(official.getEnabledFlag(), BaseConstants.Flag.YES)) {
                // 刷新缓存
                WechatOfficial.refreshCache(redisHelper, official);
            } else {
                official = null;
            }
        }
        return official;
    }

    @Override
    public String getToken(Long tenantId, String serverCode) {
        WechatOfficial wechatOfficial = getOfficial(tenantId, serverCode);
        Assert.notNull(wechatOfficial, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(Objects.equals(wechatOfficial.getEnabledFlag(), BaseConstants.Flag.YES), BaseConstants.ErrorCode.DATA_INVALID);
        switch (wechatOfficial.getAuthType()) {
            case HmsgConstant.WeChatAuthType.WE_CHAT:
                TokenDTO tokenDTO = weChatService.getTokenWithCache(wechatOfficial.getAppid(), wechatOfficial.getSecret());
                if (StringUtils.hasText(tokenDTO.getAccess_token())) {
                    return tokenDTO.getAccess_token();
                } else {
                    throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
                }
            case HmsgConstant.WeChatAuthType.THIRD:
                TokenDTO result = weChatService.getTokenFromThirdPart(wechatOfficial.getAuthAddress());
                if (result != null && StringUtils.hasText(result.getAccess_token())) {
                    return result.getAccess_token();
                } else {
                    throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
                }
            default:
                throw new CommonException(HmsgConstant.ErrorCode.GET_TOKEN);
        }
    }
}
