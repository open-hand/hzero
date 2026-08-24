package org.hzero.gateway.helper.service;

import org.hzero.gateway.helper.entity.RequestContext;

/**
 * API 签名服务
 *
 * @author bojiangzhou 2019/12/26
 */
public interface SignatureService {

    /**
     * API 签名验证
     * 
     * @param requestContext RequestContext，包含请求的所有信息
     * @return true-验证通过; false-验证不通过
     */
    boolean verifySignature(RequestContext requestContext);

}
