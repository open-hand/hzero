package org.hzero.core.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;
import org.springframework.util.StringUtils;

/**
 * 用于Slf4j日志打印的失败分析报告器
 *
 * @author XCXCXCXCX
 * @date 2020/6/11 1:16 下午
 */
public class Slf4jFailureAnalysisReporter implements FailureAnalysisReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jFailureAnalysisReporter.class);

    @Override
    public void report(FailureAnalysis failureAnalysis) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Application failed to start due to an exception",
                    failureAnalysis.getCause());
        }
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(buildMessage(failureAnalysis));
        }
    }

    private String buildMessage(FailureAnalysis failureAnalysis) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%n%n"));
        builder.append(String.format("***************************%n"));
        builder.append(String.format("APPLICATION FAILED TO START%n"));
        builder.append(String.format("***************************%n%n"));
        builder.append(String.format("Description:%n%n"));
        builder.append(String.format("%s%n", failureAnalysis.getDescription()));
        if (StringUtils.hasText(failureAnalysis.getAction())) {
            builder.append(String.format("%nAction:%n%n"));
            builder.append(String.format("%s%n", failureAnalysis.getAction()));
        }
        return builder.toString();
    }

}
