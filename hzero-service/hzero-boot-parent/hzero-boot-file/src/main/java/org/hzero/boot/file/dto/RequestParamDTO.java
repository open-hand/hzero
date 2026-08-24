package org.hzero.boot.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 请求参数
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:46:36
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestParamDTO {

    private String accessToken;
    private String docServerUrl;
    private String bodyJson;

    public String getAccessToken() {
        return accessToken;
    }

    public RequestParamDTO setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getDocServerUrl() {
        return docServerUrl;
    }

    public RequestParamDTO setDocServerUrl(String docServerUrl) {
        this.docServerUrl = docServerUrl;
        return this;
    }

    public String getBodyJson() {
        return bodyJson;
    }

    public RequestParamDTO setBodyJson(String bodyJson) {
        this.bodyJson = bodyJson;
        return this;
    }

    @Override
    public String toString() {
        return "RequestParam{" +
                "accessToken='" + accessToken + '\'' +
                ", docServerUrl='" + docServerUrl + '\'' +
                ", bodyJson='" + bodyJson + '\'' +
                '}';
    }
}
