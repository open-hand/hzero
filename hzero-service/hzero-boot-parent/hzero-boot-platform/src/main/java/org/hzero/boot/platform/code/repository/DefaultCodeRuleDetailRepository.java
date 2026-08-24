package org.hzero.boot.platform.code.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;
import org.hzero.boot.platform.code.entity.SimpleCodeRuleDetail;
import org.hzero.boot.platform.code.feign.CodeRuleRemoteService;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 编码规则明细资源层默认实现类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 16:31
 */
public class DefaultCodeRuleDetailRepository implements CodeRuleDetailRepository {
    @Autowired
    private CodeRuleRemoteService codeRuleRemoteService;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<CodeRuleDetail> listCodeRule(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey) {
        // 从缓存中获取
        List<CodeRuleDetail> codeRuleDetailList = listCodeRuleFromCache(codeRuleKey);
        // 缓存中不存在
        if (CollectionUtils.isEmpty(codeRuleDetailList)) {
            // 之前没有在数据库中查过
            if (!isFailFast(codeRuleKey.getFailFastKey())) {
                // 从数据库中查找
                codeRuleDetailList = listCodeRuleFromDatabase(codeRuleKey);
                // 数据库中查不到
                if (CollectionUtils.isEmpty(codeRuleDetailList)) {
                    // 标记失败
                    failFast(codeRuleKey.getFailFastKey());
                } else {
                    // 找到了就加入缓存
                    listCodeRuleToCache(codeRuleKey, codeRuleDetailList);
                }
            } else {
                // 如果之前在数据库中找过并且没找到，刷新快速失败时间
                refreshFailFast(codeRuleKey);
            }
        }
        if (!CollectionUtils.isEmpty(codeRuleDetailList)) {
            // 当前编码规则是否使用过key
            // 判断当前编码规则是否使用过，如果没有使用过，则回写数据库以及redis
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            if (redisHelper.strGet(codeRuleKey.getUsedKey()) == null) {
                // 更新使用标记
                updateDistUseFlag(codeRuleKey.getTenantId(), codeRuleDetailList.get(0).getRuleDistId());
                redisHelper.strSet(codeRuleKey.getUsedKey(), BaseConstants.Flag.YES.toString());
            }
            redisHelper.clearCurrentDatabase();
        }
        return codeRuleDetailList;
    }

    private void refreshFailFast(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        redisHelper.setExpire(codeRuleKey.getFailFastKey(), CodeConstants.FORBIDDEN_TIME);
        redisHelper.clearCurrentDatabase();
    }

    private void listCodeRuleToCache(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey, List<CodeRuleDetail> codeRuleDetailList) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        redisHelper.hshPutAll(codeRuleKey.getKey(),
                codeRuleDetailList
                        .stream()
                        .collect(HashMap::new,
                                (map, element) -> map.put(String.valueOf(element.getOrderSeq()),
                                        redisHelper.toJson(CommonConverter.beanConvert(SimpleCodeRuleDetail.class, element))), HashMap::putAll));
        redisHelper.clearCurrentDatabase();
    }

    @Override
    public List<CodeRuleDetail> listCodeRuleFromCache(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        List<CodeRuleDetail> codeRuleListFromCache = CodeRuleDetail.getCodeRuleListFromCache(redisHelper, codeRuleKey.getKey());
        redisHelper.clearCurrentDatabase();
        return codeRuleListFromCache;
    }

    @Override
    public List<CodeRuleDetail> listCodeRuleFromDatabase(DefaultCodeRuleBuilder.CodeRuleKey codeRuleKey) {
        String previousLevelValue = null;
        DefaultCodeRuleBuilder.CodeRuleKey previousCodeRuleKey = codeRuleKey.getPrevious();
        if (previousCodeRuleKey != null) {
            previousLevelValue = codeRuleKey.getPreviousLevelValue();
        }
        return ResponseUtils.getResponse(codeRuleRemoteService.listCodeRule(
                // 以实际租户ID为准
                previousCodeRuleKey == null ? codeRuleKey.getTenantId() : previousCodeRuleKey.getTenantId(),
                // 降级后的查询条件
                codeRuleKey.getTenantId(), codeRuleKey.getRuleCode(), codeRuleKey.getLevel(), codeRuleKey.getLevelCode(), codeRuleKey.getLevelValue(),
                // 降级前实际请求内容
                previousCodeRuleKey == null ? null : previousCodeRuleKey.getLevel(),
                previousLevelValue),
                new TypeReference<List<CodeRuleDetail>>() {
                });
    }

    @Override
    public boolean isFailFast(String failFastKey) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String failFast = redisHelper.strGet(failFastKey);
        redisHelper.clearCurrentDatabase();
        return failFast != null;
    }

    @Override
    public void failFast(String failFastKey) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        Optional<CustomUserDetails> userDetails = Optional.ofNullable(DetailsHelper.getUserDetails());
        redisHelper.strSet(failFastKey,
                userDetails.isPresent() ? String.valueOf(userDetails.get().getUserId()) : "-1",
                CodeConstants.FORBIDDEN_TIME, TimeUnit.SECONDS);
        redisHelper.clearCurrentDatabase();

    }

    @Override
    public void updateByPrimaryKey(Long tenantId, CodeRuleDetail codeRuleDetail) {
        codeRuleRemoteService.updateByPrimaryKey(tenantId, codeRuleDetail);
    }

    @Override
    public CodeRuleDetail selectByPrimaryKey(Long tenantId, Long ruleDetailId) {
        return codeRuleRemoteService.selectByPrimaryKey(tenantId, ruleDetailId);
    }

    @Override
    public void updateDistUseFlag(Long tenantId, Long ruleDistId) {
        codeRuleRemoteService.updateDistUseFlag(tenantId, ruleDistId);
    }
}
