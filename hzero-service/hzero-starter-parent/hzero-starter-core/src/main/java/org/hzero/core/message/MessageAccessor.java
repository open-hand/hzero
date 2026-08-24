package org.hzero.core.message;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import org.hzero.core.helper.LanguageHelper;

/**
 * 首先从Redis缓存中获取消息，如果没有则从 classpath 下的 message.properties 里获取，使用者可提供默认消息。
 * 该消息存取器里的静态方法返回的 Message 对象不会为 null。
 *
 * @author bojiangzhou 2018/09/15
 * @see MessageSourceAccessor
 */
public class MessageAccessor {

    private static final IMessageSource REDIS_MESSAGE_SOURCE;
    private static final ReloadableResourceBundleMessageSource PARENT_MESSAGE_SOURCE;

    private static final List<String> basenames = ImmutableList.of(
            "classpath:messages/messages",
            "classpath:messages/messages_core",
            "classpath:messages/messages_redis",
            "classpath:messages/messages_export",
            "classpath:messages/messages_ext"
    );

    private MessageAccessor() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    static {
        PARENT_MESSAGE_SOURCE = new ReloadableResourceBundleMessageSource();
        PARENT_MESSAGE_SOURCE.setBasenames(getBasenames());
        PARENT_MESSAGE_SOURCE.setDefaultEncoding("UTF-8");

        Class clazz;
        IMessageSource redisMessageSource;
        try {
            clazz = Class.forName("org.hzero.core.message.RedisMessageSource");
            // 依赖了starter
            redisMessageSource = (IMessageSource) clazz.newInstance();
        } catch (Exception e){
            redisMessageSource = new IMessageSource() {
                @Override
                public void setParent(MessageSource messageSource) {
                }
            };
        }
        redisMessageSource.setParent(PARENT_MESSAGE_SOURCE);
        REDIS_MESSAGE_SOURCE = redisMessageSource;
    }

    public static String[] getBasenames() {
        return ArrayUtils.toStringArray(basenames.toArray());
    }

    /**
     * 添加资源文件位置
     *
     * @param names 如 <code>classpath:messages/messages_core</code>
     */
    public static void addBasenames(String... names) {
        PARENT_MESSAGE_SOURCE.addBasenames(names);
    }

    /**
     * 覆盖默认资源文件位置
     *
     * @param names 如 <code>classpath:messages/messages_core</code>
     */
    public static void setBasenames(String... names) {
        PARENT_MESSAGE_SOURCE.setBasenames(names);
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, String defaultMessage) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, null, defaultMessage, LanguageHelper.locale());
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, String defaultMessage, Locale locale) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, null, defaultMessage, locale);
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args, String defaultMessage) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, defaultMessage, LanguageHelper.locale());
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, defaultMessage, locale);
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, null, LanguageHelper.locale());
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Locale locale) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, null, locale);
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, LanguageHelper.locale());
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args, Locale locale) {
        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, locale);
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code) {
        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, null, LanguageHelper.locale()));
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code, Locale locale) {
        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, null, locale));
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code, Object[] args) {
        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, args, LanguageHelper.locale()));
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code, Object[] args, Locale locale) {
        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, args, locale));
    }

}
