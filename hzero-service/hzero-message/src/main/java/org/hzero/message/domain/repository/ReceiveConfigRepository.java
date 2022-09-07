package org.hzero.message.domain.repository;

import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.domain.entity.ReceiveConfig;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 接收配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
public interface ReceiveConfigRepository extends BaseRepository<ReceiveConfig> {

    /**
     * 查询配置列表
     *
     * @param tenantId 租户Id
     * @return 配置
     */
    List<ReceiveConfigDTO> listConfig(Long tenantId);

    /**
     * 查询接收配置的父级编码列表
     *
     * @param parentReceiveCode 父级接收编码
     * @param tenantId          租户Id
     * @return 配置
     */
    List<ReceiveConfigDTO> listParentReceive(String parentReceiveCode, Long tenantId);

}
