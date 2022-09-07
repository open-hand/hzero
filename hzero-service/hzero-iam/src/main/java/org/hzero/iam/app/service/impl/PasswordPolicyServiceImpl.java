package org.hzero.iam.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.observer.EventBus;
import org.hzero.iam.app.service.PasswordPolicyService;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.service.pwdpolicy.PasswordPolicyObserver;

/**
 * 密码策略应用服务
 *
 * @author bojiangzhou 2019/08/05
 */
@Component
public class PasswordPolicyServiceImpl implements PasswordPolicyService {


    private final TenantRepository tenantRepository;
    private final PasswordPolicyRepository passwordPolicyRepository;
    private EventBus<PasswordPolicy> eventBus;


    public PasswordPolicyServiceImpl(TenantRepository tenantRepository,
                                     PasswordPolicyRepository passwordPolicyRepository) {
        this.tenantRepository = tenantRepository;
        this.passwordPolicyRepository = passwordPolicyRepository;
    }

    @Autowired(required = false)
    private void setEventBus(List<PasswordPolicyObserver> observers) {
        if (CollectionUtils.isNotEmpty(observers)){
            this.eventBus = new EventBus<>("UpdatePasswordPolicy", observers);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PasswordPolicy createPasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy) {
        Tenant tenant = tenantRepository.selectByPrimaryKey(tenantId);
        if (tenant == null) {
            throw new CommonException("hiam.warn.pwdPolicy.tenantNotFound");
        }
        passwordPolicy.setCode(tenant.getTenantNum());
        passwordPolicy.setName(tenant.getTenantName());
        passwordPolicy.validate();
        passwordPolicyRepository.insertSelective(passwordPolicy);

        passwordPolicyRepository.cachePasswordPolicy(passwordPolicy);

        notifyUpdate(passwordPolicy, null);

        return passwordPolicy;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PasswordPolicy updatePasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy) {
        Tenant tenant = tenantRepository.selectByPrimaryKey(tenantId);
        if (tenant == null) {
            throw new CommonException("hiam.warn.pwdPolicy.tenantNotFound");
        }

        PasswordPolicy originalPasswordPolicy = passwordPolicyRepository.selectTenantPasswordPolicy(tenantId);

        passwordPolicy.setCode(tenant.getTenantNum());
        passwordPolicy.setName(tenant.getTenantName());
        passwordPolicy.validate();
        passwordPolicyRepository.updateByPrimaryKey(passwordPolicy);

        passwordPolicyRepository.cachePasswordPolicy(passwordPolicy);

        notifyUpdate(passwordPolicy, originalPasswordPolicy);

        return passwordPolicy;
    }

    private void notifyUpdate(PasswordPolicy passwordPolicy, PasswordPolicy originalPasswordPolicy) {
        if (this.eventBus != null) {
            this.eventBus.notifyObservers(passwordPolicy, originalPasswordPolicy);
        }
    }

}
