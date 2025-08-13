package org.hzero.file.infra.repository.impl;

import org.hzero.file.domain.entity.WatermarkConfig;
import org.hzero.file.domain.repository.WatermarkConfigRepository;
import org.hzero.file.infra.mapper.WatermarkConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 水印配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
@Component
public class WatermarkConfigRepositoryImpl extends BaseRepositoryImpl<WatermarkConfig> implements WatermarkConfigRepository {

    @Autowired
    private WatermarkConfigMapper watermarkConfigMapper;

    @Override
    public Page<WatermarkConfig> pageWatermark(PageRequest pageRequest, Long tenantId, String watermarkCode, String description, String watermarkType) {
        return PageHelper.doPageAndSort(pageRequest, () -> watermarkConfigMapper.listWatermark(tenantId, watermarkCode, description, watermarkType));
    }

    @Override
    public WatermarkConfig detailWatermark(Long tenantId, Long watermarkId) {
        return watermarkConfigMapper.detailWatermark(tenantId, watermarkId);
    }
}
