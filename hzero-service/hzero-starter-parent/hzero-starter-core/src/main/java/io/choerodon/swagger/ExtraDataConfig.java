package io.choerodon.swagger;

import io.choerodon.swagger.swagger.extra.CompositeExtraDataInitialization;
import io.choerodon.swagger.swagger.extra.ExtraDataInitialization;
import io.choerodon.swagger.swagger.extra.ExtraDataProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ExtraData自动配置类
 *
 * @author XCXCXCXCX
 * @date 2019/9/4
 */
@Configuration
public class ExtraDataConfig {

    @Bean("extraDataProcessor")
    public ExtraDataProcessor extraDataProcessor(Environment environment) {
        return new ExtraDataProcessor(environment);
    }

    @Primary
    @Bean
    public ExtraDataInitialization extraDataInitialization(Optional<List<ExtraDataInitialization>> optional){
        return new CompositeExtraDataInitialization(optional.orElse(new ArrayList<>()));
    }

}
