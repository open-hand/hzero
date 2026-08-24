package org.hzero.gateway.helper.api;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.gateway.helper.entity.ResponseContext;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface AuthenticationHelper {
    /**
     * 鉴权入口
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse 不要对 response 做任何关闭处理
     * @return 鉴权信息
     */
    ResponseContext authentication(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
