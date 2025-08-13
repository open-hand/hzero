package org.hzero.sso.idm;

import java.util.Set;

import org.hzero.sso.core.common.SsoAuthorizeSuccessHandler;

/**
 *
 * @author bojiangzhou 2020/08/20
 */
public class IdmAuthorizeSuccessHandler extends SsoAuthorizeSuccessHandler {

    @Override
    protected Set<String> supportiveSsoType() {
        return IdmAttributes.SSO_TYPE;
    }
}
