package org.hzero.websocket.interceptor;

import static org.hzero.core.variable.RequestVariableHolder.HEADER_AUTH;
import static org.hzero.core.variable.RequestVariableHolder.HEADER_BEARER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.websocket.config.WebSocketConfig;
import org.hzero.websocket.constant.WebSocketConstant;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * websocket拦截器
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/19 10:58
 */
public class WebSocketInterceptor implements HandshakeInterceptor {

    private static final String TOKEN_PREFIX = HEADER_BEARER + " ";

    private final Map<String, SocketInterceptor> interceptors = new HashMap<>();

    private Map<String, SocketInterceptor> getInterceptors() {
        if (interceptors.size() > 0) {
            return interceptors;
        }
        Map<String, SocketInterceptor> map = ApplicationContextHelper.getContext().getBeansOfType(SocketInterceptor.class);
        for (SocketInterceptor interceptor : map.values()) {
            interceptors.put(interceptor.processor(), interceptor);
        }
        return interceptors;
    }

    /**
     * handler处理前调用,attributes属性最终在WebSocketSession里,可能通过webSocketSession.getAttributes().get(key值)获得
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            boolean result = true;
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            // 将所有路径参数添加到attributes
            for (Map.Entry<String, String[]> entry : serverHttpRequest.getServletRequest().getParameterMap().entrySet()) {
                attributes.put(entry.getKey(), entry.getValue()[0]);
            }
            // 从header取token
            List<String> list = serverHttpRequest.getHeaders().get(HEADER_AUTH);
            if (CollectionUtils.isNotEmpty(list)) {
                String accessToken = list.get(0);
                if (StringUtils.startsWithIgnoreCase(accessToken, TOKEN_PREFIX)) {
                    accessToken = accessToken.substring(TOKEN_PREFIX.length());
                }
                attributes.put(WebSocketConstant.Attributes.TOKEN, accessToken);
            }
            if (!attributes.containsKey(WebSocketConstant.Attributes.SECRET_KEY) && !attributes.containsKey(WebSocketConstant.Attributes.TOKEN)) {
                // 未携带密钥和token，连接断开
                return false;
            }
            // 密钥链接
            if (attributes.containsKey(WebSocketConstant.Attributes.SECRET_KEY)) {
                // 验证密钥
                String key = ApplicationContextHelper.getContext().getBean(WebSocketConfig.class).getSecretKey();
                if (!Objects.equals(attributes.get(WebSocketConstant.Attributes.SECRET_KEY), key)) {
                    return false;
                }
            }
            // 获取所有拦截器
            Map<String, SocketInterceptor> interceptorMap = getInterceptors();
            String processor = String.valueOf(attributes.get(WebSocketConstant.Attributes.PROCESSOR));
            if (StringUtils.isNotBlank(processor) && interceptorMap.containsKey(processor)) {
                SocketInterceptor interceptor = interceptorMap.get(processor);
                // 执行type对应的拦截器
                result = interceptor.beforeHandshake(request, response, wsHandler, attributes);
            }
            // group拦截器通过，执行默认拦截器
            if (result) {
                return interceptorMap.get(WebSocketConstant.DEFAULT_PROCESSOR).beforeHandshake(request, response, wsHandler, attributes);
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            // 获取所有拦截器
            Map<String, SocketInterceptor> interceptorMap = getInterceptors();
            String processor = serverHttpRequest.getServletRequest().getParameter(WebSocketConstant.Attributes.PROCESSOR);
            if (StringUtils.isNotBlank(processor) && interceptorMap.containsKey(processor)) {
                SocketInterceptor interceptor = interceptorMap.get(processor);
                // 执行group对应的拦截器
                interceptor.afterHandshake(request, response, wsHandler, exception);
            }
            interceptorMap.get(WebSocketConstant.DEFAULT_PROCESSOR).afterHandshake(request, response, wsHandler, exception);
        }
    }
}
