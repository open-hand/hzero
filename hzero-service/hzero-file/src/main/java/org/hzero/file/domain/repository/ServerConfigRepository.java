package org.hzero.file.domain.repository;

import org.hzero.file.domain.entity.ServerConfig;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务器上传配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
public interface ServerConfigRepository extends BaseRepository<ServerConfig> {

    /**
     * 分页查询
     *
     * @param pageRequest 分页
     * @param tenantId    租户Id
     * @param configCode  配置编码
     * @param description 描述
     * @param enabledFlag 启用标识
     * @return 查询结果
     */
    Page<ServerConfig> pageConfig(PageRequest pageRequest, Long tenantId, String configCode, String description, Integer enabledFlag);

    /**
     * 查询明细
     *
     * @param tenantId 租户ID
     * @param configId 配置ID
     * @return 配置明细
     */
    ServerConfig detailConfig(Long tenantId, Long configId);
}
