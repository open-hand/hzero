package org.hzero.core.convert.config;

import org.hzero.core.convert.date.DateConverter;
import org.hzero.core.convert.date.LocalDateConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 * 日期格式转换配置类
 * </p>
 *
 * @author qingsheng.chen 2018/8/20 星期一 19:53
 */
@Configuration
public class ConvertWebMvcConfigurer implements WebMvcConfigurer {
    @Value("${hzero.date.converter.enable:true}")
    private boolean enable;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        if (enable) {
            registry.addConverter(new DateConverter());
            registry.addConverter(new LocalDateConverter());
        }
    }
}
