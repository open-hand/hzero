package org.hzero.core;

import io.undertow.Undertow;
import org.hzero.core.undertow.CustomUndertowWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * @author qingsheng.chen@hand-china.com
 */
@ConditionalOnClass(Undertow.class)
@ConditionalOnProperty(prefix = "server.undertow", name = "allow-unescaped-characters-in-url", havingValue = "true")
public class CustomUndertowCustomizerAutoConfiguration {

    @Bean
    @Primary
    public CustomUndertowWebServerFactoryCustomizer customUndertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        return new CustomUndertowWebServerFactoryCustomizer(environment, serverProperties);
    }
}
