package org.hzero.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.mybatis.controller.MultiLanguageController;
import org.hzero.mybatis.service.MultiLanguageService;
import org.hzero.mybatis.service.impl.MultiLanguageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 多语言配置 + 数据防篡改
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 15:30
 */
@Configuration
public class HzeroMybatisMapperConfig {

    @Bean
    public SecurityTokenExceptionHandler securityTokenExceptionHandler() {
        return new SecurityTokenExceptionHandler();
    }

    @Bean
    public MultiLanguageService multiLanguageService(SqlSessionFactory sqlSessionFactory) {
        return new MultiLanguageServiceImpl(sqlSessionFactory);
    }

    @Bean
    public MultiLanguageController multiLanguageController(MultiLanguageService multiLanguageService) {
        return new MultiLanguageController(multiLanguageService);
    }
}
