package org.hzero.sso.core.standard;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.sso.core.common.SsoAuthenticationFactory;
import org.hzero.sso.core.common.SsoAuthenticationProvider;
import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.common.SsoServerLogoutHandler;
import org.hzero.sso.core.constant.SsoEnum;

/**
 * @author bojiangzhou 2020/08/17
 */
public class StandardAuthenticationFactory implements SsoAuthenticationFactory {

    @Autowired
    private StandardAuthenticationRouter authenticationRouter;
    @Autowired
    private StandardAuthenticationProvider authenticationProvider;

    @Override
    public Set<String> supportiveSsoType() {
        return ImmutableSet.of(SsoEnum.STANDARD.code());
    }

    @Override
    public SsoAuthenticationRouter getAuthenticationRouter() {
        return authenticationRouter;
    }

    @Override
    public SsoAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    @Override
    public SsoServerLogoutHandler getLogoutHandler() {
        return new StandardServerLogoutHandler();
    }

}
