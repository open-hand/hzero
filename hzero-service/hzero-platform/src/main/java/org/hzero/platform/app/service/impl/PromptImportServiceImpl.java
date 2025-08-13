package org.hzero.platform.app.service.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.domain.entity.Prompt;
import org.hzero.platform.domain.repository.PromptRepository;
import org.hzero.platform.domain.service.PromptDomainService;
import org.hzero.platform.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 平台多语言导入
 *
 * @author dingzf 2019/09/25 17:06
 */
@ImportService(templateCode = Constants.ImportTemplateCode.PROMPT_TEMP)
public class PromptImportServiceImpl implements IDoImportService {

    private static final Logger logger = LoggerFactory.getLogger(PromptImportServiceImpl.class);

    @Autowired
    private PromptRepository promptRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PromptDomainService promptDomainService;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public Boolean doImport(String data) {
        Long tenantId = BaseConstants.DEFAULT_TENANT_ID;
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails != null) {
            tenantId = userDetails.getTenantId();
        }
        try {
            Prompt prompt = objectMapper.readValue(data, Prompt.class);
            prompt.setTenantId(tenantId);
            Prompt param = new Prompt();
            param.setPromptCode(prompt.getPromptCode());
            param.setPromptKey(prompt.getPromptKey());
            param.setLang(prompt.getLang());
            param.setTenantId(tenantId);
            Prompt oldPrompt = promptRepository.selectOne(param);
            if (oldPrompt != null) {
                // 更新数据
                prompt.setPromptId(oldPrompt.getPromptId());
                prompt.setObjectVersionNumber(oldPrompt.getObjectVersionNumber());
                // 清除缓存
                Prompt.deleteCache(redisHelper, prompt);
                promptRepository.updateByPrimaryKeySelective(prompt);
            } else {
                promptRepository.insertSelective(prompt);
            }
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        return true;
    }

    @Override
    public void onFinish() {
        // 添加缓存
        try {
            promptDomainService.initCache();
        } catch (Exception e) {
            logger.error("Refresh cache failed, msg : {}", e.getMessage());
        }
    }
}
