package org.hzero.oauth.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.oauth.domain.entity.Client;
import org.hzero.oauth.domain.vo.ClientRoleDetails;

/**
 * 客户端资源库
 *
 * @author bojiangzhou 2018/12/04
 */
public interface ClientRepository extends BaseRepository<Client> {

    /**
     * 根据客户端名称查询客户端
     *
     * @param clientName 客户端唯一名称
     * @return Client
     */
    Client selectClient(String clientName);

    /**
     * 查询客户端可选角色
     *
     * @param clientId 客户端ID
     * @return 可选角色
     */
    List<ClientRoleDetails> selectRoleDetails(Long clientId);

}

