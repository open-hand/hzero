package org.hzero.oauth.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

@Aspect
@Component
public class CommonControllerAspect {

    private static final Pattern SAFE_PATTERN = Pattern.compile("^[\\w\\-_.]+$");

    @Pointcut("execution(* org.hzero.oauth.api.controller.v1.OauthController.*(..))")
    public void oauthController() {
    }

    @Before("oauthController()")
    public void verify(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for(Object arg :args){
            if(!(arg instanceof String)){
                continue;
            }
            Matcher matcher = SAFE_PATTERN.matcher(((String) arg));
            if(!matcher.matches()){
                throw new CommonException("Access path is incorrect, please check!");
            }
        }

    }

}
