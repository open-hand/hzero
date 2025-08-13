package org.hzero.boot.scheduler.infra.util;

import io.choerodon.core.exception.CommonException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * 获取完整异常信息
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/25 15:44
 */
public class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static String getMessage(Exception e) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintStream pout = new PrintStream(out, false, StandardCharsets.UTF_8.displayName())) {
            e.printStackTrace(pout);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new CommonException(ex);
        }
    }
}
