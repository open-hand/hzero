package org.hzero.starter.call.support;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.UUIDUtils;
import org.hzero.starter.call.entity.CallConfig;
import org.hzero.starter.call.entity.ExtParam;
import org.hzero.starter.call.entity.JdResponse;
import org.hzero.starter.call.exception.SendMessageException;
import org.hzero.starter.call.util.Base64Utils;
import org.hzero.starter.call.util.Md5Utils;
import org.hzero.starter.call.util.RSAUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 京东语音服务
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/27 13:55
 */
public class JdCallSupporter {
    /**
     * 语音通知服务名称
     */
    private static final String SERVICE_NAME = "callTextVerify";
    /**
     * 服务API
     */
    private static final String URI = "https://commcso.jd.com/open/api/invoke.do";
    /**
     * 版本
     */
    private static final String VERSION = "1.0";

    private JdCallSupporter() {
    }

    public static void sendCall(CallConfig callConfig, List<String> receiverAddress, String content) {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(URI);
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 管理中心-账户总览-开发参数-AppID、AppSecret(publicKey)、AccountCode、App_UserID
        String appId = callConfig.getAccessKey();
        String publicKey = callConfig.getAccessSecret();
        String sign;
        String bizParam;

        HashMap<String, Object> header = new HashMap<>(16);
        header.put("serviceName", SERVICE_NAME);
        header.put("version", VERSION);
        header.put("clientMessageId", SERVICE_NAME + UUIDUtils.generateUUID());
        header.put("timestamp", System.currentTimeMillis());
        ExtParam extParam = JSON.parseObject(callConfig.getExtParam(), ExtParam.class);
        HashMap<String, Object> body = new HashMap<>(16);
        body.put("accountCode", extParam.getAccountCode());
        body.put("userId", extParam.getUserId());
        if (StringUtils.isNotBlank(extParam.getShowNum())) {
            body.put("calledDisplayNumber", extParam.getShowNum());
        }
        body.put("playTimes", extParam.getPlayTimes());
        // 内容去除html标签
        body.put("content", content.replaceAll("<[.[^<]]*>", ""));

        // 循环处理接收人
        for (String address : receiverAddress) {
            body.put("calledNumber", address);

            HashMap<String, Object> map = new HashMap<>(16);
            map.put("header", header);
            map.put("body", body);

            //加密
            String jsonParam = JSON.toJSONString(map);
            try {
                byte[] encodeDate = RSAUtils.encryptByPublicKey(jsonParam.getBytes(StandardCharsets.UTF_8), publicKey);
                bizParam = Base64Utils.encode(encodeDate);

                //签名校验
                sign = Md5Utils.md5(jsonParam);

                NameValuePair[] data = {
                        new NameValuePair("appId", appId),
                        new NameValuePair("sign", sign),
                        new NameValuePair("bizParam", bizParam)
                };
                post.setRequestBody(data);
                client.executeMethod(post);
                String responseString = post.getResponseBodyAsString();
                JSONObject jsonObject = JSON.parseObject(responseString);

                String responseBody = jsonObject.getString("body");
                if (responseBody != null) {
                    byte[] bytes = RSAUtils.decryptByPublicKey(Base64Utils.decode(responseBody), publicKey);
                    String msg = new String(bytes);
                    JdResponse response = JSON.parseObject(msg, JdResponse.class);
                    if (StringUtils.isBlank(response.getTimestamp())) {
                        throw new SendMessageException(String.format("jingdong call failed! message:  [%s]", msg));
                    }
                } else {
                    throw new SendMessageException("jingdong response is null!");
                }
            } catch (SendMessageException sme) {
                throw sme;
            } catch (Exception e) {
                throw new SendMessageException("jingdong call failed! message : " + e.getMessage());
            }
        }
    }
}
