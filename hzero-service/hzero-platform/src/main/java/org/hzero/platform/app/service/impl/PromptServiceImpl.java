
package org.hzero.platform.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.PromptService;
import org.hzero.platform.domain.entity.Prompt;
import org.hzero.platform.domain.repository.PromptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import io.choerodon.core.exception.CommonException;

/**
 *
 * 多语言描述服务类
 *
 * @author xianzhi.chen@hand-china.com 2018年8月9日下午3:12:18
 */
@Service
public class PromptServiceImpl implements PromptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptServiceImpl.class);

    @Autowired
    private PromptRepository promptRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void promptImport(Long tenantId, MultipartFile promptFile) {
        if (promptFile == null) {
            throw new CommonException("error.prompt.file.notFund");
        }
        String originalFilename = promptFile.getOriginalFilename();
        Assert.notNull(originalFilename, BaseConstants.ErrorCode.NOT_NULL);
        // 获取语言
        String lang = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        // 加载文件
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, Map> promptMap = loadPrompt(promptFile, Map.class);
        // 同步数据库
        insertOrUpdatePrompt(tenantId, lang, promptMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Prompt insertPromptDescription(Prompt prompt, Long organizationId) {
        // 校验prompt参数合法性
        prompt.validParams();
        List<Prompt> prompts = this.generateInsertOrUpdatePromptList(prompt);
        promptRepository.insertPromptDescription(prompts, organizationId);
        return prompt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePromptByOptions(Prompt prompt) {
        // 删除当前租户多语言下所有语言的描述信息
        Prompt tempPrompt = new Prompt();
        tempPrompt.setPromptKey(prompt.getPromptKey());
        tempPrompt.setPromptCode(prompt.getPromptCode());
        tempPrompt.setTenantId(prompt.getTenantId());
        List<Prompt> dbPrompts = promptRepository.select(tempPrompt);
        if (CollectionUtils.isNotEmpty(dbPrompts)) {
            dbPrompts.forEach(dbPrompt -> promptRepository.deletePrompt(dbPrompt));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Prompt updatePromptDescription(Prompt prompt, Long organizationId) {
        // 校验prompt参数合法性
        prompt.validParams();
        List<Prompt> prompts = this.generateInsertOrUpdatePromptList(prompt);
        promptRepository.updatePromptDescription(prompts, organizationId);
        return prompt;
    }

    /**
     * 生成插入数据库所需参数
     *
     * @param prompt 传入参数
     */
    private List<Prompt> generateInsertOrUpdatePromptList(Prompt prompt) {
        List<Prompt> prompts = new ArrayList<>();
        // 组装新增参数
        prompt.getPromptConfigs().forEach((lang, description) -> {
            // 组装参数
            Prompt tempPrompt = new Prompt(prompt.getPromptKey(), prompt.getPromptCode(), lang, description,
                    prompt.getTenantId());
            prompts.add(tempPrompt);
        });
        return prompts;
    }

    /**
     *
     * 新增或更新数据库多语言描述
     *
     * @param tenantId
     * @param lang
     * @param promptMap
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void insertOrUpdatePrompt(Long tenantId, String lang, Map<String, Map> promptMap) {
        Set<Entry<String, Map>> entrySet1 = promptMap.entrySet();
        for (Entry<String, Map> es1 : entrySet1) {
            Map<String, Map> m1 = es1.getValue();
            Set<Entry<String, Map>> entrySet2 = m1.entrySet();
            for (Entry<String, Map> es2 : entrySet2) {
                String promptKey = es1.getKey() + "." + es2.getKey();
                try {
                    Map m2 = es2.getValue();
                    Set<Entry<String, String>> entrySet3 = m2.entrySet();
                    for (Entry<String, String> es3 : entrySet3) {
                        Prompt prompt = new Prompt();
                        prompt.setTenantId(tenantId);
                        prompt.setLang(lang);
                        prompt.setPromptKey(promptKey);
                        prompt.setPromptCode(es3.getKey());
                        prompt.setDescription(es3.getValue());
                        Prompt promptResult = promptRepository.selectOnePrompt(prompt);
                        if (promptResult == null || promptResult.getPromptId() == null) {
                            promptRepository.insertPrompt(prompt);
                        } else if (promptResult.getPromptId() != null) {
                            promptResult.setDescription(prompt.getDescription());
                            promptRepository.updatePrompt(promptResult);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Prompt Key {} has Error!!!", promptKey);
                }
            }
        }
    }

    /**
     * 加载多语言资源
     *
     * @param sourceFile 菜单资源/中文语言资源/英文语言资源
     * @param targetClazz 需要最终加载成为的类
     * @return
     */
    private <T> T loadPrompt(MultipartFile sourceFile, Class<T> targetClazz) {
        T result = null;
        Yaml yaml = new Yaml();
        // 加载菜单
        try {
            result = yaml.loadAs(sourceFile.getInputStream(), targetClazz);
        } catch (Exception ex) {
            throw new CommonException("error.prompt.file.loadError", sourceFile.getOriginalFilename());
        }
        return result;
    }

}

