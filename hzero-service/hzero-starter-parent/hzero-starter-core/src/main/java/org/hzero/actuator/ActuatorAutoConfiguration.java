package org.hzero.actuator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.hzero.actuator.contributor.ExecutorInfoContributor;

/**
 *
 * @author bojiangzhou 2020/03/30
 */
@ComponentScan(value = {
        "org.hzero.actuator.strategy",
        "org.hzero.actuator.endpoint"
})
@EnableConfigurationProperties(ActuatorProperties.class)
@Configuration
public class ActuatorAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "hzero.actuator.show-executor-info", havingValue = "true")
    public ExecutorInfoContributor executorInfoContributor() {
        return new ExecutorInfoContributor();
    }

}
