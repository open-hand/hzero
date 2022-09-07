package org.hzero.boot.oauth.domain.repository;

import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.mybatis.base.BaseRepository;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
public interface BaseClientRepository extends BaseRepository<BaseClient> {

    /**
     * 查询客户端
     *
     * @param clientName 客户端名称
     */
    BaseClient selectClient(String clientName);

    /**
     * 缓存客户端
     * 
     * @param client Client
     */
    void saveClient(BaseClient client);

    /**
     * 删除客户端
     *
     * @param clientName Client
     */
    void removeClient(String clientName);

}
