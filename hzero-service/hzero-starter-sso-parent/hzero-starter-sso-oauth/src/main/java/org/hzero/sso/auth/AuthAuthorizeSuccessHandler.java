package org.hzero.sso.auth;

import java.util.Set;

import org.hzero.sso.core.common.SsoAuthorizeSuccessHandler;

/**
 * @author bojiangzhou 2020/08/19
 */
public class AuthAuthorizeSuccessHandler extends SsoAuthorizeSuccessHandler {

    @Override
    protected Set<String> supportiveSsoType() {
        return AuthAttributes.SSO_TYPE;
    }


}
