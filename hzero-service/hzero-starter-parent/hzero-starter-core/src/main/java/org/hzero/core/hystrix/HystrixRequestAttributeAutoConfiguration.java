package org.hzero.core.hystrix;
/**
 * Copyright 2013-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collection;
import java.util.List;

/**
 * @author Reno Chou
 */
@Configuration
@ConditionalOnClass({Hystrix.class})
@ConditionalOnProperty(value = "hzero.hystrix.request-attribute.enabled", matchIfMissing = true)
@EnableConfigurationProperties(HystrixRequestAttributeProperties.class)
public class HystrixRequestAttributeAutoConfiguration {

    @Primary
    @Bean
    public HystrixConcurrencyStrategy primaryHystrixConcurrencyStrategy(Collection<HystrixConcurrencyStrategy> strategies) {
        return HystrixPlugins.getInstance().getConcurrencyStrategy();
    }

    @Bean
    public RequestAttributeCallableWrapper requestAttributeCallableWrapper() {
        return new RequestAttributeCallableWrapper();
    }

    @Bean
    public RequestAttributeHystrixConcurrencyStrategy requestAttributeHystrixConcurrencyStrategy(@Autowired(required = false) List<HystrixCallableWrapper> hystrixCallableWrapperList) {
        return new RequestAttributeHystrixConcurrencyStrategy(hystrixCallableWrapperList);
    }
}