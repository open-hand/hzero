package org.hzero.file.app.service;

import org.hzero.file.domain.entity.CapacityConfig;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 文件容量配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
public interface CapacityConfigService {

    /**
     * 分页查询
     *
     * @param tenantId    租户Id
     * @param pageRequest 分页
     * @return 文件上传配置分页
     */
    CapacityConfig pageUploadConfig(Long tenantId, PageRequest pageRequest);

    /**
     * 查询租户配置（文件大小校验使用）
     *
     * @param tenantId 租户Id
     * @return 文件上传配置
     */
    CapacityConfig selectByTenantId(Long tenantId);

    /**
     * 新建或更新
     *
     * @param capacityConfig 文件容量配置对象
     * @return 配置
     */
    CapacityConfig createOrUpdate(CapacityConfig capacityConfig);

    /**
     * 新建
     *
     * @param capacityConfig 文件容量配置对象
     * @return 新建的对象
     */
    CapacityConfig createCapacityConfig(CapacityConfig capacityConfig);

    /**
     * 更新
     *
     * @param capacityConfig 文件容量配置对象
     * @return 更新的对象
     */
    CapacityConfig updateCapacityConfig(CapacityConfig capacityConfig);
}
