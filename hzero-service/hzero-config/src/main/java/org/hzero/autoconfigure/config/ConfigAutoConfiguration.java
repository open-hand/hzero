package org.hzero.autoconfigure.config;

import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

import org.hzero.core.jackson.annotation.EnableObjectMapper;


@ComponentScan(value = {
    "org.hzero.config.app",
    "org.hzero.config.config",
    "org.hzero.config.domain",
    "org.hzero.config.infra",
})
@EnableFeignClients({"org.hzero.config", "io.choerodon"})
@EnableOAuth2Client
@EnableChoerodonResourceServer
@EnableObjectMapper
@EnableConfigServer
@Configuration
public class ConfigAutoConfiguration {

}
