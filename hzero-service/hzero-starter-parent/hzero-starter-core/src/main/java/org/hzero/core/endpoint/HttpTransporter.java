package org.hzero.core.endpoint;

import org.springframework.web.client.RestClientException;

/**
 *
 * Http 通信接口
 *
 * @author XCXCXCXCX
 * @date 2020/4/22 10:47 上午
 */
public interface HttpTransporter<T> {

    T transport(HttpRequest<T> request) throws RestClientException;

}


