package org.hzero.autoconfigure.report;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 
 * 报表自动化配置注解
 * 
 * @author xianzhi.chen@hand-china.com 2019年1月25日下午3:57:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ReportAutoConfiguration.class)
public @interface EnableHZeroReport {

}
