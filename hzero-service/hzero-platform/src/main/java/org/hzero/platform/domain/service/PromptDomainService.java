package org.hzero.platform.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.domain.entity.Prompt;
import org.hzero.platform.domain.repository.PromptRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;

/**
 * <p>
 * 多语言描述domain service
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/25 13:27
 */
@Component
public class PromptDomainService {

    private final PromptRepository promptRepository;

    private final RedisHelper redisHelper;

    @Autowired
    public PromptDomainService(PromptRepository promptRepository, RedisHelper redisHelper) {
        this.promptRepository = promptRepository;
        this.redisHelper = redisHelper;
    }

    public void initCache() {
        // 加锁，防止重复初始化缓存
        String key = HZeroService.Platform.CODE + ":prompt:init-lock";
        if (!getLock(key)) {
            throw new CommonException(HpfmMsgCodeConstants.REFRESH_CACHE);
        }
        try {
            ApplicationContextHelper.getContext().getBean(PromptDomainService.class).initAllPromptCacheValue(true);
        } catch (Exception e) {
            throw new CommonException(e);
        } finally {
            // 释放锁
            redisHelper.delKey(key);
        }
    }

    /**
     * 将所有的多语言描述信息缓存
     */
    public void initAllPromptCacheValue(boolean doPage) {
        if (doPage) {
            // 分页查询，缓存多语言
            int page = 0;
            int size = 1000;
            boolean flag = true;
            // 执行方法
            List<Prompt> list;
            while (flag) {
                SecurityTokenHelper.close();
                Page<Prompt> pagePrompt = PageHelper.doPage(page, size, promptRepository::selectCachePrompt);
                list = pagePrompt.getContent();
                if (CollectionUtils.isNotEmpty(list)) {
                    this.initPrompt(list);
                    page++;
                    list.clear();
                } else {
                    flag = false;
                }
            }
        } else {
            SecurityTokenHelper.close();
            this.initPrompt(promptRepository.selectCachePrompt());
        }
        SecurityTokenHelper.clear();
    }

    private boolean getLock(String key) {
        if (StringUtils.isBlank(redisHelper.strGet(key))) {
            // 获取锁成功
            redisHelper.strSet(key, "lock", 30, TimeUnit.MINUTES);
            return true;
        } else {
            // 获取锁失败
            return false;
        }
    }

    /**
     * 将租户级多语言list初始化进redis
     *
     * @param promptList 多语言list
     */
    private void initPrompt(List<Prompt> promptList) {
        if (CollectionUtils.isNotEmpty(promptList)) {
            // 平台级多语言描述的map
            Map<String, Map<String, String>> promptTenantMap = new HashMap<>(promptList.size());
            for (Prompt prompt : promptList) {
                // 每个key+lang 统一使用一个map
                Map<String, String> detailMap = promptTenantMap.get(
                        Prompt.generateCacheKey(prompt.getPromptKey(), prompt.getLang(), prompt.getTenantId()));
                if (detailMap == null) {
                    detailMap = new HashMap<>(promptList.size());
                }
                detailMap.put(Prompt.generateMapKey(prompt.getPromptKey(), prompt.getPromptCode()),
                        prompt.getDescription());
                promptTenantMap.put(
                        Prompt.generateCacheKey(prompt.getPromptKey(), prompt.getLang(), prompt.getTenantId()),
                        detailMap);
            }
            // 将汇总的数据存入redis
            Set<Map.Entry<String, Map<String, String>>> entrySet = promptTenantMap.entrySet();
            entrySet.forEach(entry -> redisHelper.hshPutAll(entry.getKey(), entry.getValue()));
        }
    }

}
