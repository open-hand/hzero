package org.hzero.imported.app.service.impl;

import org.hzero.boot.imported.infra.redis.TemplateRedis;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.imported.app.service.TemplateHeaderService;
import org.hzero.imported.domain.entity.TemplateHeader;
import org.hzero.imported.domain.entity.TemplateTarget;
import org.hzero.imported.domain.repository.TemplateHeaderRepository;
import org.hzero.imported.domain.repository.TemplateLineRepository;
import org.hzero.imported.domain.repository.TemplateTargetRepository;
import org.hzero.imported.infra.constant.HimpMessageConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-7 16:42:53
 */
@Service
public class TemplateHeaderServiceImpl implements TemplateHeaderService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateHeaderServiceImpl.class);

    private TemplateHeaderRepository templateHeaderRepository;
    private TemplateTargetRepository templateTargetRepository;
    private TemplateLineRepository templateLinesRepository;
    private RedisHelper redisHelper;

    @Autowired
    public TemplateHeaderServiceImpl(TemplateHeaderRepository templateHeaderRepository,
                                     TemplateTargetRepository templateTargetRepository,
                                     TemplateLineRepository templateLinesRepository,
                                     RedisHelper redisHelper) {
        this.templateHeaderRepository = templateHeaderRepository;
        this.templateTargetRepository = templateTargetRepository;
        this.templateLinesRepository = templateLinesRepository;
        this.redisHelper = redisHelper;
    }

    @Override
    public Page<TemplateHeader> pageTemplateHeader(String templateCode, String templateName, Long tenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> templateHeaderRepository.selectTemplateHeaderList(templateCode, templateName, tenantId));
    }

    @Override
    public TemplateHeader detailTemplateHeader(Long templateId, Long tenantId) {
        TemplateHeader templateHeader = templateHeaderRepository.selectHeaderByTemplateId(templateId, tenantId);
        if (templateHeader != null) {
            templateHeader.setTemplateTargetList(templateTargetRepository.listTemplateTarget(templateHeader.getId()));
        }
        return templateHeader;
    }

    @Override
    public TemplateHeader getTemplateHeader(String templateCode, Long tenantId) {
        TemplateHeader templateHeader = templateHeaderRepository.selectOne(new TemplateHeader().setTemplateCode(templateCode).setTenantId(tenantId).setEnabledFlag(BaseConstants.Flag.YES));
        if (templateHeader != null) {
            // FIX 仅查询非禁用的模板列
            templateHeader.setTemplateTargetList(templateTargetRepository.selectByCondition(Condition.builder(TemplateTarget.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(TemplateTarget.FIELD_HEADER_ID, templateHeader.getId())
                            .andEqualTo(TemplateTarget.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                    )
                    .build()));
        }
        return templateHeader;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateHeader createTemplateHeader(TemplateHeader templateHeader) {
        Assert.isTrue(templateHeaderRepository.selectCount(new TemplateHeader()
                        .setTemplateCode(templateHeader.getTemplateCode())
                        .setTenantId(templateHeader.getTenantId())) == BaseConstants.Digital.ZERO,
                HimpMessageConstants.DUPLICATE_TEMPLATE_CODE);
        templateHeaderRepository.insertSelective(templateHeader);
        if (!CollectionUtils.isEmpty(templateHeader.getTemplateTargetList())) {
            validSheetIndexRepeat(templateHeader);
            templateHeader.getTemplateTargetList().forEach(templateTarget ->
                    templateTargetRepository.insertSelective(templateTarget.setHeaderId(templateHeader.getId()).setTenantId(templateHeader.getTenantId())));
        }

        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
        return templateHeader;
    }

    private void validSheetIndexRepeat(TemplateHeader templateHeader) {
        Set<Integer> sheetIndexSet = new HashSet<>();
        Set<String> sheetNameSet = new HashSet<>();
        Set<Long> targetIdSet = new HashSet<>();
        templateHeader.getTemplateTargetList().forEach(templateTarget -> {
            if (sheetIndexSet.contains(templateTarget.getSheetIndex())) {
                throw new CommonException(HimpMessageConstants.ERROR_DUPLICATE_SHEET_INDEX);
            }
            sheetIndexSet.add(templateTarget.getSheetIndex());
            if (sheetNameSet.contains(templateTarget.getSheetName())) {
                throw new CommonException(HimpMessageConstants.ERROR_DUPLICATE_SHEET_NAME);
            }
            sheetNameSet.add(templateTarget.getSheetName());
            if (templateTarget.getId() != null) {
                targetIdSet.add(templateTarget.getId());
            }
        });
        if (templateHeader.getId() != null) {
            if (templateTargetRepository.selectCountByCondition(Condition.builder(TemplateTarget.class)
                    .andWhere(Sqls.custom().andEqualTo(TemplateTarget.FIELD_HEADER_ID, templateHeader.getId())
                            .andIn(TemplateTarget.FIELD_SHEET_INDEX, sheetIndexSet)
                            .andNotIn(TemplateTarget.FIELD_ID, targetIdSet, true))
                    .build()) != 0) {
                throw new CommonException(HimpMessageConstants.ERROR_DUPLICATE_SHEET_INDEX);
            }
            if (templateTargetRepository.selectCountByCondition(Condition.builder(TemplateTarget.class)
                    .andWhere(Sqls.custom().andEqualTo(TemplateTarget.FIELD_HEADER_ID, templateHeader.getId())
                            .andIn(TemplateTarget.FIELD_SHEET_NAME, sheetNameSet)
                            .andNotIn(TemplateTarget.FIELD_ID, targetIdSet, true))
                    .build()) != 0) {
                throw new CommonException(HimpMessageConstants.ERROR_DUPLICATE_SHEET_NAME);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateHeader updateTemplateHeader(TemplateHeader templateHeader) {
        templateHeaderRepository.updateOptional(templateHeader, TemplateHeader.FIELD_TEMPLATE_NAME,
                TemplateHeader.FIELD_ENABLED_FLAG, TemplateHeader.FIELD_TEMPLATE_TYPE,
                TemplateHeader.FIELD_PREFIX_PATCH, TemplateHeader.FIELD_DESCRIPTION,
                TemplateHeader.FIELD_TEMPLATE_URL, TemplateHeader.FIELD_FRAGMENT_FLAG);
        if (!CollectionUtils.isEmpty(templateHeader.getTemplateTargetList())) {
            validSheetIndexRepeat(templateHeader);
            templateHeader.getTemplateTargetList().forEach(templateTarget -> {
                if (templateTarget.getId() == null) {
                    templateTargetRepository.insertSelective(templateTarget.setHeaderId(templateHeader.getId()).setTenantId(templateHeader.getTenantId()));
                } else {
                    templateTargetRepository.updateOptional(templateTarget, TemplateTarget.FIELD_SHEET_INDEX,
                            TemplateTarget.FIELD_SHEET_NAME, TemplateTarget.FIELD_DATASOURCE_CODE,
                            TemplateTarget.FIELD_DATASOURCE_CODE, TemplateTarget.FIELD_TABLE_NAME,
                            TemplateTarget.FIELD_RULE_SCRIPT_CODE, TemplateTarget.FIELD_ENABLED_FLAG,
                            TemplateTarget.FIELD_START_LINE);
                }
            });
        }
        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
        return templateHeader;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateHeader(Long templateId) {
        TemplateHeader templateHeader = templateHeaderRepository.selectByPrimaryKey(templateId);
        logger.info("Delete template line by template id {} : {}", templateId, templateLinesRepository.deleteByHeaderId(templateId));
        logger.info("Delete template target by template id {} : {}", templateId, templateTargetRepository.deleteByHeaderId(templateId));
        templateHeaderRepository.deleteByPrimaryKey(templateId);

        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateTarget(Long targetId) {
        TemplateHeader templateHeader = templateHeaderRepository.selectByTargetId(targetId);
        Assert.notNull(templateHeader, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

        logger.info("Delete template line by template id {} : {}", targetId, templateLinesRepository.deleteByTargetId(targetId));
        templateTargetRepository.deleteByPrimaryKey(targetId);

        // 清除模板缓存
        TemplateRedis.clearCache(redisHelper, templateHeader.getTenantId(), templateHeader.getTemplateCode());
    }
}
