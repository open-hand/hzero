package org.hzero.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.core.jackson.JacksonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 响应处理工具类
 * </p>
 *
 * @author qingsheng.chen 2019/1/28 星期一 9:57
 */
@SuppressWarnings("Duplicates")
public class ResponseUtils {
    private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
    private static volatile ObjectMapper objectMapper;
    private static final FastDateFormat DEFAULT_DATE_FORMAT = FastDateFormat.getInstance(JacksonConstant.DEFAULT_DATE_FORMAT);
    private static final List<String> SIMPLE_CLASS = Arrays.asList(
            Short.class.getName(), Integer.class.getName(), Long.class.getName(),
            Float.class.getName(), Double.class.getName(),
            Boolean.class.getName(),
            String.class.getName(),
            Date.class.getName(), java.sql.Date.class.getName(), LocalDate.class.getName(),
            BigDecimal.class.getName());

    public static final Not2xxSuccessful DEFAULT_NOT_2XX_SUCCESSFUL = (httpStatus, response) -> {
        throw new CommonException("error.inner.request", httpStatus.value(), response);
    };
    public static final FailedResponse DEFAULT_FAILED_RESPONSE = (exceptionResponse) -> {
        throw new CommonException(exceptionResponse.getCode());
    };

    private ResponseUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }

        synchronized (ResponseUtils.class) {
            objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
        }

        return objectMapper;
    }

    public static synchronized void setObjectMapper(ObjectMapper objectMapper) {
        ResponseUtils.objectMapper = objectMapper;
    }

    public static <T> T getResponse(ResponseEntity<String> response, Class<T> responseType) {
        return getResponse(response, responseType, DEFAULT_NOT_2XX_SUCCESSFUL, DEFAULT_FAILED_RESPONSE);
    }

    public static <T> T getResponse(ResponseEntity<String> response, TypeReference<T> responseType) {
        return getResponse(response, responseType, DEFAULT_NOT_2XX_SUCCESSFUL, DEFAULT_FAILED_RESPONSE);
    }

    /**
     * 请求是否失败
     *
     * @param response 响应内容
     * @return 是否失败
     */
    public static boolean isFailed(ResponseEntity<String> response) {
        if (failed(response, null)) {
            return true;
        }
        if (noContent(response)) {
            return false;
        }
        String content = response.getBody();
        try {
            JsonNode jsonNode = getObjectMapper().readTree(content);
            return !jsonNode.isArray() && jsonNode.has(ExceptionResponse.FILED_FAILED) && jsonNode.get(ExceptionResponse.FILED_FAILED).asBoolean();
        } catch (IOException e) {
            logger.info("Json deserialization failed, {}", StringUtils.defaultString(e.getMessage(), "(No error message)"));
            return false;
        }
    }

    /**
     * 获取反序列化后响应内容
     *
     * @param response         响应内容
     * @param responseType     反序列化类型，支持简单类型 {@link ResponseUtils#SIMPLE_CLASS}
     * @param not2xxSuccessful 非2xx错误处理
     * @param failedResponse   200，但是failed=true处理
     * @param <T>              反序列化类型泛型
     * @return 反序列化后的响应内容
     */
    public static <T> T getResponse(ResponseEntity<String> response, Class<T> responseType, Not2xxSuccessful not2xxSuccessful, FailedResponse failedResponse) {
        if (failedOrNoContent(response, not2xxSuccessful)) {
            return null;
        }
        String content = response.getBody();
        if (content == null) {
            return null;
        }
        try {
            JsonNode jsonNode = getObjectMapper().readTree(content);
            if (!jsonNode.isArray() && jsonNode.has(ExceptionResponse.FILED_FAILED) && jsonNode.get(ExceptionResponse.FILED_FAILED).asBoolean()) {
                failedResponse.failedResponse(getObjectMapper().readValue(jsonNode.traverse(), ExceptionResponse.class));
                return null;
            } else {
                return isSimpleClass(responseType) ? getSimpleValue(responseType.getTypeName(), content) : getObjectMapper().readValue(jsonNode.traverse(), responseType);
            }
        } catch (IOException e) {
            if (isSimpleClass(responseType)) {
                return getSimpleValue(responseType.getTypeName(), content);
            }
            throw new CommonException(e);
        }
    }

    private static boolean isSimpleClass(Class<?> responseType) {
        return SIMPLE_CLASS.contains(responseType.getTypeName());
    }

    private static boolean isSimpleClass(String responseType) {
        return SIMPLE_CLASS.contains(responseType);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getSimpleValue(String responseType, String value) {
        if (Short.class.getName().equals(responseType)) {
            return (T) Short.valueOf(value);
        } else if (Integer.class.getName().equals(responseType)) {
            return (T) Integer.valueOf(value);
        } else if (Long.class.getName().equals(responseType)) {
            return (T) Long.valueOf(value);
        } else if (Float.class.getName().equals(responseType)) {
            return (T) new Float(value);
        } else if (Double.class.getName().equals(responseType)) {
            return (T) new Double(value);
        } else if (Boolean.class.getName().equals(responseType)) {
            return (T) Boolean.valueOf(value);
        } else if (String.class.getName().equals(responseType)) {
            return (T) value;
        } else if (Date.class.getName().equals(responseType)) {
            try {
                return (T) DEFAULT_DATE_FORMAT.parse(value);
            } catch (ParseException e) {
                throw new CommonException(e);
            }
        } else if (java.sql.Date.class.getName().equals(responseType)) {
            return (T) java.sql.Date.valueOf(value);
        } else if (BigDecimal.class.getName().equals(responseType)) {
            return (T) new BigDecimal(value);
        } else if (LocalDate.class.getName().equals(responseType)) {
            return (T) LocalDate.parse(value);
        } else {
            throw new IllegalArgumentException("Invalided response type : " + responseType);
        }
    }

    private static boolean failed(ResponseEntity<String> response, Not2xxSuccessful not2xxSuccessful) {
        Assert.notNull(response, "Response must be not null.");
        if (!response.getStatusCode().is2xxSuccessful()) {
            if (not2xxSuccessful != null) {
                not2xxSuccessful.not2xxSuccessful(response.getStatusCode(), response.getBody());
            }
            return true;
        }
        return false;
    }

    private static boolean noContent(ResponseEntity<String> response) {
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    private static boolean failedOrNoContent(ResponseEntity<String> response, Not2xxSuccessful not2xxSuccessful) {
        if (failed(response, not2xxSuccessful)) {
            return true;
        }
        return noContent(response);
    }

    /**
     * 获取反序列化后响应内容
     *
     * @param response         响应内容
     * @param responseType     反序列化类型，<strong>不支持简单类型</strong>
     * @param not2xxSuccessful 非2xx错误处理
     * @param failedResponse   200，但是failed=true处理
     * @param <T>              反序列化类型泛型
     * @return 反序列化后的响应内容
     */
    public static <T> T getResponse(ResponseEntity<String> response, TypeReference<T> responseType, Not2xxSuccessful not2xxSuccessful, FailedResponse failedResponse) {
        if (failedOrNoContent(response, not2xxSuccessful)) {
            return null;
        }
        String content = response.getBody();
        if (content == null) {
            return null;
        }
        try {
            JsonNode jsonNode = getObjectMapper().readTree(content);
            if (!jsonNode.isArray() && jsonNode.has(ExceptionResponse.FILED_FAILED) && jsonNode.get(ExceptionResponse.FILED_FAILED).asBoolean()) {
                failedResponse.failedResponse(getObjectMapper().readValue(jsonNode.traverse(), ExceptionResponse.class));
                return null;
            } else {
                return isSimpleClass(responseType.getType().getTypeName()) ? getSimpleValue(responseType.getType().getTypeName(), content) : getObjectMapper().readValue(jsonNode.traverse(), responseType);
            }
        } catch (IOException e) {
            if (isSimpleClass(responseType.getType().getTypeName())) {
                return getSimpleValue(responseType.getType().getTypeName(), content);
            }
            throw new CommonException(e);
        }
    }

    @FunctionalInterface
    public interface Not2xxSuccessful {
        /**
         * 非2xx结果
         *
         * @param httpStatus 请求状态嘛
         * @param response   响应内容
         */
        void not2xxSuccessful(HttpStatus httpStatus, String response);
    }

    @FunctionalInterface
    public interface FailedResponse {
        /**
         * 2xx但是失败了
         *
         * @param exceptionResponse 失败回调
         */
        void failedResponse(ExceptionResponse exceptionResponse);
    }
}
