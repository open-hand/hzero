package org.hzero.gateway.filter;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 添加 X-Forwarded-For 记录客户端真实IP
 *
 * @author bojiangzhou 2019/03/06
 */
public class XForwardedForFilter implements GlobalFilter, Ordered {

    private static final String HTTP_X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> xForwardedForList = new ArrayList<>();
        List<String> temp = request.getHeaders().get(HTTP_X_FORWARDED_FOR);
        if (CollectionUtils.isNotEmpty(temp)) {
            xForwardedForList.addAll(temp);
        }
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (Objects.nonNull(remoteAddress)) {
            xForwardedForList.add(remoteAddress.getHostString());
        }
        String xForwardedFor = String.join(",", xForwardedForList);
        return chain.filter(
                exchange.mutate()
                        .request(
                                builder -> builder.header(HTTP_X_FORWARDED_FOR, xForwardedFor)
                        )
                        .build());
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
