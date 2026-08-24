package org.hzero.file.app.service.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FilenameUtils;
import org.hzero.file.app.service.WatermarkConfigService;
import org.hzero.file.domain.entity.WatermarkConfig;
import org.hzero.file.domain.repository.WatermarkConfigRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 水印配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
@Service
public class WatermarkConfigServiceImpl implements WatermarkConfigService {

    private final WatermarkConfigRepository watermarkConfigRepository;

    @Autowired
    public WatermarkConfigServiceImpl(WatermarkConfigRepository watermarkConfigRepository) {
        this.watermarkConfigRepository = watermarkConfigRepository;
    }

    @Override
    public WatermarkConfig detailWatermarkConfig(Long tenantId, Long watermarkId) {
        WatermarkConfig config = watermarkConfigRepository.detailWatermark(tenantId, watermarkId);
        if (config == null) {
            return null;
        }
        switch (config.getWatermarkType()) {
            case HfleConstant.WatermarkType.TEXT:
            case HfleConstant.WatermarkType.TILE_TEXT:
                if (StringUtils.isNotBlank(config.getFontUrl())) {
                    config.setFilename(FilenameUtils.getFileName(config.getFontUrl()));
                }
                break;
            case HfleConstant.WatermarkType.IMAGE:
            case HfleConstant.WatermarkType.TILE_IMAGE:
                if (StringUtils.isNotBlank(config.getDetail())) {
                    config.setFilename(FilenameUtils.getFileName(config.getDetail()));
                }
                break;
            default:
                break;
        }
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WatermarkConfig createWatermarkConfig(WatermarkConfig watermarkConfig) {
        watermarkConfig.validate();
        // 唯一性校验
        Assert.isTrue(watermarkConfigRepository.selectCount(
                new WatermarkConfig().setTenantId(watermarkConfig.getTenantId()).setWatermarkCode(watermarkConfig.getWatermarkCode())) == BaseConstants.Digital.ZERO,
                BaseConstants.ErrorCode.DATA_INVALID);
        watermarkConfigRepository.insertSelective(watermarkConfig);
        return watermarkConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WatermarkConfig updateWatermarkConfig(WatermarkConfig watermarkConfig) {
        watermarkConfig.validate();
        watermarkConfigRepository.updateOptional(watermarkConfig,
                WatermarkConfig.FIELD_DESCRIPTION,
                WatermarkConfig.FIELD_WATERMARK_TYPE,
                WatermarkConfig.FIELD_FILL_OPACITY,
                WatermarkConfig.FIELD_COLOR,
                WatermarkConfig.FIELD_FONT_SIZE,
                WatermarkConfig.FIELD_X_AXIS,
                WatermarkConfig.FIELD_Y_AXIS,
                WatermarkConfig.FIELD_ALIGN,
                WatermarkConfig.FIELD_WEIGHT,
                WatermarkConfig.FIELD_HEIGHT,
                WatermarkConfig.FIELD_ROTATION,
                WatermarkConfig.FIELD_DETAIL,
                WatermarkConfig.FIELD_FONT_URL,
                WatermarkConfig.FIELD_ENABLED_FLAG);
        return watermarkConfig;
    }

    @Override
    public WatermarkConfig getConfig(Long tenantId, String watermarkCode) {
        WatermarkConfig config = watermarkConfigRepository.selectOne(new WatermarkConfig().setTenantId(tenantId)
                .setWatermarkCode(watermarkCode).setEnabledFlag(BaseConstants.Flag.YES));
        if (config == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
            config = watermarkConfigRepository.selectOne(new WatermarkConfig().setTenantId(BaseConstants.DEFAULT_TENANT_ID)
                    .setWatermarkCode(watermarkCode).setEnabledFlag(BaseConstants.Flag.YES));
        }
        Assert.notNull(config, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        return config;
    }
}
