package org.hzero.platform.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ValidUtils;
import org.hzero.mybatis.domian.Language;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ConfigService;
import org.hzero.platform.domain.entity.Config;
import org.hzero.platform.domain.repository.ConfigRepository;
import org.hzero.platform.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.mybatis.helper.LanguageHelper;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2019/1/16 星期三 10:27
 */
@Service
public class ConfigServiceImpl implements ConfigService {
    private static final String CONFIG_CODE_LOGO = "LOGO";
    private ConfigRepository configRepository;
    private Validator validator;

    @Autowired
    public ConfigServiceImpl(ConfigRepository configRepository, Validator validator) {
        this.configRepository = configRepository;
        this.validator = validator;
    }

    @Override
    public List<Config> initCompanyConfig(Long tenantId, List<Config> configList) {
        if (CollectionUtils.isNotEmpty(configList)) {
            for (Config item : configList) {
                if (CONFIG_CODE_LOGO.equals(item.getConfigCode()) && StringUtils.isEmpty(item.getConfigValue())) {
                    Config platformLogoConfig = new Config();
                    platformLogoConfig.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
                    platformLogoConfig.setConfigCode(CONFIG_CODE_LOGO);
                    platformLogoConfig = configRepository.selectOne(platformLogoConfig);
                    if (platformLogoConfig != null) {
                        item.setConfigValue(platformLogoConfig.getConfigValue());
                    } else {
                        configList.remove(item);
                    }
                }
                if (Constants.CONFIG_CODE_TITLE.equals(item.getConfigCode()) && MapUtils.isEmpty(item.get_tls())) {
                    // 设置tls对象
                    Map<String, Map<String, String>> tlsMap = new HashMap<>();
                    Map<String, String> valueMap = new HashMap<>();
                    List<Language> languages = LanguageHelper.languages();
                    languages.forEach(language -> {
                        valueMap.put(language.getCode(), item.getConfigValue());
                    });
                    tlsMap.put(Config.FIELD_CONFIG_VALUE, valueMap);
                    item.set_tls(tlsMap);
                }
            }
        }
        ValidUtils.valid(validator, configList);
        SecurityTokenHelper.validTokenIgnoreInsert(configList);
        return configRepository.insertOrUpdateConfig(configList, tenantId);
    }
}
