package org.hzero.core.endpoint.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.ExceptionResponse;
import org.hzero.core.endpoint.HttpRequest;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

/**
 * 处理以String类型返回的接口通信类
 *
 * @author XCXCXCXCX
 * @date 2020/4/22 12:45 下午
 */
public class StringHttpTransporter extends BaseHttpTransporter<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String transport(HttpRequest<String> request) {
        String body = super.transport(request);
        if (body == null) {
            return null;
        }
        // handle choerodon ExceptionResponse
        try {
            ExceptionResponse response = objectMapper.readValue(body, ExceptionResponse.class);
            if (response != null && response.getFailed()) {
                throw new RestClientException("http transport failed, ExceptionResponse: " + response.getMessage());
            }
        } catch (IOException e) {
            if (e instanceof JsonParseException || e instanceof JsonMappingException) {
                return body;
            }
            throw new RestClientException("response body parse failed", e);
        }
        return body;
    }

}
