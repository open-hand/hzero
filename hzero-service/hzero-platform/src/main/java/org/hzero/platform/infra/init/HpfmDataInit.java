package org.hzero.platform.infra.init;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.mybatis.pagehelper.Dialect;

import org.hzero.boot.platform.encrypt.EncryptRepository;
import org.hzero.platform.app.service.DatasourceInfoService;
import org.hzero.platform.domain.repository.*;
import org.hzero.platform.domain.service.CodeRuleDomainService;
import org.hzero.platform.domain.service.PromptDomainService;
import org.hzero.platform.infra.properties.PlatformProperties;

/**
 * <p>
 * 应用启动时缓存初始化数据
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/11 19:15
 */
@Component
public class HpfmDataInit implements SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(HpfmDataInit.class);

    private final PlatformProperties platformProperties;

    private final ProfileValueRepository profileValueRepository;

    private final ConfigRepository configRepository;

    private final PromptDomainService promptDomainService;

    private final CodeRuleDomainService codeRuleDomainService;

    private final PermissionRangeRepository permissionRangeRepository;

    private final ResponseMessageRepository responseMessageRepository;

    private final DatasourceInfoService datasourceRelService;

    private final EncryptRepository encryptRepository;

    /**
     * 通用模板仓库对象
     */
    private final CommonTemplateRepository commonTemplateRepository;

    @Autowired
    public HpfmDataInit(ProfileValueRepository profileValueRepository, ConfigRepository configRepository,
                        PromptDomainService promptDomainService, CodeRuleDomainService codeRuleDomainService,
                        PermissionRangeRepository permissionRangeRepository, DatasourceInfoService datasourceRelService,
                        PlatformProperties platformProperties, EncryptRepository encryptRepository,
                        ResponseMessageRepository responseMessageRepository,
                        CommonTemplateRepository commonTemplateRepository, Dialect dialect) {
        this.profileValueRepository = profileValueRepository;
        this.configRepository = configRepository;
        this.promptDomainService = promptDomainService;
        this.codeRuleDomainService = codeRuleDomainService;
        this.permissionRangeRepository = permissionRangeRepository;
        this.datasourceRelService = datasourceRelService;
        this.platformProperties = platformProperties;
        this.encryptRepository = encryptRepository;
        this.responseMessageRepository = responseMessageRepository;
        this.commonTemplateRepository = commonTemplateRepository;
    }

    @Override
    public void afterSingletonsInstantiated() {
        initCacheData(false);
    }

    public void initCacheData(boolean forceRefresh) {
        LOGGER.info("Start init redis cache. forceRefresh: {}", forceRefresh);

        // 缓存公钥和私钥
        cacheEncryptKey();

        if (!BooleanUtils.isTrue(platformProperties.getInitCache())) {
            return;
        }
        ExecutorService executorService =
                Executors.newFixedThreadPool(12, new ThreadFactoryBuilder().setNameFormat("HpfmDataInit-%d").build());

        if (forceRefresh || platformProperties.getCache().isProfileValue()) {
            executorService.submit(profileValueRepository::initAllProfileValueToRedis);
        }
        if (forceRefresh || platformProperties.getCache().isConfig()) {
            executorService.submit(configRepository::initAllConfigToRedis);
        }
        if (forceRefresh || platformProperties.getCache().isPrompt()) {
            executorService.submit(() -> promptDomainService.initAllPromptCacheValue(true));
        }
        if (forceRefresh || platformProperties.getCache().isReturnMessage()) {
            executorService.submit(responseMessageRepository::initAllReturnMessageToRedis);
        }
        if (forceRefresh || platformProperties.getCache().isCodeRule()) {
            executorService.submit(codeRuleDomainService::initCodeRuleCache);
        }
        if (forceRefresh || platformProperties.getCache().isPermissionRange()) {
            executorService.submit(permissionRangeRepository::initAllData);
        }
        if (forceRefresh || platformProperties.getCache().isDatasource()) {
            executorService.submit(datasourceRelService::initAllData);
        }
        if (forceRefresh || platformProperties.getCache().isCommonTemplate()) {
            executorService.submit(this.commonTemplateRepository::cacheAll);
        }

        executorService.shutdown();

        LOGGER.info("Finish init redis cache.");
    }

    /**
     * 缓存加密的私钥和公钥
     */
    private void cacheEncryptKey() {
        PlatformProperties.Encrypt encrypt = platformProperties.getEncrypt();
        encryptRepository.savePublicKey(encrypt.getPublicKey());
        encryptRepository.savePrivateKey(encrypt.getPrivateKey());
        LOGGER.info("Init encrypt publicKey and privateKey. \n publicKey is [{}] \n privateKey is [{}]",
                        encrypt.getPublicKey(), encrypt.getPrivateKey());
    }
}
