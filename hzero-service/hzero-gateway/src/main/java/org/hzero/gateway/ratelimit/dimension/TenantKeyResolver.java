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
public class TenantKeyResolver implements KeyResolver, SwitcherDelegate {

    private static final String PREFIX = "tenant";

    private ModeSwitcher modeSwitcher = new DoubleModeSwitcher();

    public TenantKeyResolver() {
        this.modeSwitcher.switchMode(null, null);
    }

    public TenantKeyResolver(String mode, String listString) {
        this.modeSwitcher.switchMode(mode, listString);
    }

    @Override
    public ModeSwitcher getModeSwitcher() {
        return modeSwitcher;
    }

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(Optional.ofNullable(getTenantId(exchange)).orElse(KeyGenerator.generate()));
    }

    private String getTenantId(ServerWebExchange exchange) {
        CustomUserDetails details = RequestContext.currentRequestContext().getCustomUserDetails();
        if (details == null || details.getTenantId() == null){
            return null;
        }
        String key = String.valueOf(details.getTenantId());
        String tenantKey = getModeSwitcher().execute(key);
        return tenantKey == null ? null : PREFIX + tenantKey;
    }

}
