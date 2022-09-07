package org.hzero.export.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import io.choerodon.core.exception.ExceptionResponse;
import org.apache.commons.codec.Charsets;
import org.hzero.export.vo.ExportColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ResponseWriter 注意，调用 ResponseWriter 后，不应该再执行其它方法，response 已经关闭.
 *
 * @author bojiangzhou 2018/07/26
 */
public class ResponseWriter {

    private ResponseWriter() {}

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(ResponseWriter.class);

    public static void write(HttpServletResponse response, ExportColumn exportColumn) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(Charsets.UTF_8.displayName());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(exportColumn));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void write(HttpServletResponse response, ExceptionResponse exceptionResponse) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(Charsets.UTF_8.displayName());
        try {
            Map<String, Object> json = new HashMap<>(2);
            json.put("failed", "true");
            json.put("code", exceptionResponse.getCode());
            json.put("message", exceptionResponse.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(json));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void writeAsyncRequestSuccess(HttpServletResponse response, String uuid) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(Charsets.UTF_8.displayName());
        try {
            Map<String, Object> json = new HashMap<>(2);
            json.put("uuid", uuid);
            response.getWriter().write(objectMapper.writeValueAsString(json));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
