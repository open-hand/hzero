package org.hzero.feign.init;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * api注册
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/20 17:41
 */
@Component
@Order(1)
public class MappingInit implements CommandLineRunner {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void run(String... args) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        map.forEach((k, v) -> {
            Set<RequestMethod> set = k.getMethodsCondition().getMethods();
            if (set.size() == 1) {
                String type = String.valueOf(set.iterator().next());
                // 一个方法有多个路径
                Set<String> urlSet = k.getPatternsCondition().getPatterns();
                urlSet.forEach(url -> MappingRegister.addMethods(type.toUpperCase(), url, v));
            }
        });
    }
}
