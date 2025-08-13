package org.hzero.oauth.infra.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.oauth.domain.entity.Client;
import org.hzero.oauth.domain.repository.ClientRepository;
import org.hzero.oauth.domain.vo.ClientRoleDetails;
import org.hzero.oauth.infra.mapper.ClientPlusMapper;

/**
 * 客户端资源库实现
 *
 * @author bojiangzhou 2018/12/04
 */
@Component
public class ClientRepositoryImpl extends BaseRepositoryImpl<Client> implements ClientRepository {

    @Autowired
    private ClientPlusMapper clientPlusMapper;

    @Override
    public Client selectClient(String clientName) {
        return clientPlusMapper.selectClientWithUserAndRole(clientName);
    }

    @Override
    public List<ClientRoleDetails> selectRoleDetails(Long clientId) {
        return clientPlusMapper.selectRoleDetails(clientId);
    }

}
