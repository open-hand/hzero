package org.hzero.file.app.service;

import org.hzero.file.domain.entity.WatermarkConfig;

/**
 * 水印配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
public interface WatermarkConfigService {

    /**
     * 查询水印配置明细
     *
     * @param tenantId    租户Id
     * @param watermarkId 水印配置Id
     * @return 水印配置明细
     */
    WatermarkConfig detailWatermarkConfig(Long tenantId, Long watermarkId);

    /**
     * 创建水印配置
     *
     * @param watermarkConfig 水印配置
     * @return 水印配置
     */
    WatermarkConfig createWatermarkConfig(WatermarkConfig watermarkConfig);

    /**
     * 更新水印配置
     *
     * @param watermarkConfig 水印配置
     * @return 水印配置
     */
    WatermarkConfig updateWatermarkConfig(WatermarkConfig watermarkConfig);

    /**
     * 获取水印配置
     *
     * @param tenantId      租户
     * @param watermarkCode 水印配置编码
     * @return 水印配置
     */
    WatermarkConfig getConfig(Long tenantId, String watermarkCode);
}
