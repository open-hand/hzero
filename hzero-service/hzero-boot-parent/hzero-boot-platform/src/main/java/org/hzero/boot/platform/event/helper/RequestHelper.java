package org.hzero.boot.platform.event.helper;

import org.hzero.boot.platform.event.vo.ApiParam;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * 发起Http请求
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/14 11:56
 */
public interface RequestHelper {

    /**
     * 发起Http请求，支持GET、POST、PUT、DELETE
     *
     * @param url    URL地址
     * @param method {@link HttpMethod} 请求方法，支持GET、POST、PUT、DELETE<br/>
     *
     *               <pre>
     *                      GET 参数拼接在地址后面，如：http://examples/{id}/rules?name={name}&enableFlag={enableFlag}<br/>
     *                      POST/PUT/DELETE 统一使用"application/json"格式发起请求，接收请求的API使用 "@RequestBody" 接收参数
     *                      。也支持在地址后拼接参数，使用@RequestParam接收<br/>
     *
     *                      <pre/>
     *               @param apiParam {@link ApiParam} Api参数
     *               @return 请求结果
     */
    ResponseEntity<Object> request(String url, HttpMethod method, ApiParam apiParam);

}
