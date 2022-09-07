package org.hzero.report.infra.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.ExpressionRuntimeException;

/**
 * 报表计算列表达式求值工具类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:22:34
 */
public class AviatorExprUtils {

    private AviatorExprUtils() {}

    private static final Logger logger = LoggerFactory.getLogger(AviatorExprUtils.class);

    public static Object execute(String expression, Map<String, Object> context) {
        Object value = null;

        try {
            Expression compiledExp = AviatorEvaluator.compile(expression, true);
            value = compiledExp.execute(context);
        } catch (ExpressionRuntimeException ex) {
            logger.error(ex.getMessage());
        }

        return value;
    }
}
