package org.hzero.file.domain.repository;

import org.hzero.file.domain.entity.WatermarkConfig;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 水印配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
public interface WatermarkConfigRepository extends BaseRepository<WatermarkConfig> {

    /**
     * 分页查询
     *
     * @param pageRequest   分页
     * @param tenantId      租户Id
     * @param watermarkCode 水印配置编码
     * @param description   水印描述
     * @param watermarkType 水印类型
     * @return 查询结果
     */
    Page<WatermarkConfig> pageWatermark(PageRequest pageRequest, Long tenantId, String watermarkCode, String description, String watermarkType);

    /**
     * 查询水印配置明细
     *
     * @param tenantId    租户Id
     * @param watermarkId 水印配置Id
     * @return 水印配置
     */
    WatermarkConfig detailWatermark(Long tenantId, Long watermarkId);
}
