package org.hzero.boot.platform.event.helper.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.event.helper.EventRequestInterceptor;
import org.hzero.boot.platform.event.helper.RequestHelper;
import org.hzero.boot.platform.event.vo.ApiParam;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

/**
 * {@link RequestHelper} 默认实现
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/14 16:08
 */
public class DefaultRequestHelper implements RequestHelper, ApplicationContextAware {

    private volatile RestTemplate restTemplate;

    private ApplicationContext context;

    private HttpHeaders headers;

    @PostConstruct
    public void init() {
        buildRestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    public ResponseEntity<Object> request(String url, HttpMethod method, ApiParam apiParam) {
        if (restTemplate == null) {
            buildRestTemplate();
        }
        // 处理URI参数
        Object[] uriArgs = getUriArgs(url, apiParam);
        return restTemplate.exchange(url, method, getHttpEntity(method, apiParam), Object.class, uriArgs);
    }

    private HttpEntity<Object> getHttpEntity(HttpMethod method, ApiParam apiParam) {
        if (HttpMethod.GET.equals(method)) {
            return null;
        }
        return new HttpEntity<>(apiParam, headers);
    }

    /**
     * 创建 RestTemplate
     */
    private synchronized void buildRestTemplate() {
        if (restTemplate == null) {
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            // 连接超时时间2分钟
            httpRequestFactory.setConnectTimeout(120000);
            // 读取超时时间5分钟
            httpRequestFactory.setReadTimeout(300000);

            this.restTemplate = new RestTemplate(httpRequestFactory);
            // 添加token拦截器
            restTemplate.getInterceptors().addAll(getEventRequestInterceptor());
        }
    }

    private Collection<EventRequestInterceptor> getEventRequestInterceptor() {
        Map<String, EventRequestInterceptor> interceptors = context.getBeansOfType(EventRequestInterceptor.class);
        return interceptors.values();
    }

    /**
     * 获取URI参数
     */
    private Object[] getUriArgs(String url, ApiParam apiParam) {
        Object[] uriArgs = ArrayUtils.EMPTY_OBJECT_ARRAY;
        if (StringUtils.containsAny(url, "{", "}")) {
            String[] uriArgNames = StringUtils.substringsBetween(url, "{", "}");
            uriArgs = new Object[uriArgNames.length];
            for (int i = 0; i < uriArgNames.length; i++) {
                uriArgs[i] = apiParam.get(uriArgNames[i]);
            }
        }
        return uriArgs;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
