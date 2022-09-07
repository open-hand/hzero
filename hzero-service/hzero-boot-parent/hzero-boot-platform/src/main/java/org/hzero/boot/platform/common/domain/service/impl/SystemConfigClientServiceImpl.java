package org.hzero.boot.platform.common.domain.service.impl;

import io.choerodon.mybatis.helper.LanguageHelper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.common.domain.service.SystemConfigClientService;
import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 系统配置服务实现
 *
 * @author bergturing 2020/08/18 15:13
 */
@Service
public class SystemConfigClientServiceImpl implements SystemConfigClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigClientServiceImpl.class);
    /**
     * RedisHelper
     */
    private final RedisHelper redisHelper;

    @Autowired
    public SystemConfigClientServiceImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public String getSystemConfigByConfigCode(String configCode, Long tenantId) {
        String result = redisHelper.strGet(this.generateConfigCacheKey(configCode, tenantId));
        if (StringUtils.isBlank(result)) {
            result = redisHelper.strGet(this.generateConfigCacheKey(configCode, BaseConstants.DEFAULT_TENANT_ID));
        }
        if (HZeroConstant.Config.CONFIG_CODE_TITLE.equals(configCode)) {
            return processTitleMultiLanguage(result);
        }
        return result;
    }

    @Override
    public String getSystemConfigByConfigCode(String configCode) {
        return getSystemConfigByConfigCode(configCode, BaseConstants.DEFAULT_TENANT_ID);
    }

    /**
     * 生成系统配置缓存Key
     *
     * @param configCode 系统配置编码
     * @param tenantId   租户Id
     * @return 缓存Key
     */
    private String generateConfigCacheKey(String configCode, Long tenantId) {
        return HZeroService.Platform.CODE + ":config" + ":" + configCode + "." + tenantId;
    }

    /**
     * 处理标题类型的多语言数据
     *
     * @return 处理后的标题内容
     */
    @SuppressWarnings("unchecked")
    private String processTitleMultiLanguage(String title) {
        if (StringUtils.isBlank(title)) {
            return "";
        }
        try{
            Map<String, String> resultMap =
                    (Map<String, String>) redisHelper.fromJson(title, Map.class);
            String language = LanguageHelper.language();
            return resultMap.get(language);
        }catch(Exception e) {
            LOGGER.error(">>>>>>title config can not convert to map, please refresh config cache!");
            return title;
        }
    }
}
