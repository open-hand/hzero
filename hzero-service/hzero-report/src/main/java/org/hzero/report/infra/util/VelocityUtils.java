package org.hzero.report.infra.util;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.introspection.SecureUberspector;
import org.hzero.report.infra.constant.HrptConstants;

import io.choerodon.core.exception.CommonException;

/**
 * 验证工具类
 *
 * @author xianzhi.chen@hand-china.com	2018年10月17日下午8:56:37
 */
public class VelocityUtils {
    private static final VelocityEngine VELOCITY_ENGINE = new VelocityEngine();

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
    public static String parse(String template, Map<String, Object> parameters) {
        return parse(template, parameters, HrptConstants.REPORT);
    }

    /**
     * 替换的模板中的参数
     *
     * @param template 模板
     * @return 替换后的文本
     */
    public static String parse(String template) {
        return parse(template, HrptConstants.REPORT);
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
    public static String parse(String template, Map<String, Object> parameters, String logTag) {
        try (StringWriter writer = new StringWriter()) {
            VelocityContext context = new VelocityContext();
            for (Entry<String, Object> kvset : parameters.entrySet()) {
                context.put(kvset.getKey(), kvset.getValue());
            }
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
