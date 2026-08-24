package org.hzero.gateway.helper.service.impl;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import org.hzero.core.redis.RedisHelper;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.config.GatewayHelperProperties.Secret;
import org.hzero.gateway.helper.config.GatewayHelperProperties.Signature;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.SignatureService;
import org.hzero.gateway.helper.util.SignatureUtils;

/**
 * 签名服务默认实现
 *
 * @author bojiangzhou 2019/12/26
 */
public class DefaultSignatureService implements SignatureService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSignatureService.class);

    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    private static final String NONCE_PREFIX = "gateway:nonce:%s";

    private final Signature signature;
    private final RedisHelper redisHelper;
    private int expireMillisecond;


    public DefaultSignatureService(GatewayHelperProperties properties, RedisHelper redisHelper) {
        this.signature = properties.getSignature();
        this.expireMillisecond = signature.getExpireTime() * 1000;
        this.redisHelper = redisHelper;
    }

    @Override
    public boolean verifySignature(RequestContext requestContext) {
        Object servletRequest = requestContext.getServletRequest();
        if (!SignatureUtils.supported(HttpMethod.resolve(requestContext.request.method.toUpperCase()))) {
            LOGGER.info("Not supported http method, method is {}", requestContext.request.method);
            return false;
        }
        // 获取请求参数
        Map<String, Object> params = null;
        if (servletRequest instanceof ServerHttpRequest) {
            ServerHttpRequest request = (ServerHttpRequest) servletRequest;
            params = SignatureUtils.getParamMap(request);

        } else if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            params = SignatureUtils.getParamMap(request);
        }

        SignInfo signInfo = SignInfo.parseSignInfo(signature, params);
        if (signInfo == null) {
            return false;
        }

        return verify(requestContext, signInfo, params);
    }

    protected boolean verify(RequestContext context, SignInfo signInfo, Map<String, Object> params) {
        // 验证签名客户端
        Secret secret = signature.getSecrets().stream().filter(c -> StringUtils.equals(c.getSecretId(), signInfo.getSecretId())).findFirst().orElse(null);
        if (secret == null) {
            LOGGER.info("Invalid sign client. signInfo is {}", signInfo);
            return false;
        }

        // 验证时间
        if (System.currentTimeMillis() - Long.parseLong(signInfo.getTimestamp()) > expireMillisecond) {
            LOGGER.info("Sign info is expired.");
            return false;
        }

        // 验证唯一随机数
        if (redisHelper.hasKey(String.format(NONCE_PREFIX, signInfo.getNonce()))) {
            LOGGER.info("Nonce key exists, repeat request. signInfo is {}", signInfo);
            return false;
        } else {
            // 缓存唯一key
            redisHelper.strSet(String.format(NONCE_PREFIX, signInfo.getNonce()), "1", signature.getExpireTime(), TimeUnit.SECONDS);
        }

        // 验证签名
        String paramSignature = valueOf(params.get(SignInfo.PARAM_SIGNATURE));
        String signature = SignatureUtils.getSignature(params, HttpMethod.resolve(context.request.method.toUpperCase()), secret.getSecretId(), secret.getSecretKey());
        if (!StringUtils.equals(paramSignature, signature)) {
            LOGGER.warn("Signature incorrect, params is {}, paramSignature is {}", params, paramSignature);
            return false;
        }

        return true;
    }

    private static String valueOf(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 签名信息
     */
    static class SignInfo {
        static final String PARAM_TIMESTAMP = "timestamp";
        static final String PARAM_SECRET_ID = "secretId";
        static final String PARAM_NONCE = "nonce";
        static final String PARAM_SIGNATURE = "signature";

        private String timestamp;
        private String secretId;
        private String nonce;
        private String signature;

        private SignInfo(String timestamp, String secretId, String nonce, String signature) {
            this.timestamp = timestamp;
            this.secretId = secretId;
            this.nonce = nonce;
            this.signature = signature;
        }

        /**
         * 从请求参数中解析签名信息，并做基本的参数校验
         *
         * @param signature 签名配置
         * @param map 请求参数
         * @return 无法解析出签名信息或解析过程校验错误都返回 null.
         */
        @Nullable
        static SignInfo parseSignInfo(Signature signature, Map<String, Object> map) {
            Assert.notNull(map, "params map is null.");
            if (map.get(PARAM_TIMESTAMP) == null || StringUtils.isBlank(valueOf(map.get(PARAM_TIMESTAMP)))) {
                LOGGER.info("Sign param [timestamp] is blank");
                return null;
            }
            if (map.get(PARAM_SECRET_ID) == null || StringUtils.isBlank(valueOf(map.get(PARAM_SECRET_ID)))) {
                LOGGER.info("Sign param [secretId] is blank");
                return null;
            }
            if (map.get(PARAM_NONCE) == null || StringUtils.isBlank(valueOf(map.get(PARAM_NONCE)))) {
                LOGGER.info("Sign param [nonce] is blank");
                return null;
            }
            if (map.get(PARAM_SIGNATURE) == null || StringUtils.isBlank(valueOf(map.get(PARAM_SIGNATURE)))) {
                LOGGER.info("Sign param [signature] is blank");
                return null;
            }

            if (!NumberUtils.isDigits(valueOf(map.get(PARAM_TIMESTAMP)))) {
                LOGGER.info("Incorrect sign timestamp format. timestamp is {}", map.get(PARAM_TIMESTAMP));
                return null;
            }

            return new SignInfo(valueOf(map.get(PARAM_TIMESTAMP)), valueOf(map.get(PARAM_SECRET_ID)), valueOf(map.get(PARAM_NONCE)), valueOf(map.get(PARAM_SIGNATURE)));
        }



        public String getTimestamp() {
            return timestamp;
        }

        public String getSecretId() {
            return secretId;
        }

        public String getNonce() {
            return nonce;
        }

        public String getSignature() {
            return signature;
        }


        @Override
        public String toString() {
            return new StringJoiner(", ", SignInfo.class.getSimpleName() + "[", "]")
                            .add("timestamp='" + timestamp + "'").add("secretId='" + secretId + "'")
                            .add("nonce='" + nonce + "'").add("signature='" + signature + "'").toString();
        }
    }



}
