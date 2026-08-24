package org.hzero.iam.app.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.hzero.boot.platform.data.permission.util.DocRedisUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.DocTypeDimensionService;
import org.hzero.iam.app.service.DocTypeService;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.repository.DocTypeDimensionRepository;
import org.hzero.iam.domain.repository.RoleAuthorityLineRepository;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.iam.infra.feign.PermissionRuleFeignClient;
import org.hzero.mybatis.helper.UniqueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 单据类型维度应用服务默认实现
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 * @author xiaoyu.zhao@hand-china.com
 */
@Service
public class DocTypeDimensionServiceImpl implements DocTypeDimensionService {
    @Autowired
    private DocTypeDimensionRepository dimensionRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;
    @Autowired
    private RoleAuthorityLineRepository authorityLineRepository;
    @Autowired
    private DocTypeService docTypeService;
    @Autowired
    private PermissionRuleFeignClient feignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocTypeDimension createDocTypeDimension(DocTypeDimension docTypeDimension) {
        Assert.isTrue(UniqueHelper.valid(docTypeDimension), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        dimensionRepository.insertSelective(docTypeDimension);
        return docTypeDimension;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocTypeDimension updateDocTypeDimension(DocTypeDimension docTypeDimension) {
        DocTypeDimension dbDimension = dimensionRepository.selectByPrimaryKey(docTypeDimension.getDimensionId());
        dimensionRepository.updateOptional(docTypeDimension, DocTypeDimension.FIELD_DIMENSION_NAME,
                DocTypeDimension.FIELD_DIMENSION_TYPE, DocTypeDimension.FIELD_VALUE_SOURCE_TYPE,
                DocTypeDimension.FIELD_VALUE_SOURCE, DocTypeDimension.FIELD_ORDER_SEQ,
                DocTypeDimension.FIELD_ENABLED_FLAG);
        // 禁用单据维度需判断是否存在启用的单据权限与其关联，若存在则需清除相关数据（数据权限、单据权限缓存）
        if (docTypeDimension.getEnabledFlag().equals(BaseConstants.Flag.NO)
                && dbDimension.getEnabledFlag().equals(BaseConstants.Flag.YES)) {
            this.processDocCacheAndPermissionRule(docTypeDimension, false);
        } else if (docTypeDimension.getEnabledFlag().equals(BaseConstants.Flag.YES)
                && dbDimension.getEnabledFlag().equals(BaseConstants.Flag.NO)) {
            this.processDocCacheAndPermissionRule(docTypeDimension, true);
        }
        return docTypeDimension;
    }

    /**
     * 处理单据权限缓存以及数据权限内容
     *
     * @param docTypeDimension 单据维度
     * @param isEnabled 是否为从禁用到启用，true则添加缓存，false则删除缓存
     */
    private void processDocCacheAndPermissionRule(DocTypeDimension docTypeDimension, Boolean isEnabled) {
        // 判断维度是否关联启用状态的单据，若未关联则无需处理缓存及数据权限
        List<Long> docTypeIds = dimensionRepository.selectDocTypeIdByDimensionCode(docTypeDimension);
        if (CollectionUtils.isEmpty(docTypeIds)) {
            return;
        }
        // 处理数据权限
        docTypeService.generateShieldRule(docTypeDimension.getTenantId(), docTypeIds);
    }
}
