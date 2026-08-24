package org.hzero.iam.app.service.impl;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseAppService;
import org.hzero.iam.api.dto.LdapAccountDTO;
import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.app.service.LdapService;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.iam.domain.entity.LdapSyncConfig;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.iam.domain.repository.LdapRepository;
import org.hzero.iam.domain.service.ldap.LdapConnectService;
import org.hzero.iam.domain.service.ldap.LdapSyncConfigDomainService;
import org.hzero.iam.domain.service.ldap.LdapSyncUserTask;
import org.hzero.iam.domain.service.ldap.LdapUserService;
import org.hzero.iam.domain.service.ldap.impl.LdapConnectServiceImpl;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.LdapSyncType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

import io.choerodon.core.exception.CommonException;

/**
 * Ldap 应用服务实现
 *
 * @author bojiangzhou 2019/08/02
 */
@Service
public class LdapServiceImpl extends BaseAppService implements LdapService {

    private LdapRepository ldapRepository;
    private LdapConnectService ldapConnectService;
    private LdapSyncUserTask ldapSyncUserTask;
    private LdapSyncUserTask.FinishFallback finishFallback;
    private LdapHistoryRepository ldapHistoryRepository;
    private EncryptClient encryptClient;
    private LdapUserService ldapUserService;
    private LdapSyncConfigDomainService ldapSyncConfigDomainService;

    public LdapServiceImpl(LdapRepository ldapRepository, LdapConnectService ldapConnectService,
                    LdapSyncUserTask ldapSyncUserTask, LdapSyncUserTask.FinishFallback finishFallback,
                    LdapHistoryRepository ldapHistoryRepository, EncryptClient encryptClient,
                    LdapUserService ldapUserService, LdapSyncConfigDomainService ldapSyncConfigDomainService) {
        this.ldapRepository = ldapRepository;
        this.ldapConnectService = ldapConnectService;
        this.ldapSyncUserTask = ldapSyncUserTask;
        this.finishFallback = finishFallback;
        this.ldapHistoryRepository = ldapHistoryRepository;
        this.encryptClient = encryptClient;
        this.ldapUserService = ldapUserService;
        this.ldapSyncConfigDomainService = ldapSyncConfigDomainService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Ldap create(Ldap ldap) {
        ldap.createValidate();
        // 密码解密
        ldap.setLdapPassword(encryptClient.decrypt(ldap.getLdapPassword()));
        ldapRepository.insertSelective(ldap);
        // 添加Ldap缓存
        ldapRepository.cacheLdap(ldap);
        return ldap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Ldap ldap) {
        Ldap exist = ldapRepository.selectLdap(ldap.getOrganizationId(), ldap.getId());
        if (exist == null) {
            throw new CommonException(Constants.ErrorCode.LDAP_NOT_EXIST_EXCEPTION);
        }
        ldap.createValidate();
        // 密码解密
        ldap.setLdapPassword(encryptClient.decrypt(ldap.getLdapPassword()));
        ldapRepository.updateByPrimaryKey(ldap);
        // 更新Ldap缓存
        ldapRepository.cacheLdap(ldap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Ldap ldap) {
        Ldap exist = ldapRepository.selectLdap(ldap.getOrganizationId(), ldap.getId());
        if (exist == null) {
            throw new CommonException(Constants.ErrorCode.LDAP_NOT_EXIST_EXCEPTION);
        }
        ldapRepository.deleteByPrimaryKey(ldap);
        // 删除Ldap缓存
        ldapRepository.deleteLdap(ldap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableLdap(Ldap ldap) {
        Ldap exist = ldapRepository.selectLdap(ldap.getOrganizationId(), ldap.getId());
        if (exist == null) {
            throw new CommonException(Constants.ErrorCode.LDAP_NOT_EXIST_EXCEPTION);
        }
        ldap.setEnabled(true);
        ldapRepository.updateOptional(ldap, Ldap.FIELD_ENABLED);
        // 更新缓存
        ldapRepository.cacheLdap(ldap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableLdap(Ldap ldap) {
        Ldap exist = ldapRepository.selectLdap(ldap.getOrganizationId(), ldap.getId());
        if (exist == null) {
            throw new CommonException(Constants.ErrorCode.LDAP_NOT_EXIST_EXCEPTION);
        }
        ldap.setEnabled(false);
        ldapRepository.updateOptional(ldap, Ldap.FIELD_ENABLED);
        // 删除缓存
        ldapRepository.deleteLdap(ldap);
    }

    @Override
    public LdapConnectionDTO testConnect(Long organizationId, Long ldapId, LdapAccountDTO ldapAccount) {
        Ldap ldap = ldapRepository.selectLdap(organizationId, ldapId);
        if (ldap == null) {
            throw new CommonException(Constants.ErrorCode.LDAP_NOT_EXIST_EXCEPTION);
        }
        ldap.setAccount(ldapAccount.getAccount());
        ldap.setLdapPassword(encryptClient.decrypt(ldapAccount.getLdapPassword()));
        return (LdapConnectionDTO) ldapConnectService.testConnect(ldap).get(LdapConnectServiceImpl.LDAP_CONNECTION_DTO);
    }

    @Override
    public void syncLdapUser(Long organizationId, Long ldapId) {
        Ldap ldap = ldapUserService.validateLdap(organizationId, ldapId);
        Map<String, Object> map = ldapUserService.validateLdapConnection(ldap);
        LdapTemplate ldapTemplate = (LdapTemplate) map.get(LdapConnectServiceImpl.LDAP_TEMPLATE);
        ldapSyncUserTask.syncLDAPUser(ldapTemplate, ldap, finishFallback, Constants.LdapHistorySyncType.M);
    }

    @Override
    public void stop(Long ldapId) {
        LdapHistory ldapHistory = ldapHistoryRepository.queryLatestHistory(ldapId);
        ldapHistory.setSyncEndTime(new Date(System.currentTimeMillis()));
        ldapHistoryRepository.updateOptional(ldapHistory, LdapHistory.FIELD_SYNC_END_TIME);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LdapSyncConfig saveLdapSyncUserConfig(Long tenantId, LdapSyncConfig ldapSyncConfig) {
        ldapSyncConfig.setSyncType(LdapSyncType.SYNC_USER.value());
        return saveLdapSyncConfig(tenantId, ldapSyncConfig);
    }

    @Override
    public LdapSyncConfig queryLdapSyncUserConfig(Long tenantId) {
        return ldapSyncConfigDomainService.queryLdapUserConfig(tenantId, LdapSyncType.SYNC_USER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LdapSyncConfig saveLdapSyncLeaveConfig(Long tenantId, LdapSyncConfig ldapSyncConfig) {
        ldapSyncConfig.setSyncType(LdapSyncType.SYNC_LEAVE.value());
        return saveLdapSyncConfig(tenantId, ldapSyncConfig);
    }

    @Override
    public LdapSyncConfig queryLdapSyncLeaveConfig(Long tenantId) {
        return ldapSyncConfigDomainService.queryLdapUserConfig(tenantId, LdapSyncType.SYNC_LEAVE);
    }

    /**
     * 保存LDAP同步配置
     *
     * @param tenantId 租户ID
     * @param ldapSyncConfig LDAP同步配置
     * @return LdapSyncConfig
     */
    private LdapSyncConfig saveLdapSyncConfig(Long tenantId, LdapSyncConfig ldapSyncConfig) {
        validObject(ldapSyncConfig);
        if (ldapSyncConfig.getLdapSyncConfigId() == null) {
            return ldapSyncConfigDomainService.createLdapSyncConfig(tenantId, ldapSyncConfig);
        } else {
            SecurityTokenHelper.validToken(ldapSyncConfig);
            return ldapSyncConfigDomainService.updateLdapUserConfig(tenantId, ldapSyncConfig);
        }
    }
}
