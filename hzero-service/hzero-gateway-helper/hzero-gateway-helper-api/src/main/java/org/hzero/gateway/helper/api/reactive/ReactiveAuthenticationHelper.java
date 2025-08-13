package org.hzero.gateway.helper.api.reactive;

import org.hzero.gateway.helper.entity.ResponseContext;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;

/**
 * @author XCXCXCXCX
 * @date 2019/9/4
 * @project hzero-gateway-helper
 */
public interface ReactiveAuthenticationHelper {

    /**
     * 鉴权入口
     *
     * @param exchange HttpServletRequest
     * @return 鉴权信息
     */
    ResponseContext authentication(ServerWebExchange exchange) throws IOException;

}
