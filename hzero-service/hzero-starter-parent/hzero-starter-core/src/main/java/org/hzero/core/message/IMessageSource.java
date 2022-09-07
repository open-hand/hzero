package org.hzero.core.message;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 获取描述的默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/22 16:04
 */
public interface IMessageSource {

    Logger LOGGER = LoggerFactory.getLogger(IMessageSource.class);

    default void setParent(MessageSource messageSource){
    }

    default Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, Locale locale) {
        return resolveMessage(parentMessageSource, code, args, null, locale);
    }

    default Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, String defaultMessage, Locale locale) {
        Message message = new Message();
        String desc = null;
        try {
            desc = parentMessageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            LOGGER.warn("resolveMessage not found message for code={}", code);
        }
        if (StringUtils.isBlank(desc) && StringUtils.isNotBlank(defaultMessage)) {
            desc = defaultMessage;
        }
        if (StringUtils.isNotBlank(desc) && ArrayUtils.isNotEmpty(args)) {
            desc = new MessageFormat(desc, locale).format(args);
        }
        if (StringUtils.isBlank(desc)) {
            desc = code;
        }

        String finalDesc = desc;
        message = Optional.of(message).map(m -> m.setDesc(finalDesc)).orElse(new Message(code, desc));
        LOGGER.debug("resolve message: code={}, message={}, language={}", code, message, locale);
        return message;
    }
}
