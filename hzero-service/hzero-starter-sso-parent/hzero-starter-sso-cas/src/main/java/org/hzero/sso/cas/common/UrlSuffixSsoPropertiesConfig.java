package org.hzero.sso.cas.common;

import org.hzero.sso.cas.CasAttributes;
import org.hzero.sso.core.common.config.AbstractSsoPropertiesConfig;
import org.hzero.sso.core.configuration.SsoProperties;

/**
 * urlSuffix字段应用级配置对象
 * <p>
 * 配置方式
 * <pre>
 * hzero:
 *   oauth:
 *     sso:
 *       domain-configs:
 *         - domain: http://test01.com
 *           configs:
 *             - key: urlSuffix
 *               value: public/serviceValidate01
 *         - domain: http://test02.com
 *           configs:
 *             - key: urlSuffix
 *               value: public/serviceValidate02
 * </pre>
 *
 * @author berg-turing 2022/03/18
 */
public class UrlSuffixSsoPropertiesConfig extends AbstractSsoPropertiesConfig {
    public UrlSuffixSsoPropertiesConfig(SsoProperties ssoProperties) {
        super(ssoProperties);
    }

    @Override
    public String getKey() {
        return CasAttributes.CONFIG_FIELD_URL_SUFFIX;
    }
}
