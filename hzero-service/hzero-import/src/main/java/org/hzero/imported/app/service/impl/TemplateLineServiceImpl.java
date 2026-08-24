package org.hzero.imported.app.service.impl;

import java.util.List;

import org.hzero.boot.imported.infra.redis.TemplateRedis;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.SecurityUtils;
import org.hzero.imported.app.service.TemplateLineService;
import org.hzero.imported.domain.entity.TemplateHeader;
import org.hzero.imported.domain.entity.TemplateLine;
import org.hzero.imported.domain.repository.TemplateHeaderRepository;
import org.hzero.imported.domain.repository.TemplateLineRepository;
import org.hzero.imported.infra.constant.HimpMessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-7 16:42:53
 */
@Service
public class TemplateLineServiceImpl implements TemplateLineService {

    private final RedisHelper redisHelper;
    private final TemplateLineRepository templateLineRepository;
    private final TemplateHeaderRepository templateHeaderRepository;

    @Autowired
    public TemplateLineServiceImpl(RedisHelper redisHelper,
                                   TemplateLineRepository templateLineRepository,
                                   TemplateHeaderRepository templateHeaderRepository) {
        this.redisHelper = redisHelper;
        this.templateLineRepository = templateLineRepository;
        this.templateHeaderRepository = templateHeaderRepository;
    }

    @Override
    public Page<TemplateLine> pageTemplateLine(Long targetId, String columnCode, String columnName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> templateLineRepository.listTemplateLineByTargetId(targetId, columnCode, columnName));
    }

    @Override
    public TemplateLine detailTemplateLine(Long lineId, Long tenantId) {
        return templateLineRepository.getTemplateLine(lineId);
    }

    @Override
    public List<TemplateLine> listTemplateLine(Long templateHeaderId) {
        List<TemplateLine> templateLineList = templateLineRepository.listTemplateLine(templateHeaderId);
        templateLineList.forEach(templateLine ->
                templateLine.setTemplateLineTls(templateLineRepository.getColumnNameTl(templateLine.getId())));
        return templateLineList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateLine createTemplateLine(TemplateLine templateLine) {
        templateLine.setColumnName(SecurityUtils.preventCsvInjection(templateLine.getColumnName()));
        templateLine.setSampleData(SecurityUtils.preventCsvInjection(templateLine.getSampleData()));
        TemplateHeader templateHeader = templateHeaderRepository.selectByTargetId(templateLine.getTargetId());
        Assert.notNull(templateHeader, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 唯一性校验
        Assert.isTrue(templateLineRepository.selectCount(new TemplateLine()
                        .setTargetId(templateLine.getTargetId())
                        .setColumnIndex(templateLine.getColumnIndex())) == BaseConstants.Digital.ZERO,
                HimpMessageConstants.COLUMN_INDEX_REPEAT);
        templateLineRepository.insertSelective(templateLine);

        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
        return templateLine;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateLine updateTemplateLine(TemplateLine templateLine) {
        templateLine.setColumnName(SecurityUtils.preventCsvInjection(templateLine.getColumnName()));
        templateLine.setSampleData(SecurityUtils.preventCsvInjection(templateLine.getSampleData()));
        TemplateHeader templateHeader = templateHeaderRepository.selectByTargetId(templateLine.getTargetId());
        Assert.notNull(templateHeader, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        templateLineRepository.updateOptional(templateLine, TemplateLine.FIELD_COLUMN_NAME,
                TemplateLine.FIELD_COLUMN_CODE, TemplateLine.FIELD_COLUMN_TYPE, TemplateLine.FIELD_LENGTH,
                TemplateLine.FIELD_FORMAT_MASK, TemplateLine.FIELD_ENABLED_FLAG, TemplateLine.FIELD_NULLABLE_FLAG,
                TemplateLine.FIELD_VALIDATE_FLAG, TemplateLine.FIELD_CHANGE_DATA_FLAG,
                TemplateLine.FIELD_SAMPLE_DATA, TemplateLine.FIELD_DESCRIPTION,
                TemplateLine.FIELD_MAX_VALUE, TemplateLine.FIELD_MIN_VALUE, TemplateLine.FIELD_VALIDATE_SET,
                TemplateLine.FIELD_REGULAR_EXPRESSION, TemplateLine.FIELD_COLUMN_INDEX);

        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
        return templateLine;
    }

    @Override
    public void deleteTemplateLine(Long lineId) {
        TemplateLine templateLine = templateLineRepository.selectByPrimaryKey(lineId);
        TemplateHeader templateHeader = templateHeaderRepository.selectByTargetId(templateLine.getTargetId());
        Assert.notNull(templateHeader, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        templateLineRepository.deleteByPrimaryKey(lineId);
        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
    }
}
