package org.hzero.scheduler.infra.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.undertow.util.StatusCodes;

/**
 * rpc工具类
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 15:59
 */
public class RpcClient {

    private RpcClient() {
    }

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).build();

    public static void runJob(String url, String jwtToken, String jsonParams) throws IOException {
        Call call = getCall(url, jwtToken, jsonParams);
        try (Response response = call.execute()) {
            if (response.code() == StatusCodes.NOT_FOUND) {
                throw new CommonException("Client communication failed.");
            }
            if (!response.isSuccessful()) {
                throw new CommonException(response.message());
            }
        }
    }

    public static void runJobWithRestTemplate(String url, String jwtToken, JobDataDTO jobDataDTO) {
        RestTemplate restTemplate = ApplicationContextHelper.getContext().getBean(RestTemplate.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Connection", "keep-alive");
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON_UTF8);
        headers.add("jwt_token", jwtToken);
        HttpEntity<JobDataDTO> entity = new HttpEntity<>(jobDataDTO, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
        if (response.getStatusCodeValue() == StatusCodes.NOT_FOUND) {
            throw new CommonException("Client communication failed.");
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CommonException(response.toString());
        }
    }

    public static String stopJob(String url, String jwtToken, String jsonParams) throws IOException {
        Call call = getCall(url, jwtToken, jsonParams);
        try (Response response = call.execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                return "exception";
            }
        }
    }

    private static Call getCall(String url, String jwtToken, String jsonParams) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request;
        if (StringUtils.isNotBlank(jwtToken)) {
            request = new Request.Builder()
                    .addHeader("jwt_token", jwtToken)
                    .addHeader("Connection", "keep-alive")
                    .url(url)
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .addHeader("Connection", "keep-alive")
                    .url(url)
                    .post(requestBody)
                    .build();
        }
        return HTTP_CLIENT.newCall(request);
    }
}
