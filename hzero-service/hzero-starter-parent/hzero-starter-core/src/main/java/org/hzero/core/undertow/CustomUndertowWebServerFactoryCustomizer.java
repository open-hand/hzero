package org.hzero.core.undertow;

import io.undertow.UndertowOptions;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.embedded.UndertowWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.core.env.Environment;

/**
 * @author qingsheng.chen@hand-china.com 2019-06-18 14:56
 */
public class CustomUndertowWebServerFactoryCustomizer extends UndertowWebServerFactoryCustomizer {
    public CustomUndertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        super(environment, serverProperties);
    }

    @Override
    public void customize(ConfigurableUndertowWebServerFactory factory) {
        super.customize(factory);
        factory.addBuilderCustomizers((builder) -> builder
                .setServerOption(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, true));
    }
}
