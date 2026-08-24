package org.hzero.admin.infra.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Configuration
public class FeignLogger {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
