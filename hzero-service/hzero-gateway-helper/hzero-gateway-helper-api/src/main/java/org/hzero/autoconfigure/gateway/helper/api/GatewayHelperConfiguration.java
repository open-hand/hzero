package org.hzero.autoconfigure.gateway.helper.api;

import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.config.GatewayPropertiesWrapper;
import org.hzero.gateway.helper.resolver.GatewayPropertiesResolver;
import org.hzero.gateway.helper.resolver.PropertiesResolver;
import org.hzero.gateway.helper.resolver.ZuulPropertiesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;

@Configuration
@EnableConfigurationProperties(GatewayHelperProperties.class)
public class GatewayHelperConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayHelperConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(Signer.class)
    public Signer jwtSigner(GatewayHelperProperties gatewayHelperProperties) {
        return new MacSigner(gatewayHelperProperties.getJwtKey());
    }

    @Bean
    public PropertiesResolver propertiesResolver(ApplicationContext applicationContext){

        Object bean = fallback(() ->
                applicationContext.getBean("primaryRouteLocator"));

        if(bean != null){
            return new ZuulPropertiesResolver(bean);
        }else if((bean = fallback(() ->
                applicationContext.getBean("routeDefinitionLocator"))) != null){
            return new GatewayPropertiesResolver(bean);
        }

        throw new IllegalStateException("Must rely on a kind of zuul or gateway.");
    }

    @Bean
    public GatewayPropertiesWrapper propertiesWrapper(ApplicationContext applicationContext){

        Object bean = fallback(() ->
                applicationContext.getBean("zuul-org.springframework.cloud.netflix.zuul.filters.ZuulProperties"));

        if(bean != null){
            return new GatewayPropertiesWrapper(bean);
        }else if((bean = fallback(() ->
                applicationContext.getBean("gatewayProperties"))) != null){
            return new GatewayPropertiesWrapper(bean);
        }

        throw new IllegalStateException("Must rely on a kind of zuul or gateway.");
    }

    private Object fallback(Function function) {
        Object returnVal = null;
        try {
            returnVal = function.apply();
        }catch (Throwable e){
            //fallback
            LOGGER.debug("contain getBean failed.");
        }
        return returnVal;
    }

}
@FunctionalInterface
interface Function{
    Object apply();
}
