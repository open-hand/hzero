package org.hzero.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.WatermarkConfig;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 水印配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
public interface WatermarkConfigMapper extends BaseMapper<WatermarkConfig> {

    /**
     * 查询水印配置
     *
     * @param tenantId      租户Id
     * @param watermarkCode 水印配置编码
     * @param description   描述
     * @param watermarkType 水印类型
     * @return 水印配置列表
     */
    List<WatermarkConfig> listWatermark(@Param("tenantId") Long tenantId,
                                        @Param("watermarkCode") String watermarkCode,
                                        @Param("description") String description,
                                        @Param("watermarkType") String watermarkType);

    /**
     * 查询水印配置明细
     *
     * @param tenantId    租户Id
     * @param watermarkId 水印配置Id
     * @return 水印配置
     */
    WatermarkConfig detailWatermark(@Param("tenantId") Long tenantId,
                                    @Param("watermarkId") Long watermarkId);
}
