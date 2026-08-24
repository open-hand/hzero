package org.hzero.core.captcha;

import static org.hzero.core.base.BaseConstants.Symbol.COLON;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Regexs;

/**
 * 短信/邮箱验证码发送、校验
 *
 * @author bo.he02@hand-china.com 2020/04/01 增加【业务范围】
 * @author bojiangzhou 2018/08/08
 */
public class CaptchaMessageHelper {
    /**
     * captcha
     */
    private static final String CAPTCHA = ":captcha:";
    /**
     * KEY:验证码
     */
    private static final String KEY_CAPTCHA = "code:";
    /**
     * KEY:间隔时间
     */
    private static final String KEY_INTERVAL = "interval:";
    /**
     * KEY:发送次数
     */
    private static final String KEY_LIMIT_TIME = "time:";
    /**
     * KEY:验证结果
     */
    private static final String KEY_CHECK_RESULT = "result:";
    /**
     * KEY:验证失败次数
     */
    private static final String KEY_ERROR_TIME = "error-time:";
    /**
     * 用户类型
     */
    private static final String KEY_USER_TYPE = "user_type_";
    /**
     * 默认的业务范围
     */
    private static final String DEFAULT_BUSINESS_SCOPE = "default";

    /**
     * 验证码过期时间(分)
     */
    private final int expire;
    /**
     * 验证码字符来源，验证码值从中获取
     */
    private final char[] charSource;
    /**
     * 验证码字符长度
     */
    private final int charLength;
    /**
     * 发送验证码间隔时间(秒)
     */
    private final int interval;
    /**
     * 限制时间内发送次数上限(-1则无限制)
     */
    private final int limitTime;
    /**
     * 次数限制在多长时间内(小时)
     */
    private final int limitInterval;
    /**
     * 验证码验证次数
     */
    private final int maxErrorTime;

    /**
     * redisHelper
     */
    private final RedisHelper redisHelper;
    /**
     * 验证码配置参数对象
     */
    private final CaptchaProperties captchaProperties;

    /**
     * 构造函数
     *
     * @param captchaProperties 验证码配置参数对象
     * @param redisHelper       redisHelper
     */
    public CaptchaMessageHelper(CaptchaProperties captchaProperties,
                                RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
        this.captchaProperties = captchaProperties;

        CaptchaProperties.Sms sms = captchaProperties.getSms();
        this.expire = sms.getExpire();
        String source = StringUtils.isBlank(sms.getCharSource()) ? sms.getCharSource() : "0123456789";
        this.charSource = source.toCharArray();
        this.charLength = sms.getCharLength();
        this.interval = sms.getInterval();
        this.limitTime = sms.getLimitTime();
        this.limitInterval = sms.getLimitInterval();
        this.maxErrorTime = sms.getMaxErrorTime();
    }

    /**
     * 编码
     *
     * @param number 编码的对象
     * @return 编码结果
     */
    public static String encodeNumber(String number) {
        return StringUtils.substring(number, 0, 3) + "*****" + StringUtils.substring(number, -3);
    }

    /**
     * 生成总的缓存前缀值
     *
     * @param prefix        缓存前缀
     * @param userType      用户类型
     * @param businessScope 业务范围
     * @param key           key
     * @return 总的缓存前缀值
     */
    private static String getPrefixKey(String prefix, UserType userType,
                                       String businessScope, String key) {
        // 处理业务范围
        businessScope = StringUtils.isNoneBlank(businessScope) ? businessScope : DEFAULT_BUSINESS_SCOPE;

        if (userType == null) {
            // 返回结果
            return prefix + CAPTCHA + businessScope + COLON + key;
        }

        // 返回结果
        return prefix + CAPTCHA + KEY_USER_TYPE + StringUtils.lowerCase(userType.value()) + COLON
                + businessScope + COLON + key;
    }

    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param crownCode          国际冠码
     * @param mobile             手机号
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     * @see CaptchaMessageHelper#generateMobileCaptcha(java.lang.String, java.lang.String, org.hzero.core.user.UserType, java.lang.String)
     */
    public CaptchaResult generateMobileCaptcha(String crownCode, String mobile,
                                               String captchaCachePrefix) {
        return this.generateMobileCaptcha(crownCode, mobile, null, captchaCachePrefix);
    }

    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param crownCode          国际冠码
     * @param mobile             手机号
     * @param captchaCachePrefix 缓存前缀
     * @param userType           用户类型
     * @return Captcha 封装
     * @see CaptchaMessageHelper#generateMobileCaptcha(java.lang.String, java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult generateMobileCaptcha(String crownCode, String mobile, UserType userType,
                                               String captchaCachePrefix) {
        return this.generateMobileCaptcha(crownCode, mobile, userType, null, captchaCachePrefix);
    }

    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param crownCode          国际冠码
     * @param mobile             手机号
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     * @see CaptchaMessageHelper#generateMobileCaptcha(java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult generateMobileCaptcha(String crownCode, String mobile, UserType userType,
                                               String businessScope, String captchaCachePrefix) {
        return this.generateMobileCaptcha(crownCode + BaseConstants.Symbol.MIDDLE_LINE + mobile,
                userType, businessScope, captchaCachePrefix);
    }

    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param mobile             手机号，国际冠码可用 "-" 分隔，如 +86-18566666666，如果没有国际冠码前缀，则默认为 +86
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     * @see CaptchaMessageHelper#generateMobileCaptcha(java.lang.String, org.hzero.core.user.UserType, java.lang.String)
     */
    public CaptchaResult generateMobileCaptcha(String mobile, String captchaCachePrefix) {
        return this.generateMobileCaptcha(mobile, UserType.ofDefault(), captchaCachePrefix);
    }

    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param mobile             手机号，国际冠码可用 "-" 分隔，如 +86-18566666666，如果没有国际冠码前缀，则默认为 +86
     * @param userType           用户类型
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     * @see CaptchaMessageHelper#generateMobileCaptcha(java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult generateMobileCaptcha(String mobile, UserType userType,
                                               String captchaCachePrefix) {
        // 返回生成的手机验证码
        return this.generateMobileCaptcha(mobile, userType, null, captchaCachePrefix);
    }

    /**
     * 生成手机验证码，缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     * 【Public End Method】
     *
     * @param mobile             手机号，国际冠码可用 "-" 分隔，如 +86-18566666666，如果没有国际冠码前缀，则默认为 +86
     * @param userType           用户类型
     * @param businessScope      业务范围，如果没有业务范围，则默认为 default
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     * @see CaptchaMessageHelper#DEFAULT_BUSINESS_SCOPE
     */
    public CaptchaResult generateMobileCaptcha(String mobile, UserType userType,
                                               String businessScope, String captchaCachePrefix) {
        // 最终返回的Captcha 封装对象
        CaptchaResult result;

        // 处理国际冠码
        String crownCode;
        if (mobile.contains(BaseConstants.Symbol.MIDDLE_LINE)) {
            // 分离国际冠码和手机号
            crownCode = StringUtils.substringBefore(mobile, BaseConstants.Symbol.MIDDLE_LINE);
            mobile = StringUtils.substringAfter(mobile, BaseConstants.Symbol.MIDDLE_LINE);
        } else {
            // 使用默认的国际冠码
            crownCode = BaseConstants.DEFAULT_CROWN_CODE;
        }

        // 校验手机号码格式
        if (!Regexs.isMobile(crownCode, mobile)) {
            result = new CaptchaResult();
            // 手机号码格式异常
            result.setSuccess(false);
            result.setCode("phone.format.incorrect");
            result.setMessage(MessageAccessor.getMessage("phone.format.incorrect", new Object[]{mobile}).desc());
            return result;
        }

        // 生成验证码
        result = this.generateCaptcha(mobile, userType, businessScope, captchaCachePrefix);
        if (result.isSuccess()) {
            // 生成成功，设置消息编码
            result.setCode("captcha.send.phone");
            result.setMessage(MessageAccessor.getMessage("captcha.send.phone", new Object[]{encodeNumber(mobile), String.valueOf(expire)}).desc());
        }

        // 返回生成的验证码
        return result;
    }

    /**
     * 生成邮件验证码，并缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param email              邮箱
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     * @see CaptchaMessageHelper#generateEmailCaptcha(java.lang.String, org.hzero.core.user.UserType, java.lang.String)
     */
    public CaptchaResult generateEmailCaptcha(String email, String captchaCachePrefix) {
        return this.generateEmailCaptcha(email, UserType.ofDefault(), captchaCachePrefix);
    }

    /**
     * 生成邮件验证码，并缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     *
     * @param email              邮箱
     * @param userType           用户类型
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha
     * @see CaptchaMessageHelper#generateEmailCaptcha(java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult generateEmailCaptcha(String email, UserType userType, String captchaCachePrefix) {
        return this.generateEmailCaptcha(email, userType, null, captchaCachePrefix);
    }

    /**
     * 生成邮件验证码，并缓存验证码。返回captcha、captchaKey，需自己调用消息服务发送消息。无论成功与否，都会返回对应的消息。
     * 【Public End Method】
     *
     * @param email              邮箱
     * @param businessScope      业务范围
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     */
    public CaptchaResult generateEmailCaptcha(String email, UserType userType,
                                              String businessScope, String captchaCachePrefix) {
        // 最终返回的Captcha 封装对象
        CaptchaResult result;

        // 校验邮箱格式
        if (!Regexs.isEmail(email)) {
            // 邮箱格式异常
            result = new CaptchaResult();
            result.setSuccess(false);
            result.setCode("email.format.incorrect");
            result.setMessage(MessageAccessor.getMessage("email.format.incorrect", new Object[]{email}).desc());
            return result;
        }

        // 生成验证码
        result = this.generateCaptcha(email, userType, businessScope, captchaCachePrefix);
        if (result.isSuccess()) {
            // 生成成功，设置消息编码
            result.setCode("captcha.send.email");
            result.setMessage(MessageAccessor.getMessage("captcha.send.email", new Object[]{encodeNumber(email), String.valueOf(expire)}).desc());
        }

        // 返回生成的验证码
        return result;
    }

    /**
     * 验证验证码，不检查手机号是否一致。验证通过返回success=true，否则返回相应的错误信息。
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果
     * @return Captcha 封装
     * @see CaptchaMessageHelper#checkCaptcha(java.lang.String, java.lang.String, org.hzero.core.user.UserType, java.lang.String, boolean)
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha,
                                      String captchaCachePrefix, boolean cacheCheckResult) {
        return this.checkCaptcha(captchaKey, captcha, UserType.ofDefault(),
                captchaCachePrefix, cacheCheckResult);
    }

    /**
     * 验证验证码，不检查手机号是否一致。验证通过返回success=true，否则返回相应的错误信息。
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param userType           用户类型
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果
     * @return Captcha 封装
     * @see CaptchaMessageHelper#checkCaptcha(java.lang.String, java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String, boolean)
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, UserType userType,
                                      String captchaCachePrefix, boolean cacheCheckResult) {
        return this.checkCaptcha(captchaKey, captcha, userType, null,
                captchaCachePrefix, cacheCheckResult);
    }

    /**
     * 验证验证码，不检查手机号是否一致。验证通过返回success=true，否则返回相应的错误信息。
     * 【Public End Method】
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果
     * @return Captcha 封装
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, UserType userType,
                                      String businessScope, String captchaCachePrefix,
                                      boolean cacheCheckResult) {
        return this.checkCaptchaWithNumber(captchaKey, captcha, null, userType, businessScope,
                captchaCachePrefix, cacheCheckResult);
    }

    /**
     * 验证验证码，必须传入手机号。验证通过返回success=true，否则返回相应的错误信息。
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param number             手机号/邮箱
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果，缓存则返回缓存了验证结果的KEY
     * @return Captcha 封装
     * @see CaptchaMessageHelper#checkCaptcha(java.lang.String, java.lang.String, java.lang.String, org.hzero.core.user.UserType, java.lang.String, boolean)
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, String number,
                                      String captchaCachePrefix, boolean cacheCheckResult) {
        return this.checkCaptcha(captchaKey, captcha, number, UserType.ofDefault(), captchaCachePrefix,
                cacheCheckResult);
    }

    /**
     * 验证验证码，必须传入手机号。验证通过返回success=true，否则返回相应的错误信息。
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param number             手机号/邮箱
     * @param userType           用户类型
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果，缓存则返回缓存了验证结果的KEY
     * @return Captcha 封装
     * @see CaptchaMessageHelper#checkCaptcha(java.lang.String, java.lang.String, java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String, boolean)
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, String number,
                                      UserType userType, String captchaCachePrefix,
                                      boolean cacheCheckResult) {
        return this.checkCaptcha(captchaKey, captcha, number, userType, null,
                captchaCachePrefix, cacheCheckResult);
    }

    /**
     * 验证验证码，必须传入手机号/邮箱。验证通过返回success=true，否则返回相应的错误信息。
     * 【Public End Method】
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param number             手机号/邮箱
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果，缓存则返回缓存了验证结果的KEY
     * @return Captcha 封装
     */
    public CaptchaResult checkCaptcha(String captchaKey, String captcha, String number,
                                      UserType userType, String businessScope,
                                      String captchaCachePrefix, boolean cacheCheckResult) {
        if (StringUtils.isBlank(number)) {
            CaptchaResult result = new CaptchaResult();
            result.setSuccess(false);
            result.setCode("captcha.validate.number-not-null");
            result.setMessage(MessageAccessor.getMessage("captcha.validate.number-not-null").desc());
            return result;
        }
        return this.checkCaptchaWithNumber(captchaKey, captcha, number, userType, businessScope,
                captchaCachePrefix, cacheCheckResult);
    }

    /**
     * 检查上一次验证码是否校验通过. 验证码正确，返回success=true，否则返回相应的message
     *
     * @param lastCheckKey       上一次校验的key
     * @param captchaCachePrefix 缓存前缀
     * @see CaptchaMessageHelper#checkLastResult(java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult checkLastResult(String lastCheckKey, String captchaCachePrefix) {
        return this.checkLastResult(lastCheckKey, null, null, captchaCachePrefix);
    }

    /**
     * 检查上一次验证码是否校验通过. 验证码正确，返回success=true，否则返回相应的message
     * 【Public End Method】
     *
     * @param lastCheckKey       上一次校验的key
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 缓存前缀
     */
    public CaptchaResult checkLastResult(String lastCheckKey, UserType userType,
                                         String businessScope, String captchaCachePrefix) {
        if (captchaProperties.isTestDisable()) {
            return CaptchaResult.SUCCESS;
        }
        String lastCheckResult = redisHelper.strGet(getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_CHECK_RESULT) + lastCheckKey);
        CaptchaResult result = new CaptchaResult();
        if (StringUtils.isBlank(lastCheckResult)) {
            result.setSuccess(false);
            result.setCode("captcha.validate.last-check-incorrect");
            result.setMessage(MessageAccessor.getMessage("captcha.validate.last-check-incorrect").desc());
            return result;
        }

        result.setSuccess(true);

        return result;
    }

    /**
     * cache the validate result.
     *
     * @param cachePrefix cache prefix.
     * @return {@link CaptchaResult#FIELD_LAST_CHECK_KEY}
     * @see CaptchaMessageHelper#cacheCheckResult(java.lang.String, java.lang.String)
     */
    public CaptchaResult cacheCheckResult(String cachePrefix) {
        return this.cacheCheckResult(cachePrefix, "1");
    }

    /**
     * cache the validate result.
     *
     * @param cachePrefix cache prefix.
     * @return {@link CaptchaResult#FIELD_LAST_CHECK_KEY}
     * @see CaptchaMessageHelper#cacheCheckResult(java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult cacheCheckResult(String cachePrefix, UserType userType, String businessScope) {
        return this.cacheCheckResult(cachePrefix, userType, businessScope, "1");
    }

    /**
     * cache the validate result.
     *
     * @param cachePrefix cache prefix
     * @param result      cache value
     * @return {@link CaptchaResult#FIELD_LAST_CHECK_KEY}
     * @see CaptchaMessageHelper#cacheCheckResult(java.lang.String, org.hzero.core.user.UserType, java.lang.String, java.lang.String)
     */
    public CaptchaResult cacheCheckResult(String cachePrefix, String result) {
        return this.cacheCheckResult(cachePrefix, null, null, result);
    }

    /**
     * cache the validate result.
     * 【Public End Method】
     *
     * @param cachePrefix   cache prefix
     * @param userType      user type
     * @param businessScope business scope
     * @param result        cache value
     * @return {@link CaptchaResult#FIELD_LAST_CHECK_KEY}
     */
    public CaptchaResult cacheCheckResult(String cachePrefix, UserType userType,
                                          String businessScope, String result) {
        String lastCheckKey = CaptchaGenerator.generateCaptchaKey();
        redisHelper.strSet(getPrefixKey(cachePrefix, userType, businessScope, KEY_CHECK_RESULT) + lastCheckKey, result, expire * 2L, TimeUnit.MINUTES);
        CaptchaResult captchaResult = new CaptchaResult();
        captchaResult.setLastCheckKey(lastCheckKey);
        captchaResult.setSuccess(true);
        return captchaResult;
    }

    /**
     * 生成验证码
     *
     * @param number             验证码的业务值，如果是生成手机验证码，则为手机号；如果是生成邮箱验证码，则为邮箱
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 缓存前缀
     * @return Captcha 封装
     */
    private CaptchaResult generateCaptcha(String number, UserType userType,
                                          String businessScope, String captchaCachePrefix) {
        // 最终返回的Captcha 封装对象
        CaptchaResult result = new CaptchaResult();

        // 检查间隔时间
        if (checkInterval(number, userType, businessScope, captchaCachePrefix, result)) {
            result.setSuccess(false);
            return result;
        }

        // 检查发送次数是否已达上限
        if (checkLimitTime(number, userType, businessScope, captchaCachePrefix)) {
            result.setSuccess(false);
            result.setCode("captcha.send.time-over");
            result.setMessage(MessageAccessor.getMessage("captcha.send.time-over", new Object[]{String.valueOf(limitInterval)}).desc());
            return result;
        }

        // 生成验证码
        String captcha = CaptchaGenerator.generateNumberCaptcha(charLength, charSource);
        String captchaKey = CaptchaGenerator.generateCaptchaKey();

        // cache
        String group = captcha + "_" + number;
        // 设置 验证码
        redisHelper.strSet(getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_CAPTCHA) + captchaKey, group, expire, TimeUnit.MINUTES);
        // 设置 间隔时间
        redisHelper.strSet(getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_INTERVAL) + number, "1", interval, TimeUnit.SECONDS);
        if (limitTime > 0) {
            // 设置发送次数
            redisHelper.strIncrement(getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_LIMIT_TIME) + number, 1L);
            // 设置发送次数过期时间
            redisHelper.setExpire(getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_LIMIT_TIME) + number, limitInterval, TimeUnit.HOURS);
        }

        // 设置结果信息
        result.setSuccess(true);
        result.setCaptcha(captcha);
        result.setCaptchaKey(captchaKey);
        result.setInterval(interval);

        // 返回生成的结果
        return result;
    }

    /**
     * 验证验证码
     *
     * @param captchaKey         缓存KEY
     * @param captcha            验证码
     * @param number             手机号/邮箱
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 验证码缓存前缀
     * @param cacheCheckResult   是否缓存验证结果，缓存则返回缓存了验证结果的KEY
     * @return Captcha 封装
     */
    private CaptchaResult checkCaptchaWithNumber(String captchaKey, String captcha, String number,
                                                 UserType userType, String businessScope,
                                                 String captchaCachePrefix, boolean cacheCheckResult) {
        if (captchaProperties.isTestDisable()) {
            return CaptchaResult.SUCCESS;
        }
        String key = getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_CAPTCHA) + captchaKey;
        String group = redisHelper.strGet(key);
        String[] groupArr = StringUtils.split(group, "_", 2);

        CaptchaResult result = new CaptchaResult();

        if (groupArr == null) {
            result.setSuccess(false);
            result.setCode("captcha.validate.overdue");
            result.setMessage(MessageAccessor.getMessage("captcha.validate.overdue").desc());
            return result;
        }

        // 校验发送验证码时的手机号与修改的手机号是否一致
        if (number != null && !StringUtils.equalsAnyIgnoreCase(groupArr[1], number)) {
            result.setSuccess(false);
            result.setCode("captcha.validate.number-not-match");
            result.setMessage(MessageAccessor.getMessage("captcha.validate.number-not-match", new Object[]{number}).desc());
            return result;
        }

        // 验证码错误
        if (!StringUtils.equalsIgnoreCase(groupArr[0], captcha)) {
            result.setSuccess(false);
            result.setCode("captcha.validate.incorrect");
            result.setMessage(MessageAccessor.getMessage("captcha.validate.incorrect").desc());

            handleErrorTime(captchaKey, userType, businessScope, captchaCachePrefix, result);

            return result;
        }

        // 验证通过清除验证码
        redisHelper.delKey(key);
        // 清除间隔时间
        String intervalKey = getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_INTERVAL) + number;
        redisHelper.delKey(intervalKey);

        if (cacheCheckResult) {
            return this.cacheCheckResult(captchaCachePrefix, userType, businessScope);
        }

        result.setSuccess(true);

        return result;
    }

    /**
     * 处理验证码验证失败次数
     *
     * @param captchaKey         缓存KEY
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 验证码缓存前缀
     * @param result             Captcha 封装
     */
    private void handleErrorTime(String captchaKey, UserType userType, String businessScope,
                                 String captchaCachePrefix, CaptchaResult result) {
        // 验证码错误，保存验证次数
        String errKey = getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_ERROR_TIME) + captchaKey;
        String errTimeS = redisHelper.strGet(errKey);
        int errTime = 0;
        if (StringUtils.isNotEmpty(errTimeS)) {
            errTime = Integer.parseInt(errTimeS);
        }

        int errorTimes = errTime + 1;
        // 如果大于最大错误次数，清除验证码
        if (errorTimes >= maxErrorTime) {
            String key = getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_CAPTCHA) + captchaKey;
            redisHelper.delKey(key);
            redisHelper.delKey(errKey);
            // 验证错误次数太多，请重新获取
            result.setCode("captcha.validate.error.too-many-time");
            result.setMessage(MessageAccessor.getMessage("captcha.validate.error.too-many-time").desc());
        } else {
            redisHelper.strSet(errKey, String.valueOf(errorTimes));
            result.setErrorTimes(errorTimes);
            result.setSurplusTimes(maxErrorTime - errorTimes);
        }
    }

    /**
     * 检查间隔时间
     *
     * @param number             手机号/邮箱
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 验证码缓存前缀
     * @param result             Captcha 对象
     * @return true 通过检查 false 未通过检查
     */
    private boolean checkInterval(String number, UserType userType, String businessScope,
                                  String captchaCachePrefix, CaptchaResult result) {
        String key = getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_INTERVAL) + number;
        Long expireTime = redisHelper.getExpire(key, TimeUnit.SECONDS);
        if (expireTime != null && expireTime > 0) {
            result.setCode("captcha.send.interval");
            result.setInterval(expireTime);
            result.setMessage(MessageAccessor.getMessage("captcha.send.interval", new Object[]{String.valueOf(expireTime)}).desc());
            return true;
        }
        return false;
    }

    /**
     * 检查发送次数是否已达上限
     *
     * @param number             手机号/邮箱
     * @param userType           用户类型
     * @param businessScope      业务范围
     * @param captchaCachePrefix 验证码缓存前缀
     */
    private boolean checkLimitTime(String number, UserType userType,
                                   String businessScope, String captchaCachePrefix) {
        if (limitTime > 0) {
            String key = getPrefixKey(captchaCachePrefix, userType, businessScope, KEY_LIMIT_TIME) + number;
            String time = redisHelper.strGet(key);
            return StringUtils.isNotBlank(time) && Integer.parseInt(time) >= limitTime;
        }
        return false;
    }
}
