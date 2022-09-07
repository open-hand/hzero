package org.hzero.platform.infra.repository.impl;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.PromptDTO;
import org.hzero.platform.domain.entity.Prompt;
import org.hzero.platform.domain.repository.PromptRepository;
import org.hzero.platform.infra.mapper.PromptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 多语言描述 资源库实现
 *
 * @author yunxiang.zhou01@hand-china.com 2018-06-21 16:02:18
 */
@Component
public class PromptRepositoryImpl extends BaseRepositoryImpl<Prompt> implements PromptRepository {

    /**
     * 表达引用关系的开始值
     */
    private static final String START_WITH = "${";

    /**
     * 表达引用关系的末尾值
     */
    private static final String END_WITH = "}";

    /**
     * 表达引用关系中key和code的连接符
     */
    private static final String CONNECTOR = "@";

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private PromptMapper promptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Prompt> insertOrUpdate(List<Prompt> promptList) {
        promptList.forEach(prompt -> {
            // 更新新增判断重复，只要不返回null值即为更新，返回null则为新增
            Long checkResult = judgeRepeatPrompt(prompt);
            if (checkResult == null) {
                // 新增数据，先将promptId设置为null再插入（FIX 处理切换租户后编辑数据并保存时需要将编辑的数据新增到传入租户下的情景）
                prompt.setPromptId(null);
                this.insertPrompt(prompt);
            } else {
                this.updatePrompt(prompt);
            }
        });
        return promptList;
    }

    @Override
    public List<Prompt> insertOrUpdatePromptTenant(List<Prompt> promptList, Long tenantId) {
        if (CollectionUtils.isNotEmpty(promptList)) {
            promptList.forEach(prompt -> {
                Long oldTenantId = judgeRepeatPrompt(prompt);
                if (prompt.getPromptId() == null || prompt.getTenantId() == null || !Objects.equals(prompt.getTenantId(), oldTenantId)) {
                    prompt.setTenantId(tenantId);
                    prompt.setPromptId(null);
                    this.insertPrompt(prompt);
                } else if (tenantId.equals(prompt.getTenantId())) {
                    this.updatePrompt(prompt);
                }
            });
        }
        return promptList;
    }

    /**
     * 判断是否重复
     *
     * @param prompt 多语言
     * @return 原记录租户ID
     */
    private Long judgeRepeatPrompt(Prompt prompt) {
        Prompt tempPrompt = new Prompt();
        tempPrompt.setPromptKey(prompt.getPromptKey());
        tempPrompt.setPromptCode(prompt.getPromptCode());
        tempPrompt.setLang(prompt.getLang());
        tempPrompt.setTenantId(prompt.getTenantId());
        List<Prompt> promptList = this.select(tempPrompt);
        if (CollectionUtils.isNotEmpty(promptList) && promptList.size() > 1) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        } else if (CollectionUtils.isNotEmpty(promptList) && promptList.size() == 1) {
            if (!promptList.get(0).getPromptId().equals(prompt.getPromptId())) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
            }
            return promptList.get(0).getTenantId();
        }
        return null;
    }

    @Override
    public Map<String, String> getDescription(String[] promptKeys, String lang, Long tenantId) {
        final Map<String, String> map = new HashMap<>(16);
        if (promptKeys != null && promptKeys.length > 0) {
            for (String promptKey : promptKeys) {
                if (tenantId == null || tenantId == 0) {
                    String cacheKey = Prompt.generateCacheKey(promptKey, lang, BaseConstants.DEFAULT_TENANT_ID);
                    map.putAll(redisHelper.hshGetAll(cacheKey));
                } else {
                    // 租户级多语言cacheKey
                    String tenantCacheKey = Prompt.generateCacheKey(promptKey, lang, tenantId);
                    // 租户级多语言标签map
                    Map<String, String> promptTenantMap = redisHelper.hshGetAll(tenantCacheKey);
                    // 平台级多语言cacheKey
                    String cacheKey = Prompt.generateCacheKey(promptKey, lang, BaseConstants.DEFAULT_TENANT_ID);
                    // 平台级多语言标签map
                    map.putAll(redisHelper.hshGetAll(cacheKey));
                    Set<Map.Entry<String, String>> entrySet = promptTenantMap.entrySet();
                    // 将租户的个性化语言标签存入平台级的，如有重复则替代，保证了个性化与继承性
                    if (CollectionUtils.isNotEmpty(entrySet)) {
                        entrySet.forEach(entry -> map.put(entry.getKey(), entry.getValue()));
                    }
                }
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                if (CollectionUtils.isNotEmpty(entrySet)) {
                    entrySet.forEach(entry -> entry.setValue(replacePromptDescription(entry.getValue(), tenantId, lang)));
                }
            }
        }
        return map;
    }

    /**
     * 对多语言中的值进行判断，判断是否含有特殊表达式表示引用关系，如果有则替换值 如果含有引用关系，但却找不到替换值，则返回原值
     *
     * @param description 描述
     * @return 替换后的描述
     */
    private String replacePromptDescription(String description, Long tenantId, String lang) {
        tenantId = tenantId == null ? BaseConstants.DEFAULT_TENANT_ID : tenantId;
        String replaceValue = description.trim();
        int index = replaceValue.indexOf(CONNECTOR);
        if (replaceValue.startsWith(START_WITH) && replaceValue.endsWith(END_WITH) && index != -1) {
            replaceValue = replaceValue.substring(START_WITH.length(), replaceValue.length() - END_WITH.length());
            String[] strArray = replaceValue.split(CONNECTOR);
            Assert.isTrue( strArray.length >= 2, BaseConstants.ErrorCode.DATA_INVALID);
            String key = strArray[0];
            String code = strArray[1];
            String cacheKey = Prompt.generateCacheKey(key, lang, tenantId);
            String mapKey = Prompt.generateMapKey(key, code);
            replaceValue = redisHelper.hshGet(cacheKey, mapKey);
            if (replaceValue == null) {
                replaceValue = description;
            }
        }
        return replaceValue;
    }

    @Override
    public List<PromptDTO> selectPrompt(PageRequest pageRequest, Prompt prompt) {
        PageHelper.startPageAndSort(pageRequest);
        return promptMapper.selectPrompt(prompt);
    }

    @Override
    public List<PromptDTO> selectPromptTenant(PageRequest pageRequest, Prompt prompt) {
        PageHelper.startPageAndSort(pageRequest);
        return promptMapper.selectPromptTenant(prompt);
    }

    @Override
    public Prompt insertPrompt(Prompt prompt) {
        this.insertSelective(prompt);
        Prompt.createCache(redisHelper, prompt);
        return prompt;
    }

    @Override
    public Prompt updatePrompt(Prompt prompt) {
        Prompt oldPrompt = this.selectByPrimaryKey(prompt);
        this.updateByPrimaryKey(prompt);
        Prompt.refreshCache(redisHelper, prompt, oldPrompt);
        return prompt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePrompt(Prompt prompt) {
        this.deleteByPrimaryKey(prompt);
        Prompt.deleteCache(redisHelper, prompt);
    }

    @Override
    public void deletePromptTenant(Prompt prompt, Long tenantId) {
        Prompt tempPrompt = new Prompt();
        tempPrompt.setPromptId(prompt.getPromptId());
        tempPrompt.setTenantId(tenantId);
        int count = this.selectCount(tempPrompt);
        if (count == 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        } else if (count == 1) {
            this.deletePrompt(prompt);
        } else {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    @Override
    public Prompt selectOnePrompt(Prompt prompt) {
        return promptMapper.selectOnePrompt(prompt);
    }

    @Override
    public Page<PromptDTO> selectCurrentLangPrompts(PageRequest pageRequest, Prompt prompt) {
        return PageHelper.doPageAndSort(pageRequest, () -> promptMapper.selectPromptTenant(prompt));
    }

    @Override
    public Prompt getPromptDetails(Prompt prompt) {
        // 设置id为null，查询所有当前租户多语言Key和code下维护的语言信息
        Prompt tempPrompt = new Prompt();
        tempPrompt.setPromptKey(prompt.getPromptKey());
        tempPrompt.setPromptCode(prompt.getPromptCode());
        tempPrompt.setTenantId(prompt.getTenantId());
        List<Prompt> dbPrompts = this.select(tempPrompt);
        Assert.notEmpty(dbPrompts, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Map<String, String> configs = new HashMap<>();
        dbPrompts.forEach(pro -> configs.put(pro.getLang(), pro.getDescription()));
        prompt.setPromptConfigs(configs);
        prompt.setLang(null);
        return prompt;
    }

    @Override
    public void insertPromptDescription(List<Prompt> prompts, Long organizationId) {
        if (CollectionUtils.isNotEmpty(prompts)) {
            prompts.forEach(prompt -> {
                prompt.setTenantId(organizationId);
                int count = this.selectCount(prompt);
                if (count != 0) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
                } else {
                    // 新增数据
                    this.insertPrompt(prompt);
                }
            });
        }
    }

    @Override
    public void updatePromptDescription(List<Prompt> prompts, Long organizationId) {
        if (CollectionUtils.isNotEmpty(prompts)) {
            // 更新参数
            prompts.forEach(prompt -> {
                // 根据传入的多语言key和code以及语言和租户的信息查询出数据库中的数据
                Prompt dbPrompt = this.selectOnePrompt(prompt);
                if (dbPrompt == null) {
                    // 数据库中不存在当前语言的描述信息，执行新增操作
                    this.insertPrompt(prompt);
                } else {
                    dbPrompt.setDescription(prompt.getDescription());
                    // 判断修改的多语言数据的所属租户是否和当前租户保持一致
                    if (dbPrompt.getTenantId().equals(organizationId)) {
                        // 编辑的是当前租户的多语言信息，执行更新操作
                        this.updatePrompt(dbPrompt);
                    } else {
                        // 编辑的是预定义数据，将其新增到当前租户下
                        dbPrompt.setPromptId(null);
                        dbPrompt.setTenantId(organizationId);
                        this.insertPrompt(dbPrompt);
                    }
                }
            });
        }
    }

    @Override
    public List<Prompt> selectCachePrompt() {
        return promptMapper.selectCachePrompt();
    }
}
