package org.hzero.oauth.security.secheck;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 二次认证用到的 Authentication，封装登录信息。 认证前，放入手机号/邮箱；认证成功之后，放入用户信息。
 * <p>
 * 参考 {@link UsernamePasswordAuthenticationToken}
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 6523902344385012499L;

    /**
     * 二次认证的认证类型
     */
    private final Object principal;

    public SecCheckAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(false);
    }

    public SecCheckAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
