package org.hzero.boot.message.util;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.introspection.SecureUberspector;

import io.choerodon.core.exception.CommonException;

/**
 * 验证工具类
 *
 * @author xianzhi.chen@hand-china.com	2018年10月17日下午8:56:37
 */
public class VelocityUtils {
    private static final VelocityEngine VELOCITY_ENGINE = new VelocityEngine();
    private static final String APP_CODE = "hmsg";

    static {
        VELOCITY_ENGINE.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, SecureUberspector.class.getName());
        VELOCITY_ENGINE.init();
    }

    private VelocityUtils() {
    }

    /**
     * 替换的模板中的参数
     *
     * @param template   模板
     * @param parameters 参数
     * @return 替换后的文本
     */
    public static String parse(String template, Map<String, String> parameters) {
        return parse(template, parameters, APP_CODE);
    }

    /**
     * 替换的模板中的参数
     *
     * @param template   模板
     * @param parameters 参数
     * @return 替换后的文本
     */
    public static String parseObject(String template, Map<String, Object> parameters) {
        return parseObject(template, parameters, APP_CODE);
    }

    /**
     * 替换的模板中的参数
     *
     * @param template 模板
     * @return 替换后的文本
     */
    public static String parse(String template) {
        return parse(template, APP_CODE);
    }

    /**
     * 替换的模板中的参数
     *
     * @param template 模板
     * @param logTag   tag
     * @return 替换后的文本
     */
    public static String parse(String template, String logTag) {
        return parse(template, new HashMap<>(0), logTag);
    }

    /**
     * 替换的模板中的参数
     *
     * @param template   模板
     * @param parameters 参数
     * @param logTag     tag
     * @return 替换后的文本
     */
    public static String parse(String template, Map<String, String> parameters, String logTag) {
        VelocityContext context = new VelocityContext();
        parameters.forEach(context::put);
        return parseContext(template, context, logTag);
    }

    /**
     * 替换的模板中的参数
     *
     * @param template   模板
     * @param parameters 参数
     * @param logTag     tag
     * @return 替换后的文本
     */
    public static String parseObject(String template, Map<String, Object> parameters, String logTag) {
        VelocityContext context = new VelocityContext();
        parameters.forEach(context::put);
        return parseContext(template, context, logTag);
    }

    private static String parseContext(String template, VelocityContext context, String logTag) {
        try (StringWriter writer = new StringWriter()) {
            context.put("Calendar", Calendar.getInstance());
            context.put("DateUtils", DateUtils.class);
            context.put("StringUtils", StringUtils.class);
            VELOCITY_ENGINE.evaluate(context, writer, logTag, template);
            return writer.toString();
        } catch (Exception ex) {
            throw new CommonException(ex);
        }
    }
}
