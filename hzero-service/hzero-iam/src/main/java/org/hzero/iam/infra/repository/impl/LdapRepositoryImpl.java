package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import org.hzero.boot.oauth.domain.entity.BaseLdap;
import org.hzero.boot.oauth.domain.repository.BaseLdapRepository;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.repository.LdapRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

@Component
public class LdapRepositoryImpl extends BaseRepositoryImpl<Ldap> implements LdapRepository {

    private final BaseLdapRepository baseLdapRepository;

    public LdapRepositoryImpl(BaseLdapRepository baseLdapRepository) {
        this.baseLdapRepository = baseLdapRepository;
    }

    @Override
    public Ldap selectLdapByTenantId(Long tenantId) {
        Ldap param = new Ldap();
        param.setOrganizationId(tenantId);
        return this.selectOne(param);
    }

    @Override
    public Ldap selectLdap(Long tenantId, Long ldapId) {
        Ldap param = new Ldap();
        param.setOrganizationId(tenantId);
        param.setId(ldapId);
        return this.selectOne(param);
    }

    @Override
    public void initCacheLdap() {
        List<Ldap> ldapList = this.selectAll();
        ldapList.forEach(this::cacheLdap);
    }

    @Override
    public void cacheLdap(Ldap ldap) {
        BaseLdap baseLdap = new BaseLdap();
        BeanUtils.copyProperties(ldap, baseLdap);
        baseLdapRepository.saveLdap(baseLdap);
    }

    @Override
    public void deleteLdap(Ldap ldap) {
        BaseLdap baseLdap = new BaseLdap();
        BeanUtils.copyProperties(ldap, baseLdap);
        baseLdapRepository.deleteLdap(baseLdap);
    }

}
