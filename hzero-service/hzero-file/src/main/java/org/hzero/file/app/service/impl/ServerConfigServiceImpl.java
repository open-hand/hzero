package org.hzero.file.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.app.service.ServerConfigService;
import org.hzero.file.domain.entity.ServerConfig;
import org.hzero.file.domain.entity.ServerConfigLine;
import org.hzero.file.domain.repository.ServerConfigLineRepository;
import org.hzero.file.domain.repository.ServerConfigRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.mybatis.helper.UniqueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 服务器上传配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
@Service
public class ServerConfigServiceImpl implements ServerConfigService {

    private final ServerConfigRepository serverConfigRepository;
    private final ServerConfigLineRepository serverConfigLineRepository;

    @Autowired
    public ServerConfigServiceImpl(ServerConfigRepository serverConfigRepository,
                                   ServerConfigLineRepository serverConfigLineRepository) {
        this.serverConfigRepository = serverConfigRepository;
        this.serverConfigLineRepository = serverConfigLineRepository;
    }

    @Override
    public ServerConfig detailConfig(Long tenantId, Long configId) {
        ServerConfig serverConfig = serverConfigRepository.detailConfig(tenantId, configId);
        Assert.notNull(serverConfig, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        List<ServerConfigLine> configLineList = null;
        switch (serverConfig.getSourceType()) {
            case HfleConstant.SourceType.SERVER:
                configLineList = serverConfigLineRepository.listConfigLineWithServer(configId);
                break;
            case HfleConstant.SourceType.CLUSTER:
                configLineList = serverConfigLineRepository.listConfigLineWithCluster(configId);
                break;
            default:
                break;
        }
        return serverConfig.setServerConfigLineList(configLineList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerConfig createConfig(ServerConfig serverConfig) {
        Assert.isTrue(UniqueHelper.valid(serverConfig), BaseConstants.ErrorCode.DATA_EXISTS);
        serverConfigRepository.insertSelective(serverConfig);
        // 创建行
        if (CollectionUtils.isNotEmpty(serverConfig.getServerConfigLineList())) {
            serverConfig.getServerConfigLineList().forEach(item -> {
                item.setConfigId(serverConfig.getConfigId()).setTenantId(serverConfig.getTenantId());
                Assert.isTrue(UniqueHelper.valid(item), BaseConstants.ErrorCode.DATA_EXISTS);
                serverConfigLineRepository.insertSelective(item);
            });
        }
        return serverConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerConfig updateConfig(ServerConfig serverConfig) {
        serverConfigRepository.updateOptional(serverConfig,
                ServerConfig.FIELD_DESCRIPTION,
                ServerConfig.FIELD_ROOT_DIR,
                ServerConfig.FIELD_ENABLED_FLAG);
        if (CollectionUtils.isNotEmpty(serverConfig.getServerConfigLineList())) {
            serverConfig.getServerConfigLineList().forEach(item -> {
                if (item.getConfigLineId() == null) {
                    // 新建
                    item.setConfigId(serverConfig.getConfigId()).setTenantId(serverConfig.getTenantId());
                    serverConfigLineRepository.insertSelective(item);
                } else {
                    // 更新
                    serverConfigLineRepository.updateOptional(item,
                            ServerConfigLine.FIELD_ENABLED_FLAG,
                            ServerConfigLine.FIELD_ROOT_DIR);
                }
            });
        }
        return serverConfig;
    }
}
