package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.domain.repository.BaseClientRepository;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.repository.ClientRepository;
import org.hzero.iam.infra.mapper.ClientMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 客户端资源库实现
 *
 * @author bojiangzhou 2019/08/02
 */
@Component
public class ClientRepositoryImpl extends BaseRepositoryImpl<Client> implements ClientRepository {

    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private BaseClientRepository baseClientRepository;

    @Override
    public Page<Client> pageClient(Long organizationId, String name, Integer enabledFlag, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> clientMapper.listClient(organizationId, name, enabledFlag));
    }

    @Override
    public Client detailClient(Long tenantId, Long clientId) {
        return clientMapper.detailClient(tenantId, clientId);
    }

    @Override
    public Page<Client> pageClientSimple(Long tenantId, Long roleId, String name, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> clientMapper.listClientSimple(tenantId, roleId, name));
    }

    @Override
    public Page<Client> selectRoleClients(Long roleId, String name, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> clientMapper.selectRoleClients(roleId, name));
    }

    @Override
    public void initCacheClient() {
        List<Client> clientList = this.selectAll();
        clientList.forEach(this::cacheClient);

    }

    @Override
    public void cacheClient(Client client) {
        BaseClient baseClient = new BaseClient();
        BeanUtils.copyProperties(client, baseClient);
        baseClientRepository.saveClient(baseClient);
    }

    @Override
    public void removeCacheClient(String clientName) {
        baseClientRepository.removeClient(clientName);
    }


}
