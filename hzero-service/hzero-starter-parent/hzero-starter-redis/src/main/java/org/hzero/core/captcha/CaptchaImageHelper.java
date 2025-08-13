package org.hzero.core.captcha;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import org.hzero.core.exception.MessageException;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.redis.RedisHelper;

/**
 * 图片验证码输出
 *
 * @author bojiangzhou 2018/08/08
 */
public class CaptchaImageHelper {

    private static final String CAPTCHA_STATIC_PREFIX = ":captcha:";
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaImageHelper.class);

    private DefaultKaptcha captchaProducer;
    private CaptchaProperties captchaProperties;
    private RedisHelper redisHelper;

    public CaptchaImageHelper(DefaultKaptcha captchaProducer, CaptchaProperties captchaProperties, RedisHelper redisHelper) {
        this.captchaProducer = captchaProducer;
        this.captchaProperties = captchaProperties;
        this.redisHelper = redisHelper;
    }

    /**
     * 生成验证码并输出图片到指定输出流，验证码的key为UUID，设置到Cookie中，key和验证码将缓存到Redis中
     *
     * @param response HttpServletResponse
     * @param captchaCachePrefix 缓存验证码的前缀
     */
    public void generateAndWriteCaptchaImage(HttpServletResponse response, String captchaCachePrefix) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = null;
        try {
            String captcha = captchaProducer.createText();

            String captchaKey = CaptchaGenerator.generateCaptchaKey();
            Cookie cookie = new Cookie(CaptchaResult.FIELD_CAPTCHA_KEY, captchaKey);
            cookie.setPath(StringUtils.defaultIfEmpty("/", "/"));
            cookie.setMaxAge(-1);
            response.addCookie(cookie);

            // cache
            redisHelper.strSet(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey, captcha, captchaProperties.getImage().getExpire(), TimeUnit.MINUTES);

            // output
            BufferedImage bi = captchaProducer.createImage(captcha);
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (Exception e) {
            LOGGER.info("create captcha fail: {}", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    LOGGER.info("captcha output close fail: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 生成验证码并输出图片到指定输出流，通过 captchaKey 获取验证码，然后生成图片
     *
     * @param response HttpServletResponse
     * @param captchaKey 验证码Key
     * @param captchaCachePrefix 缓存验证码的前缀
     */
    public void generateAndWriteCaptchaImage(HttpServletResponse response, String captchaKey, String captchaCachePrefix) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = null;
        try {
            String captcha = redisHelper.strGet(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);

            // output
            BufferedImage bi = captchaProducer.createImage(captcha);
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (Exception e) {
            LOGGER.info("create captcha fail: {}", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    LOGGER.info("captcha output close fail: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 生成验证码，返回 captchaKey
     *
     * @param captchaCachePrefix 缓存验证码的前缀
     */
    public CaptchaResult generateCaptcha(String captchaCachePrefix) {
        String captcha = captchaProducer.createText();
        String captchaKey = CaptchaGenerator.generateCaptchaKey();
        // cache
        redisHelper.strSet(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey, captcha, captchaProperties.getImage().getExpire(), TimeUnit.MINUTES);
        // result
        CaptchaResult result = new CaptchaResult(true);
        result.setCaptchaKey(captchaKey);
        return result;
    }

    /**
     * 校验验证码
     *
     * @param request HttpServletRequest
     * @param captcha captcha
     * @param captchaCachePrefix captcha cache prefix
     */
    public CaptchaResult checkCaptcha(HttpServletRequest request, String captcha, String captchaCachePrefix) {
        if (captchaProperties.isTestDisable()) {
            return CaptchaResult.SUCCESS;
        }

        CaptchaResult captchaResult = new CaptchaResult();
        if (StringUtils.isBlank(captcha)) {
            captchaResult.setSuccess(false);
            captchaResult.setCode("captcha.validate.captcha.notnull");
            captchaResult.setMessage(MessageAccessor.getMessage("captcha.validate.captcha.notnull").desc());
            return captchaResult;
        }
        Cookie captchaKeyCookie = WebUtils.getCookie(request, CaptchaResult.FIELD_CAPTCHA_KEY);
        if (captchaKeyCookie == null) {
            throw new MessageException("captcha key not null");
        }
        String captchaKey = captchaKeyCookie.getValue();
        String cacheCaptcha = redisHelper.strGet(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);
        redisHelper.delKey(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);
        if (!StringUtils.equalsIgnoreCase(cacheCaptcha, captcha)) {
            captchaResult.setSuccess(false);
            captchaResult.setCode("captcha.validate.captcha.incorrect");
            captchaResult.setMessage(MessageAccessor.getMessage("captcha.validate.captcha.incorrect").desc());
            return captchaResult;
        }
        captchaResult.setSuccess(true);
        return captchaResult;
    }

    /**
     * 校验验证码
     *
     * @param captchaCachePrefix 缓存前缀
     * @param captchaKey 验证码KEY
     * @return 验证码
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, String captchaCachePrefix) {
        if (captchaProperties.isTestDisable()) {
            return CaptchaResult.SUCCESS;
        }

        CaptchaResult captchaResult = new CaptchaResult();
        if (StringUtils.isBlank(captcha)) {
            captchaResult.setSuccess(false);
            captchaResult.setCode("captcha.validate.captcha.notnull");
            captchaResult.setMessage(MessageAccessor.getMessage("captcha.validate.captcha.notnull").desc());
            return captchaResult;
        }
        String cacheCaptcha = redisHelper.strGet(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);
        redisHelper.delKey(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);
        if (!StringUtils.equalsIgnoreCase(cacheCaptcha, captcha)) {
            captchaResult.setSuccess(false);
            captchaResult.setCode("captcha.validate.captcha.incorrect");
            captchaResult.setMessage(MessageAccessor.getMessage("captcha.validate.captcha.incorrect").desc());
            return captchaResult;
        }
        captchaResult.setSuccess(true);
        return captchaResult;
    }

    /**
     * 从request cookie 中获取验证码
     *
     * @param request HttpServletRequest
     * @param captchaCachePrefix captcha cache prefix
     */
    public String getCaptcha(HttpServletRequest request, String captchaCachePrefix) {
        Cookie captchaKeyCookie = WebUtils.getCookie(request, CaptchaResult.FIELD_CAPTCHA_KEY);
        if (captchaKeyCookie == null) {
            return null;
        }
        String captchaKey = captchaKeyCookie.getValue();
        String captcha = redisHelper.strGet(captchaCachePrefix + ":captcha:" + captchaKey);
        redisHelper.delKey(captchaCachePrefix + ":captcha:" + captchaKey);
        return captcha;
    }

    /**
     * 获取验证码
     *
     * @param captchaCachePrefix 缓存前缀
     * @param captchaKey 验证码KEY
     * @return 验证码
     */
    public String getCaptcha(String captchaCachePrefix, String captchaKey) {
        String captcha = redisHelper.strGet(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);
        redisHelper.delKey(captchaCachePrefix + CAPTCHA_STATIC_PREFIX + captchaKey);
        return captcha;
    }

}
