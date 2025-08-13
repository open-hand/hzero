package org.hzero.boot.admin.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 7:23 下午
 */
public class RegistrationHttpMessageConverter extends AbstractHttpMessageConverter<Response> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz == Response.class;
    }

    @Override
    protected Response readInternal(Class<? extends Response> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream is = inputMessage.getBody();
        byte[] bytes = IOUtils.toByteArray(is);
        return objectMapper.readValue(bytes, clazz);
    }

    @Override
    protected void writeInternal(Response response, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream os = outputMessage.getBody();
        IOUtils.write(objectMapper.writeValueAsBytes(response), os);
    }
}
