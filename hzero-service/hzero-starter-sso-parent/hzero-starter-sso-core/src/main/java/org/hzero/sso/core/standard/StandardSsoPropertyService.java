package org.hzero.sso.core.standard;

import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.configuration.SsoPropertyService;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class StandardSsoPropertyService implements SsoPropertyService {

    private final SsoProperties ssoProperties;

    public StandardSsoPropertyService(SsoProperties ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    @Override
    public String getBaseUrl() {
        return null;
    }

    @Override
    public String getProcessUrl() {
        return ssoProperties.getProcessUrl();
    }

    @Override
    public String getDisableSsoParameter() {
        return ssoProperties.getDisableSsoParameter();
    }
}
