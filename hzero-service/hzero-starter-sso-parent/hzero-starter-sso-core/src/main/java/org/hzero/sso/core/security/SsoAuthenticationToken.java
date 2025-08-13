package org.hzero.sso.core.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author bojiangzhou 2020/08/18
 */
public class SsoAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = -3206261462124420215L;

    public SsoAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public SsoAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
