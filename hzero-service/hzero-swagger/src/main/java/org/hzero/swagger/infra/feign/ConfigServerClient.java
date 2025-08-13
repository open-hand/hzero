package org.hzero.swagger.infra.feign;

import org.hzero.swagger.config.FeignLogConfiguration;
import org.hzero.swagger.infra.feign.fallback.ConfigServerClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 向config-server服务发送feign请求，告知修改的配置
 *
 * @author wuguokai
 */
@FeignClient(value = "config-server",
    fallback = ConfigServerClientFallback.class,
    configuration = FeignLogConfiguration.class
)
public interface ConfigServerClient {

    @RequestMapping(value = "/monitor/refresh-route", method = RequestMethod.POST)
    ResponseEntity<String> refreshRoute();

}


