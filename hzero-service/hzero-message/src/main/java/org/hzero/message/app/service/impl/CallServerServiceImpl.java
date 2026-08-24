package org.hzero.message.app.service.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.app.service.CallServerService;
import org.hzero.message.domain.entity.CallServer;
import org.hzero.message.domain.repository.CallServerRepository;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 语音消息服务应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
@Service
public class CallServerServiceImpl implements CallServerService {

    private static final Logger logger = LoggerFactory.getLogger(CallServerServiceImpl.class);

    @Autowired
    private CallServerRepository callServerRepository;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public CallServer createCallServer(CallServer callServer) {
        Assert.isTrue(UniqueHelper.valid(callServer), BaseConstants.ErrorCode.DATA_EXISTS);
        // 开启加密存储
        DataSecurityHelper.open();
        callServerRepository.insertSelective(callServer);
        // 清理缓存
        CallServer.clearCache(redisHelper, callServer.getTenantId(), callServer.getServerCode());
        return callServer;
    }

    @Override
    public CallServer updateCallServer(CallServer callServer) {
        if (StringUtils.isNotBlank(callServer.getAccessSecret())) {
            // 开启加密存储
            DataSecurityHelper.open();
            callServerRepository.updateOptional(callServer,
                    CallServer.FIELD_SERVER_NAME,
                    CallServer.FIELD_SERVER_TYPE_CODE,
                    CallServer.FIELD_ACCESS_KEY,
                    CallServer.FIELD_ACCESS_SECRET,
                    CallServer.FIELD_EXT_PARAM,
                    CallServer.FIELD_ENABLED_FLAG);
        } else {
            callServerRepository.updateOptional(callServer,
                    CallServer.FIELD_SERVER_NAME,
                    CallServer.FIELD_SERVER_TYPE_CODE,
                    CallServer.FIELD_ACCESS_KEY,
                    CallServer.FIELD_EXT_PARAM,
                    CallServer.FIELD_ENABLED_FLAG);
        }
        // 清理缓存
        CallServer.clearCache(redisHelper, callServer.getTenantId(), callServer.getServerCode());
        return callServer;
    }

    @Override
    public CallServer getCallServer(Long tenantId, String serverCode) {
        CallServer callServer = getServerWithCache(tenantId, serverCode);
        if (callServer == null) {
            // 查平台级
            callServer = getServerWithCache(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        }
        if (callServer != null) {
            // 解密
            try {
                callServer.setAccessSecret(DataSecurityHelper.decrypt(callServer.getAccessSecret()));
            } catch (Exception e) {
                logger.warn("Call accessSecret decrypt failed!");
            }
        }
        return callServer;
    }

    private CallServer getServerWithCache(Long tenantId, String serverCode) {
        // 查询租户缓存
        CallServer callServer = CallServer.getCache(redisHelper, tenantId, serverCode);
        if (callServer == null) {
            callServer = callServerRepository.selectOne(new CallServer().setTenantId(tenantId).setServerCode(serverCode));
            if (callServer != null && Objects.equals(callServer.getEnabledFlag(), BaseConstants.Flag.YES)) {
                // 刷新缓存
                CallServer.refreshCache(redisHelper, callServer);
            } else {
                callServer = null;
            }
        }
        return callServer;
    }
}
