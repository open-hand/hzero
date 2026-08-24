package org.hzero.core.captcha;

import java.util.Properties;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.hzero.core.redis.RedisHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置验证码
 *
 * @author bojiangzhou 2018/08/08
 */
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@ConditionalOnProperty(prefix = CaptchaProperties.PREFIX, name = "enable", havingValue = "true")
public class CaptchaAutoConfiguration {

    @Bean(name = "captchaProducer")
    @ConditionalOnMissingBean(name = "captchaProducer")
    public DefaultKaptcha captchaProducer(CaptchaProperties captchaProperties) {
        Properties properties = new Properties();
        CaptchaProperties.Image image = captchaProperties.getImage();
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, image.getWidth() + "");
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, image.getHeight() + "");
        properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, image.getStyle().value());
        properties.setProperty(Constants.KAPTCHA_BORDER, image.getBorder().value());
        properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, image.getBorderColor());
        properties.setProperty(Constants.KAPTCHA_BORDER_THICKNESS, image.getBorderThickness() + "");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, image.getCharSource());
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, image.getCharLength() + "");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, image.getCharSpace() + "");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, image.getFontNames());
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, image.getFontSize() + "");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, image.getFontColor());
        properties.setProperty(Constants.KAPTCHA_NOISE_COLOR, image.getNoiseColor());
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, image.getBackgroundColorFrom());
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_TO, image.getBackgroundColorTo());

        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @Bean
    public CaptchaImageHelper captchaImageHelper(DefaultKaptcha captchaProducer, CaptchaProperties captchaProperties, RedisHelper redisHelper) {
        return new CaptchaImageHelper(captchaProducer, captchaProperties, redisHelper);
    }

    @Bean
    public CaptchaMessageHelper captchaSmsHelper(CaptchaProperties captchaProperties, RedisHelper redisHelper) {
        return new CaptchaMessageHelper(captchaProperties, redisHelper);
    }



}
