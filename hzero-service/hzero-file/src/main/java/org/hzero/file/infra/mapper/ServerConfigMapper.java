package org.hzero.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.ServerConfig;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 服务器上传配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
public interface ServerConfigMapper extends BaseMapper<ServerConfig> {

    /**
     * 查询
     *
     * @param tenantId    租户ID
     * @param configCode  配置编码
     * @param description 描述
     * @param enabledFlag 启用标识
     * @return 查询结果
     */
    List<ServerConfig> listConfig(@Param("tenantId") Long tenantId,
                                  @Param("configCode") String configCode,
                                  @Param("description") String description,
                                  @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询明细
     *
     * @param tenantId 租户ID
     * @param configId 配置ID
     * @return 配置明细
     */
    ServerConfig detailConfig(@Param("tenantId") Long tenantId, @Param("configId") Long configId);
}
