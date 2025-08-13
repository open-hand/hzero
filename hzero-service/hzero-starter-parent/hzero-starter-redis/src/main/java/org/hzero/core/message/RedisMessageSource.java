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
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;

/**
 * 基于Redis的消息源
 *
 * @author bojiangzhou 2019/01/11
 */
public class RedisMessageSource extends AbstractMessageSource implements IMessageSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageSource.class);
    private static final String MESSAGE_KEY = "hpfm:message:";

    private RedisHelper redisHelper;
    private int redisDb = 1;

    public RedisMessageSource() {
        ApplicationContextHelper.asyncInstanceSetter(RedisHelper.class, this, "setRedisHelper");
        ApplicationContextHelper.asyncInstanceSetter(Environment.class, this, "setEnvironment");
    }

    public RedisMessageSource(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
        ApplicationContextHelper.asyncInstanceSetter(Environment.class, this, "setEnvironment");
    }

    private void setRedisHelper(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    private void setEnvironment(Environment environment) {
        this.redisDb = Integer.parseInt(environment.getProperty("hzero.service.platform.redis-db", "1"));
    }

    @Override
    public void setParent(MessageSource messageSource) {
        this.setParentMessageSource(messageSource);
        this.setAlwaysUseMessageFormat(true);
    }


    @Override
    public Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, Locale locale) {
        return resolveMessage(parentMessageSource, code, args, null, locale);
    }

    @Override
    public Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, String defaultMessage, Locale locale) {
        Message message = getMessageObject(code, locale);
        String desc = null;
        if (message != null) {
            desc = message.desc();
        } else {
            try {
                desc = getParentMessageSource().getMessage(code, null, locale);
            } catch (NoSuchMessageException e) {
                LOGGER.warn("resolveMessage not found message for code={}", code);
            }
        }
        if (StringUtils.isBlank(desc) && StringUtils.isNotBlank(defaultMessage)) {
            desc = defaultMessage;
        }
        if (StringUtils.isNotBlank(desc) && ArrayUtils.isNotEmpty(args)) {
            desc = createMessageFormat(desc, locale).format(args);
        }
        if (StringUtils.isBlank(desc)) {
            desc = code;
        }

        String finalDesc = desc;
        message = Optional.ofNullable(message).map(m -> m.setDesc(finalDesc)).orElse(new Message(code, desc));
        LOGGER.debug("resolve message: code={}, message={}, language={}", code, message, locale);
        return message;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        Message message = getMessageObject(code, locale);

        String msg = null;
        if (message != null) {
            msg = message.desc();
        } else {
            try {
                msg = getParentMessageSource().getMessage(code, null, locale);
            } catch (NoSuchMessageException e) {
                LOGGER.warn("resolveCode not found message for code={}", code);
            }
        }
        if (StringUtils.isNotBlank(msg)) {
            return createMessageFormat(msg, locale);
        }
        return null;
    }

    private Message getMessageObject(String code, Locale locale) {
        if (StringUtils.isBlank(code)) {
            return new Message("null", "null");
        }
        if (redisHelper == null) {
            LOGGER.debug("redisHelper is null, use local message");
            return null;
        }
        // 优先取用户上下文
        // FIX: java.util.Locale.convertOldISOCodes  Locale会将印尼语言（ID）转为印度语言（IN），页面选择语言为印尼，但这里实际获取的语言是印度
        String language = LanguageHelper.language();
        if (StringUtils.isBlank(language) && locale != null) {
            language = locale.toString();
        }
        if (StringUtils.isBlank(language)) {
            language = LanguageHelper.getDefaultLanguage();
        }
        String redisKey = MESSAGE_KEY + language;
        String obj = SafeRedisHelper.execute(redisDb, () -> redisHelper.hshGet(redisKey, code));

        if (StringUtils.isNotEmpty(obj)) {
            return redisHelper.fromJson(obj, Message.class);
        }
        return null;
    }


}
