package org.hzero.core.analysis;

import org.hzero.core.exception.ServiceStartException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.util.StringUtils;

/**
 * 自定义服务启动失败异常的分析器
 *
 * @author XCXCXCXCX
 * @date 2020/6/11 12:17 下午
 */
public class StartedFailureAnalyzer extends AbstractFailureAnalyzer<ServiceStartException> {

    private static final String DEFAULT_ACTION = "启动服务失败，请检查配置！";

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, ServiceStartException cause) {
        String action = cause.getFailureCause();
        if (StringUtils.isEmpty(action)) {
            action = DEFAULT_ACTION;
        }
        return new FailureAnalysis(cause.getMessage(), action, cause);
    }
}
