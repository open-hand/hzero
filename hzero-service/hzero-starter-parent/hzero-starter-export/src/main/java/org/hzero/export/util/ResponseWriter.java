package org.hzero.export.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.ExceptionResponse;

import org.hzero.export.vo.ExportColumn;

/**
 * ResponseWriter 注意，调用 ResponseWriter 后，不应该再执行其它方法，response 已经关闭.
 *
 * @author bojiangzhou 2018/07/26
 */
public class ResponseWriter {

    private ResponseWriter() {
    }

    private static final ObjectMapper OBJECT_MAPPER = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseWriter.class);

    public static void write(HttpServletResponse response, ExportColumn exportColumn) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        try {
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(exportColumn));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void write(HttpServletResponse response, ExceptionResponse exceptionResponse) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        try {
            Map<String, Object> json = new HashMap<>(8);
            json.put("failed", true);
            json.put("code", exceptionResponse.getCode());
            json.put("message", exceptionResponse.getMessage());
            json.put("type", "error");
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(json));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void writeAsyncRequestSuccess(HttpServletResponse response, String uuid) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        try {
            Map<String, Object> json = new HashMap<>(2);
            json.put("uuid", uuid);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(json));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
