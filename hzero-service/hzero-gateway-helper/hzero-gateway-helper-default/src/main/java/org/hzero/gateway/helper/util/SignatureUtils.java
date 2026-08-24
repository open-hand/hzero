package org.hzero.gateway.helper.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Charsets;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 签名工具
 *
 * @author bojiangzhou 2019/12/26
 */
public final class SignatureUtils {

    /**
     * 签名原文的模板 <i>hzero-api/{method}/{timestamp}/{nonce}/{secretId}?{params}</i>
     */
    private static final String SIGN_TEMPLATE = "hzero-api/%s/%s/%s/%s?%s";
    private static final String MAC_NAME = "HmacSHA1";

    private static final Set<HttpMethod> SUPPORT_METHOD = Stream.of(HttpMethod.POST, HttpMethod.GET,
            HttpMethod.DELETE, HttpMethod.PUT).collect(Collectors.toSet());

    public static final String PARAM_TIMESTAMP = "timestamp";
    public static final String PARAM_SECRET_ID = "secretId";
    public static final String PARAM_NONCE = "nonce";
    public static final String PARAM_SIGNATURE = "signature";

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("username", "test");
        params.put("email", "test@hand-china.com");

        String signature = SignatureUtils.buildSignature(params, HttpMethod.GET, "hzero", "537509248a3da7804d12905c102d14cd1bec000797a6178a7353a4c3ac23a0b3");
        System.out.println("params is " + params.toString());
        System.out.println("signature is " + signature);
    }

    public static boolean supported(HttpMethod method) {
        return SUPPORT_METHOD.contains(method);
    }

    @Nonnull
    public static Map<String, Object> getParamMap(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(8);
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 1) {
                map.put(k, Arrays.asList(v));
            } else {
                map.put(k, v);
            }
        });
        return map;
    }

    @Nonnull
    public static Map<String, Object> getParamMap(ServerHttpRequest request) {
        Map<String, Object> map = new HashMap<>(8);
        request.getQueryParams().forEach((k, v) -> {
            if (v != null && v.size() > 1) {
                map.put(k, v);
            } else if (v != null && v.size() == 1) {
                map.put(k, v.get(0));
            }
        });
        return map;
    }

    /**
     * 根据参数等信息获取签名，供外部客户端签名使用，签名步骤如下：
     * <ul>
     *      <li>参数中加入时间戳、随机唯一值、签名ID</li>
     *      <li>对参数字典排序</li>
     *      <li>拼接请求字符串</li>
     *      <li>拼接签名原文字符串</li>
     *      <li>生成签名串</li>
     *      <li>将生成的签名串使用 Base64 进行编码</li>
     * </ul>
     *
     * @param params 请求参数
     * @param method 请求方法，支持 {@link HttpMethod#POST}、{@link HttpMethod#GET}、{@link HttpMethod#DELETE}、{@link HttpMethod#PUT}
     * @param secretId 密钥Id
     * @param secretKey 签名密钥
     * @return 签名
     */
    public static String buildSignature(Map<String, Object> params, HttpMethod method, String secretId, String secretKey) {
        if (!supported(method)) {
            throw new IllegalArgumentException("HttpMethod not supported.");
        }
        params = Optional.ofNullable(params).orElse(new HashMap<>(8));
        // 时间戳
        long timestamp = System.currentTimeMillis();
        // 随机正整数
        long nonce = RandomUtils.nextLong();

        params.remove(PARAM_TIMESTAMP);
        params.remove(PARAM_SECRET_ID);
        params.remove(PARAM_NONCE);

        // 1.加入签名所需参数
        params.put(PARAM_TIMESTAMP, timestamp);
        params.put(PARAM_SECRET_ID, secretId);
        params.put(PARAM_NONCE, nonce);

        return getSignature(params, method, secretId, secretKey);
    }

    /**
     * 根据参数等信息获取签名，供内部签名认证使用签名使用
     *
     * @param params 参数，需包含签名信息
     */
    public static String getSignature(Map<String, Object> params, HttpMethod method, String secretId, String secretKey) {
        params.remove(PARAM_SIGNATURE);

        // 2.对参数字典排序
        String[] keys = params.keySet().toArray(new String[]{});
        Arrays.sort(keys);

        // 3.拼接请求字符串
        StringBuilder paramBuilder = new StringBuilder();
        for (String key : keys) {
            paramBuilder.append("&").append(key).append("=");
            String value = String.valueOf(params.getOrDefault(key, ""));
            paramBuilder.append(value);
        }
        String paramStr = StringUtils.replaceOnce(paramBuilder.toString(), "&", "");

        // 4.拼接签名原文字符串
        String originSignature = String.format(SIGN_TEMPLATE, method.name(), params.get(PARAM_TIMESTAMP), params.get(PARAM_SECRET_ID), secretId, paramStr);

        // 5.生成签名串
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey keySpec = new SecretKeySpec(secretKey.getBytes(Charsets.UTF_8), MAC_NAME);
        Mac mac = null;
        try {
            //生成一个指定 Mac 算法 的 Mac 对象
            mac = Mac.getInstance(MAC_NAME);
            //用给定密钥初始化 Mac 对象
            mac.init(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Sign error.");
        }

        // 完成 Mac 操作
        byte[] signBytes = mac.doFinal(originSignature.getBytes(Charsets.UTF_8));

        // 6. 将生成的签名串使用 Base64 进行编码
        return Base64.getEncoder().encodeToString(signBytes);
    }

}
