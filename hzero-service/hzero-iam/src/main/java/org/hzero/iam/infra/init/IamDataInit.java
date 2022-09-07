package org.hzero.iam.infra.init;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.mybatis.pagehelper.Dialect;

import org.hzero.iam.app.service.FieldPermissionService;
import org.hzero.iam.app.service.OpenAppService;
import org.hzero.iam.config.IamProperties;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.AuthorityDomainService;

/**
 * <p>
 * 应用启动时缓存初始化数据
 * </p>
 *
 * @author jiaxu.cui@hand-china.com
 */
@Component
public class IamDataInit implements SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(IamDataInit.class);

    private final OpenAppService openAppService;
    private final PasswordPolicyRepository passwordPolicyRepository;
    private final UserRepository userRepository;
    private final LdapRepository ldapRepository;
    private final ClientRepository clientRepository;
    private final FieldPermissionService fieldPermissionService;
    private final AuthorityDomainService authorityDomainService;
    private final DomainRepository domainRepository;
    private final IamProperties properties;

    @Autowired
    public IamDataInit(OpenAppService openAppService,
                       PasswordPolicyRepository passwordPolicyRepository,
                       UserRepository userRepository,
                       LdapRepository ldapRepository,
                       ClientRepository clientRepository,
                       FieldPermissionService fieldPermissionService,
                       AuthorityDomainService authorityDomainService,
                       DomainRepository domainRepository,
                       IamProperties properties,
                       Dialect dialect) {
        this.openAppService = openAppService;
        this.passwordPolicyRepository = passwordPolicyRepository;
        this.userRepository = userRepository;
        this.ldapRepository = ldapRepository;
        this.clientRepository = clientRepository;
        this.fieldPermissionService = fieldPermissionService;
        this.authorityDomainService = authorityDomainService;
        this.domainRepository = domainRepository;
        this.properties = properties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        initCacheData(false);
    }

    public void initCacheData(boolean forceRefresh) {
        LOGGER.info("Start init redis cache.");

        ExecutorService executorService = Executors.newFixedThreadPool(12, new ThreadFactoryBuilder().setNameFormat("HiamRedisInit-%d").build());
        if (forceRefresh || properties.getInitCache().isOpenLoginWay()) {
            executorService.submit(openAppService::saveOpenAppCache);
        }

        if (forceRefresh || properties.getInitCache().isPasswordPolicy()) {
            executorService.submit(passwordPolicyRepository::initCachePasswordPolicy);
        }

        if (forceRefresh || properties.getInitCache().isLdap()) {
            executorService.submit(ldapRepository::initCacheLdap);
        }

        if (forceRefresh || properties.getInitCache().isClient()) {
            executorService.submit(clientRepository::initCacheClient);
        }

        if (forceRefresh || properties.getInitCache().isUser()) {
            executorService.submit(userRepository::initUsers);
        }

        if (forceRefresh || properties.getInitCache().isFieldPermission()) {
            executorService.submit(fieldPermissionService::restorePermission);
        }

        if (forceRefresh || properties.getInitCache().isDocAuth()) {
            executorService.submit(authorityDomainService::initDocAuthCache);
        }

        if (forceRefresh || properties.getInitCache().isDomain()) {
            executorService.submit(domainRepository::initCacheDomain);
        }
        executorService.shutdown();
        LOGGER.info("Finish init redis cache.");
    }

}
