package org.hzero.boot.platform.lov.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.lov.handler.LovValueHandle;

/**
 * 用以处理值集值映射的切面
 *
 * @author gaokuo.dai@hand-china.com 2018年7月3日下午8:10:37
 */
@Aspect
public class LovValueAspect {
	
	private LovValueHandle lovValueHandle;
	
	public LovValueAspect(LovValueHandle lovValueHandle) {
		this.lovValueHandle = lovValueHandle;
	}
	
	@AfterReturning(value = "@annotation(org.hzero.boot.platform.lov.annotation.ProcessLovValue)",returning="result") 
	public Object afterReturning(JoinPoint proceedingJoinPoint, Object result) throws Throwable {
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = signature.getMethod();
		ProcessLovValue processLovValue = method.getAnnotation(ProcessLovValue.class);
		result = this.lovValueHandle.process(processLovValue.targetField(), result);
		return result;
	}
	
}
