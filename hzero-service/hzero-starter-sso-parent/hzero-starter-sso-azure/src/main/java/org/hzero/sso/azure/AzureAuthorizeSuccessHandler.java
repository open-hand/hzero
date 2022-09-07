package org.hzero.sso.azure;

import java.util.Set;

import org.hzero.sso.core.common.SsoAuthorizeSuccessHandler;

/**
 *
 * @author bojiangzhou 2020/08/19
 */
public class AzureAuthorizeSuccessHandler extends SsoAuthorizeSuccessHandler {

    @Override
    protected Set<String> supportiveSsoType() {
        return AzureAttributes.SSO_TYPE;
    }
}
