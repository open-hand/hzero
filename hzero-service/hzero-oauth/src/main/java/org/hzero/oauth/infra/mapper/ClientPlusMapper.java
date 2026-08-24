package org.hzero.oauth.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.oauth.domain.entity.Client;
import org.hzero.oauth.domain.vo.ClientRoleDetails;

import java.util.List;

/**
 * HothUserMapper
 *
 * @author bojiangzhou 2019/02/26
 */
public interface ClientPlusMapper extends BaseMapper<Client> {

    /**
     * 查询客户端角色信息
     *
     * @param clientName 客户端ID
     * @return 客户端角色信息
     */
    Client selectClientWithUserAndRole(@Param("clientName") String clientName);

    /**
     * 查询客户端可选角色
     *
     * @param clientId 客户端ID
     * @return 可选角色
     */
    List<ClientRoleDetails> selectRoleDetails(@Param("clientId") Long clientId);
}
