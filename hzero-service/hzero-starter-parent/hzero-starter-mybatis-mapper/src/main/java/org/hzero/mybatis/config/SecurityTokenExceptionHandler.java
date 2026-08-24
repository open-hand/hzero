package org.hzero.mybatis.config;

import java.util.Locale;

import io.choerodon.core.exception.ExceptionResponse;
import org.apache.commons.codec.Charsets;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.mybatis.exception.SecurityTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2018/10/23 星期二 21:19
 */
@Order(0)
@ControllerAdvice
public class SecurityTokenExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTokenExceptionHandler.class);
    private static final String EXCEPTION_CODE = "error.invalid.security.token";
    private static MessageSource messageSource;

    static {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding(Charsets.UTF_8.displayName());
        resourceBundleMessageSource.setBasename("org.hzero.mybatis.messages");
        messageSource = resourceBundleMessageSource;
    }


    private static String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, code, locale());
    }

    private static Locale locale() {
        return LanguageHelper.locale();
    }

    /**
     * 拦截 {@link SecurityTokenException} 异常信息，直接返回封装的异常消息
     *
     * @param exception MessageException
     * @return ExceptionResponse
     */
    @ExceptionHandler(SecurityTokenException.class)
    public ResponseEntity<ExceptionResponse> process(SecurityTokenException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(new ExceptionResponse(EXCEPTION_CODE), HttpStatus.OK);
    }
}
