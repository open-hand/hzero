package org.hzero.platform.app.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.app.service.TemplateAssignService;
import org.hzero.platform.domain.entity.TemplateAssign;
import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.repository.TemplateAssignRepository;
import org.hzero.platform.domain.repository.TemplateConfigRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 模板配置应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
@Service
public class TemplateAssignServiceImpl implements TemplateAssignService {

    @Autowired
    private TemplateAssignRepository assignRepository;
    @Autowired
    private TemplateConfigRepository configRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TemplateAssign> batchCreateTemplateAssigns(List<TemplateAssign> templateAssigns) {
        // 校验传入参数是否为空
        Assert.notEmpty(templateAssigns, BaseConstants.ErrorCode.NOT_NULL);
        templateAssigns.forEach(this::checkRepeat);
        // 判断当前域名下分配的模板是否存在默认模板，若存在则不设置默认，不存在则将分配的第一个模板设置为默认
        TemplateAssign templateAssign = templateAssigns.get(0);
        boolean flag = assignRepository.checkDefaultTpl(templateAssign.getSourceKey(), templateAssign.getSourceType(),
                templateAssign.getTenantId());
        if (flag) {
            // 数据库中不存在，设置第一个模板为默认模板
            templateAssigns.get(0).setDefaultFlag(BaseConstants.Flag.YES);
        }
        // 插入数据库
        assignRepository.batchInsertSelective(templateAssigns);
        return templateAssigns;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateAssign defaultTemplateAssign(Long templateAssignId) {
        // 将模板设置为默认，先查询数据库检查是否存在默认模板，若存在则需将其取消默认在设置当前模板为默认模板
        TemplateAssign dbTemplateAssign = assignRepository.selectByPrimaryKey(templateAssignId);
        dbTemplateAssign.setDefaultFlag(BaseConstants.Flag.YES);
        TemplateAssign templateAssign = new TemplateAssign(dbTemplateAssign.getSourceType(),
                dbTemplateAssign.getSourceKey(), dbTemplateAssign.getTenantId(), BaseConstants.Flag.YES);
        List<TemplateAssign> results = assignRepository.select(templateAssign);
        if (CollectionUtils.isNotEmpty(results)) {
            results.forEach(result -> {
                if (result.getDefaultFlag().equals(BaseConstants.Flag.YES)) {
                    // 将数据库中已经存在的默认模板状态去除
                    result.setDefaultFlag(BaseConstants.Flag.NO);
                    assignRepository.updateOptional(result, TemplateAssign.FIELD_DEFAULT_FLAG);
                    // 清除历史默认模板缓存
                    configRepository.clearDefaultTplCache(result.getTemplateAssignId());
                }
            });
        }
        // 将当前数据更新为默认模板状态
        assignRepository.updateOptional(dbTemplateAssign, TemplateAssign.FIELD_DEFAULT_FLAG);
        // 添加新设置的默认模板缓存
        configRepository.generateDefaultTplCache(templateAssignId);
        return dbTemplateAssign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteTemplateAssigns(List<TemplateAssign> templateAssigns) {
        templateAssigns.forEach(this::deleteTemplateAssign);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateAssign(TemplateAssign templateAssign) {
        // 删除分配模板信息，先判断删除的是否是默认模板，默认模板不可删除
        Assert.isTrue(Objects.equals(BaseConstants.Flag.NO, templateAssign.getDefaultFlag()),
                HpfmMsgCodeConstants.ERROR_TEMPLATE_ASSIGN_DELETE_DEFAULT);
        // 删除模板的同时要删除模板下的配置信息
        TemplateConfig templateConfig = new TemplateConfig()
                .setTemplateAssignId(templateAssign.getTemplateAssignId())
                .setTenantId(templateAssign.getTenantId());
        configRepository.removeTemplateConfigsWithCache(templateConfig);
        assignRepository.deleteByPrimaryKey(templateAssign);
    }

    /**
     * 校验数据是否重复
     */
    private void checkRepeat(TemplateAssign templateAssign) {
        int count = assignRepository.selectCountByCondition(
                Condition
                        .builder(TemplateAssign.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(TemplateAssign.FIELD_SOURCE_TYPE,
                                                templateAssign.getSourceType())
                                .andEqualTo(TemplateAssign.FIELD_SOURCE_KEY,
                                                templateAssign.getSourceKey())
                                .andEqualTo(TemplateAssign.FIELD_TENANT_ID,
                                                templateAssign.getTenantId())
                                .andEqualTo(TemplateAssign.FIELD_TEMPLATE_ID,
                                                templateAssign.getTemplateId()))
                        .build());
        if (count > 0) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_TEMPLATE_ASSIGN_EXISTS);
        }
    }
}
