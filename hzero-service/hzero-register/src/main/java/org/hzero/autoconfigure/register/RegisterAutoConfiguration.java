package org.hzero.autoconfigure.register;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author bojiangzhou
 */
@ComponentScan(value = {
        "org.hzero.register.api"
})
@Configuration
public class RegisterAutoConfiguration {


}
