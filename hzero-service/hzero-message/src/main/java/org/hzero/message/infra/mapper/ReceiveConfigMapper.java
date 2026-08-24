package org.hzero.message.infra.mapper;

import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.domain.entity.ReceiveConfig;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 接收配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
public interface ReceiveConfigMapper extends BaseMapper<ReceiveConfig> {

    /**
     * 查询配置列表
     *
     * @param tenantId 租户ID
     * @return 配置
     */
    List<ReceiveConfigDTO> listConfig(Long tenantId);

    /**
     * 查询接收配置的父级编码列表
     *
     * @param parentReceiveCode 父级编码
     * @param tenantId          租户ID
     * @return 配置DTO
     */
    List<ReceiveConfigDTO> listParentReceive(String parentReceiveCode, Long tenantId);

}
