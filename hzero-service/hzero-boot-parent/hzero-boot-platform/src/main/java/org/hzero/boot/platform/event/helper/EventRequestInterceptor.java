package org.hzero.boot.platform.event.helper;

import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * 实现该接口为 RestTemplate 加入 Header <br/>
 * <pre>
 *      HttpHeaders headers = request.getHeaders();
 *      headers.add(key, value);
 *      execution.execute(request, body)
 * <pre/>
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/15 9:45
 * @see ClientHttpRequestInterceptor
 */
public interface EventRequestInterceptor extends ClientHttpRequestInterceptor {

}
