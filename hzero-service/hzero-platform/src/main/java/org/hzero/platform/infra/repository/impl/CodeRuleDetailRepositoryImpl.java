package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.CodeRuleDetailDTO;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.hzero.platform.domain.repository.CodeRuleRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.CodeRuleDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 编码规则明细repositoryImpl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:58
 */
@Component
public class CodeRuleDetailRepositoryImpl extends BaseRepositoryImpl<CodeRuleDetail>
        implements CodeRuleDetailRepository {

    @Autowired
    private CodeRuleDistRepository codeRuleDistRepository;

    @Autowired
    private CodeRuleRepository codeRuleRepository;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private CodeRuleDetailMapper codeRuleDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRuleDetail insertOrUpdate(CodeRuleDetail codeRuleDetail) {
        if (codeRuleDetail.getRuleDetailId() == null) {
            this.insertCodeRuleDetail(codeRuleDetail);
        } else {
            this.updateCodeRuleDetail(codeRuleDetail);
        }
        return codeRuleDetail;
    }

    @Override
    public CodeRuleDetail insertCodeRuleDetail(CodeRuleDetail codeRuleDetail) {
        this.insertSelective(codeRuleDetail);
        codeRuleDetail = generateCacheKeyByDetail(codeRuleDetail);
        if (BaseConstants.Flag.YES.equals(codeRuleDetail.getEnabledFlag())) {
            codeRuleRepository.clearFailFastCache(codeRuleDetail.getCodeRuleKey().getTenantId(),
                    codeRuleDetail.getCodeRuleKey().getRuleCode(),
                    codeRuleDetail.getCodeRuleKey().getLevel(),
                    codeRuleDetail.getCodeRuleKey().getLevelCode(),
                    codeRuleDetail.getCodeRuleKey().getLevelValue());
            List<CodeRuleDetail> codeRuleDetailList = codeRuleDetailMapper.selectDetailListByRuleCode(codeRuleDetail.getCodeRuleKey().getTenantId(),
                    codeRuleDetail.getCodeRuleKey().getRuleCode(),
                    codeRuleDetail.getCodeRuleKey().getLevel(),
                    codeRuleDetail.getCodeRuleKey().getLevelCode(),
                    codeRuleDetail.getCodeRuleKey().getLevelValue());
            if (codeRuleDetailList == null) {
                codeRuleDetailList = new ArrayList<>();
            }
            // 更新完了数据库之后更新redis
            codeRuleDetailList.add(codeRuleDetail);
            // 对新增后的编码规则明细进行排序
            CodeRuleDetail.initCache(redisHelper, codeRuleDetail.getCodeRuleKey().getKey(), codeRuleDetailList);
        }
        return codeRuleDetail;
    }

    @Override
    public CodeRuleDetail updateCodeRuleDetail(CodeRuleDetail codeRuleDetail) {
        codeRuleDetail = generateCacheKeyByDetail(codeRuleDetail);
        // 采用先删除原先数据，然后再新增数据的方式修改
        this.deleteByPrimaryKey(codeRuleDetail);
        codeRuleDetail.setRuleDetailId(null);
        this.insertSelective(codeRuleDetail);
        List<CodeRuleDetail> codeRuleDetailList = this.select(CodeRuleDetail.FIELD_RULE_DIST_ID, codeRuleDetail.getRuleDistId());
        // 对修改后的编码规则明细进行排序
        if (BaseConstants.Flag.YES.equals(codeRuleDetail.getEnabledFlag())) {
            CodeRuleDetail.initCache(redisHelper, codeRuleDetail.getCodeRuleKey().getKey(), codeRuleDetailList);
        }
        return codeRuleDetail;
    }

    /**
     * 根据编码规则明细查询得到当前编码规则明细redis所存储的key
     *
     * @param codeRuleDetail 编码规则明细
     * @return key
     */
    private CodeRuleDetail generateCacheKeyByDetail(CodeRuleDetail codeRuleDetail) {
        CodeRuleDist codeRuleDist = new CodeRuleDist();
        codeRuleDist.setRuleDistId(codeRuleDetail.getRuleDistId());
        codeRuleDist = codeRuleDistRepository.selectByPrimaryKey(codeRuleDist);
        if (codeRuleDist == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        CodeRule codeRule = new CodeRule();
        codeRule.setRuleId(codeRuleDist.getRuleId());
        codeRule = codeRuleRepository.selectByPrimaryKey(codeRule);
        return codeRuleDetail.setEnabledFlag(codeRuleDist.getEnabledFlag())
                .setCodeRuleKey(new DefaultCodeRuleBuilder.CodeRuleKey(codeRule.getTenantId(), codeRule.getRuleCode(), codeRule.getRuleLevel(), codeRuleDist.getLevelCode(), codeRuleDist.getLevelValue()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDetail(Long tenantId, List<CodeRuleDetail> codeRuleDetailList) {
        if (CollectionUtils.isNotEmpty(codeRuleDetailList)) {
            CodeRuleDetail ruleDetail = codeRuleDetailList.get(0);
            // 判断当前删除当前数据操作行为的合法性
            CodeRuleDist codeRuleDist = codeRuleDistRepository.selectByPrimaryKey(ruleDetail.getRuleDistId());
            if (!CodeRule.judgeDataLegality(codeRuleRepository, tenantId, codeRuleDist.getRuleId())) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }
            // 判断当前分配是否使用过，使用过则不允许删除
            if (BaseConstants.Flag.YES.equals(codeRuleDist.getUsedFlag())) {
                throw new CommonException(HpfmMsgCodeConstants.ERROR_PERMISSION_RULE_FORBID_DELETE);
            }
            // 删除
            CodeRuleDetail finalRuleDetail = generateCacheKeyByDetail(ruleDetail);
            codeRuleDetailList.forEach(codeRuleDetail -> {
                CodeRuleDetail tempDetail = this.selectByPrimaryKey(codeRuleDetail.getRuleDetailId());
                if (tempDetail != null && tempDetail.getFieldType().equals(FndConstants.FieldType.SEQUENCE)) {
                    redisHelper.delKey(finalRuleDetail.getCodeRuleKey().getSequenceKey());
                }
                // 删除编码段行缓存
                redisHelper.hshDelete(finalRuleDetail.getCodeRuleKey().getKey(), codeRuleDetail.getOrderSeq().toString());
                this.deleteByPrimaryKey(codeRuleDetail);
            });

            List<CodeRuleDetail> newCodeRuleDetail =
                    this.select(CodeRuleDetail.FIELD_RULE_DIST_ID, ruleDetail.getRuleDistId());
            if (BaseConstants.Flag.YES.equals(ruleDetail.getEnabledFlag())) {
                CodeRuleDetail.initCache(redisHelper, finalRuleDetail.getCodeRuleKey().getKey(), newCodeRuleDetail);
            }
        }
    }


    @Override
    public List<CodeRuleDetailDTO> selectCodeRuleDetailList(Long ruleDistId) {
        return codeRuleDetailMapper.selectCodeRuleDetailList(ruleDistId);
    }

    @Override
    public int updateCodeRuleDetailWithNotOVN(CodeRuleDetail codeRuleDetail) {
        return codeRuleDetailMapper.updateCodeRuleDetailWithNotOVN(codeRuleDetail);
    }

    @Override
    public List<CodeRuleDetail> selectDetailListByRuleCode(Long organizationId, String ruleCode,
                                                           String ruleLevel, String levelCode, String levelValue) {
        return codeRuleDetailMapper.selectDetailListByRuleCode(organizationId, ruleCode, ruleLevel, levelCode, levelValue);
    }

    @Override
    public List<CodeRuleDetail> listCodeRuleWithPrevious(Long tenantId, String ruleCode, String ruleLevel, String levelCode, String levelValue,
                                                         String previousRuleLevel, String previousLevelValue) {
        return codeRuleDetailMapper.listCodeRuleWithPrevious(tenantId, ruleCode, ruleLevel, levelCode, levelValue,
                previousRuleLevel, previousLevelValue);
    }
}
