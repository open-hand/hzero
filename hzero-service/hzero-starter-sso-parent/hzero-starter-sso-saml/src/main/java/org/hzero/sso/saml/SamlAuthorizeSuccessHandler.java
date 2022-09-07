package org.hzero.sso.saml;

import java.util.Set;

import org.hzero.sso.core.common.SsoAuthorizeSuccessHandler;

/**
 *
 * @author bojiangzhou 2020/08/21
 */
public class SamlAuthorizeSuccessHandler extends SsoAuthorizeSuccessHandler {

    @Override
    protected Set<String> supportiveSsoType() {
        return SamlAttributes.SSO_TYPE;
    }
}
