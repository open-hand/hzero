package org.hzero.gateway.ratelimit.dimension;

import io.choerodon.core.oauth.CustomUserDetails;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.ratelimit.switcher.DoubleModeSwitcher;
import org.hzero.gateway.ratelimit.switcher.ModeSwitcher;
import org.hzero.gateway.ratelimit.switcher.SwitcherDelegate;
import org.hzero.gateway.util.KeyGenerator;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author XCXCXCXCX
 * @date 2019/10/17
 * @project hzero-gateway
 */
public class UserKeyResolver implements KeyResolver, SwitcherDelegate {

    private static final String PREFIX = "user";

    private ModeSwitcher modeSwitcher = new DoubleModeSwitcher();

    public UserKeyResolver() {
        this.modeSwitcher.switchMode(null, null);
    }

    public UserKeyResolver(String mode, String listString) {
        this.modeSwitcher.switchMode(mode, listString);
    }

    @Override
    public ModeSwitcher getModeSwitcher() {
        return modeSwitcher;
    }

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(Optional.ofNullable(getUserId()).orElse(KeyGenerator.generate()));
    }

    private String getUserId() {
        CustomUserDetails details = RequestContext.currentRequestContext().getCustomUserDetails();
        if (details == null || details.getUserId() == null){
            return null;
        }
        String key = String.valueOf(details.getUserId());
        String userKey = getModeSwitcher().execute(key);
        return userKey == null ? null : PREFIX + userKey;
    }

}
