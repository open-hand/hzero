package org.hzero.iam.domain.service.ldap;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.iam.domain.repository.LdapRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.service.ldap.impl.LdapConnectServiceImpl;
import org.hzero.iam.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * @author bojiangzhou 2019/08/05
 */
@Component
public class LdapSyncUserQuartzTask {

    private static final Logger logger = LoggerFactory.getLogger(LdapSyncUserQuartzTask.class);

    private final LdapSyncUserTask ldapSyncUserTask;
    private final LdapHistoryRepository ldapHistoryRepository;
    private final TenantRepository tenantRepository;
    private final LdapRepository ldapRepository;
    private final LdapUserService ldapUserService;

    public LdapSyncUserQuartzTask(LdapSyncUserTask ldapSyncUserTask, LdapHistoryRepository ldapHistoryRepository,
                                  TenantRepository tenantRepository, LdapRepository ldapRepository, LdapUserService ldapUserService) {
        this.ldapSyncUserTask = ldapSyncUserTask;
        this.ldapHistoryRepository = ldapHistoryRepository;
        this.tenantRepository = tenantRepository;
        this.ldapRepository = ldapRepository;
        this.ldapUserService = ldapUserService;
    }

    public void syncLdapUserSite(Map<String, Object> map) {
        syncLdapUser(map);
    }

    public void syncLdapUserOrganization(Map<String, Object> map) {
        syncLdapUser(map);
    }

    public void syncDisabledLdapUserSite(Map<String, Object> map) {
        syncAndDisabledLdapUserByFilter(map);
    }

    public void syncDisabledLdapUserOrg(Map<String, Object> map) {
        syncAndDisabledLdapUserByFilter(map);
    }

    private void syncLdapUser(Map<String, Object> map) {
        //获取方法参数
        String tenantNum = Optional.ofNullable((String) map.get("organizationCode")).orElseThrow(() -> new CommonException("hiam.warn.syncLdapUser.tenantNumNull"));
        //获取ldap
        Ldap ldap = getLdapByTenantNum(tenantNum);
        //获取测试连接的returnMap 及 测试连接十分成功
        Map<String, Object> returnMap = ldapUserService.validateLdapConnection(ldap);
        //获取ldapTemplate
        LdapTemplate ldapTemplate = (LdapTemplate) returnMap.get(LdapConnectServiceImpl.LDAP_TEMPLATE);
        CountDownLatch latch = new CountDownLatch(1);
        //开始同步
        long startTime = System.currentTimeMillis();
        ldapSyncUserTask.syncLDAPUser(ldapTemplate, ldap, (LdapSyncReport ldapSyncReport, LdapHistory ldapHistory) -> {
            latch.countDown();
            ldapHistory.setSyncEndTime(ldapSyncReport.getEndTime());
            ldapHistory.setNewUserCount(ldapSyncReport.getInsert());
            ldapHistory.setUpdateUserCount(ldapSyncReport.getUpdate());
            ldapHistory.setErrorUserCount(ldapSyncReport.getError());
            ldapHistory.setTenantId(ldapSyncReport.getOrganizationId());
            ldapHistoryRepository.updateByPrimaryKeySelective(ldapHistory);
            return ldapHistory;
        }, Constants.LdapHistorySyncType.A);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommonException("error.ldapSyncUserTask.countDownLatch", e);
        }
        long entTime = System.currentTimeMillis();
        logger.info("LdapSyncUserQuartzTask sync idap user completed.speed time:{} millisecond", (entTime - startTime));
    }

    private void syncAndDisabledLdapUserByFilter(Map<String, Object> map) {
        //获取方法参数
        String orgCode = Optional.ofNullable((String) map.get("organizationCode")).orElseThrow(() -> new CommonException("error.syncLdapUser.organizationCodeEmpty"));
        String filterStr = Optional.ofNullable((String) map.get("filterStr")).orElseThrow(() -> new CommonException("error.syncLdapUser.filterStrEmpty"));
        //获取ldap及修改停用条件
        Ldap ldap = getLdapByTenantNum(orgCode);
        ldap.setCustomFilter(filterStr);
        //获取测试连接的returnMap 及 测试连接十分成功
        Map<String, Object> returnMap = ldapUserService.validateLdapConnection(ldap);

        //获取ldapTemplate
        LdapTemplate ldapTemplate = (LdapTemplate) returnMap.get(LdapConnectServiceImpl.LDAP_TEMPLATE);
        CountDownLatch latch = new CountDownLatch(1);
        //开始同步
        long startTime = System.currentTimeMillis();

        ldapSyncUserTask.syncDisabledLDAPUser(ldapTemplate, ldap, (LdapSyncReport ldapSyncReport, LdapHistory ldapHistory) -> {
            latch.countDown();
            ldapHistory.setSyncEndTime(ldapSyncReport.getEndTime());
            ldapHistory.setNewUserCount(ldapSyncReport.getInsert());
            ldapHistory.setUpdateUserCount(ldapSyncReport.getUpdate());
            ldapHistory.setErrorUserCount(ldapSyncReport.getError());
            ldapHistory.setTenantId(ldapSyncReport.getOrganizationId());
            ldapHistoryRepository.updateByPrimaryKeySelective(ldapHistory);
            return ldapHistory;
        }, Constants.LdapHistorySyncType.A);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommonException("error.ldapSyncUserTask.countDownLatch", e);
        }
        long entTime = System.currentTimeMillis();
        logger.info("LdapSyncUserQuartzTask sync idap user completed.speed time:{} millisecond", (entTime - startTime));
    }

    private Long validator(Tenant tenant) {
        if (tenant == null) {
            throw new CommonException("hiam.warn.ldapSyncUserTask.tenantNull");
        }
        if (tenant.getTenantId() == null) {
            throw new CommonException("hiam.warn.ldapSyncUserTask.tenantIdNull");
        }
        return tenant.getTenantId();
    }

    /**
     * 获取组织下ldap
     *
     * @param tenantNum 租户编码
     * @return ldap
     */
    private Ldap getLdapByTenantNum(String tenantNum) {
        Tenant tenant = new Tenant();
        tenant.setTenantNum(tenantNum);
        tenant = tenantRepository.selectOne(tenant);
        Long tenantId = validator(tenant);
        Long ldapId = ldapRepository.selectLdapByTenantId(tenantId).getId();
        logger.info("LdapSyncUserQuartzTask starting sync idap user,id:{}, tenantId:{}", ldapId, tenantId);
        return ldapUserService.validateLdap(tenantId, ldapId);
    }
}
