package org.hzero.oauth.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.captcha.CaptchaMessageHelper;
import org.hzero.core.user.UserType;
import org.hzero.core.util.AssertUtils;
import org.hzero.oauth.config.LoginLanguageMapConfig;
import org.hzero.oauth.config.MultiLanguageConfig;
import org.hzero.oauth.domain.entity.Language;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.service.LanguageService;
import org.hzero.oauth.domain.service.OauthPageService;
import org.hzero.oauth.domain.service.UserLoginService;
import org.hzero.oauth.domain.utils.ConfigGetter;
import org.hzero.oauth.domain.utils.ProfileCode;
import org.hzero.oauth.infra.encrypt.EncryptClient;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.LoginType;
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.secheck.SecCheckVO;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.oauth.security.util.RequestUtil;
import org.hzero.starter.social.core.security.ProviderBindHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.social.connect.Connection;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 *
 * @author bojiangzhou 2020/10/21
 */
public class OauthPageServiceImpl implements OauthPageService {

    private static final String LOGIN_DEFAULT = "login";
    private static final String LOGIN_MOBILE = "login-mobile";
    private static final String PASS_EXPIRED_PAGE = "pass-expired";
    private static final String PASS_FORCE_MODIFY_PAGE = "pass-force-modify";
    private static final String PASS_SECONDARY_CHECK_PAGE = "pass-secondary-check";
    private static final String OPEN_BIND_PAGE = "open-app-bind";
    private static final String SLASH = BaseConstants.Symbol.SLASH;


    @Value("${hzero.oauth.redirect-url:login}")
    private String defaultUrl;

    @Autowired
    protected MultiLanguageConfig multiLanguageConfig;
    @Autowired
    protected UserLoginService userLoginService;
    @Autowired
    protected LanguageService languageService;
    @Autowired
    protected ConfigGetter configGetter;
    @Autowired
    protected SecurityProperties securityProperties;
    @Autowired
    protected EncryptClient encryptClient;
    @Autowired
    protected LoginLanguageMapConfig loginLanguageMapConfig;

    @Override
    public String indexPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        String template = (String) session.getAttribute(LoginUtil.FIELD_TEMPLATE);
        template = StringUtils.defaultIfBlank(template, configGetter.getValue(ProfileCode.OAUTH_DEFAULT_TEMPLATE));
        model.addAttribute(LoginUtil.FIELD_TEMPLATE, template);
        return template + SLASH + defaultUrl;
    }

    @Override
    public String loginPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        String device = request.getParameter("device");
        String type = request.getParameter("type");

        setPageDefaultData(request, session, model);
        String template = (String) session.getAttribute(LoginUtil.FIELD_TEMPLATE);
        // 登录页面
        String returnPage = "mobile".equals(device) ? LOGIN_MOBILE : LOGIN_DEFAULT;
        returnPage = template + SLASH + returnPage;

        // 登录方式
        type = LoginType.match(type) != null ? type : SecurityAttributes.DEFAULT_LOGIN_TYPE.code();
        model.addAttribute(SecurityAttributes.FIELD_LOGIN_TYPE, type);

        User user = userLoginService.queryRequestUser(request);
        // 是否需要验证码
        model.addAttribute(SecurityAttributes.FIELD_IS_NEED_CAPTCHA, userLoginService.isNeedCaptcha(user));

        // 错误消息
        Object ex = session.getAttribute(SecurityAttributes.SECURITY_LAST_EXCEPTION);
        String exceptionMessage = null;
        if (ex instanceof String) {
            exceptionMessage = (String) ex;
        } else if (ex instanceof Exception) {
            exceptionMessage = ((Exception) ex).getMessage();
        }
        if (StringUtils.isNotBlank(exceptionMessage)) {
            model.addAttribute(SecurityAttributes.FIELD_LOGIN_ERROR_MSG, exceptionMessage);
        }

        String username = (String) session.getAttribute(SecurityAttributes.SECURITY_LOGIN_USERNAME);

        SecurityAttributes.removeSecuritySessionAttribute(session);
        if (StringUtils.isBlank(username)) {
            return returnPage;
        }

        model.addAttribute(SecurityAttributes.SECURITY_LOGIN_USERNAME, username);
        if (LoginType.SMS.code().equals(type)) {
            model.addAttribute(SecurityAttributes.SECURITY_LOGIN_MOBILE, username);
        }

        return returnPage;
    }

    @Override
    public String renderView(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, String template, String view) {
        Map<String, String[]> params = request.getParameterMap();
        if (MapUtils.isNotEmpty(params)) {
            params.forEach((key, value) -> {
                model.addAttribute(key, value[0]);
            });
        }
        // 设置多语言
        String lang = (String) session.getAttribute(SecurityAttributes.FIELD_LANG);
        setFindPasswordPageLabel(model, lang);
        return template + SLASH + view;
    }

    @Override
    public String passwordExpiredPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // 模板
        String template = RequestUtil.getParameterValueFromRequestOrSavedRequest(request, LoginUtil.FIELD_TEMPLATE,
                configGetter.getValue(ProfileCode.OAUTH_DEFAULT_TEMPLATE));
        setLoginPageLabel(model, session);

        SecurityAttributes.removeSecuritySessionAttribute(session);

        return template + SLASH + PASS_EXPIRED_PAGE;
    }

    @Override
    public String forceModifyPasswordPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // 设置强制修改初始密码页面参数
        this.setForceModifyPasswordParams(model, session);
        // 添加参数
        setLoginPageLabel(model, session);
        // 模板
        String template = RequestUtil.getParameterValueFromRequestOrSavedRequest(request, LoginUtil.FIELD_TEMPLATE,
                configGetter.getValue(ProfileCode.OAUTH_DEFAULT_TEMPLATE));

        SecurityAttributes.removeSecuritySessionAttribute(session);

        return template + SLASH + PASS_FORCE_MODIFY_PAGE;
    }

    @Override
    public String secondCheckPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        String secCheckType = request.getParameter(SecCheckVO.PARAMETER_SEC_CHECK_TYPE);
        // 设置二次校验参数
        this.setSecCheckParameter(model, session, secCheckType);
        // 设置多语言
        this.setLoginPageLabel(model, session);
        // 设置通用的页面配置参数
        this.setCommonPageConfigData(model);

        // 错误消息
        String exceptionMessage = (String) session.getAttribute(SecurityAttributes.SECURITY_LAST_EXCEPTION);
        if (StringUtils.isNotBlank(exceptionMessage)) {
            model.addAttribute(SecurityAttributes.FIELD_LOGIN_ERROR_MSG, exceptionMessage);
        }

        // 模板
        String template = RequestUtil.getParameterValueFromRequestOrSavedRequest(request, LoginUtil.FIELD_TEMPLATE,
                configGetter.getValue(ProfileCode.OAUTH_DEFAULT_TEMPLATE));

        SecurityAttributes.removeSecuritySessionAttribute(session);

        return template + SLASH + PASS_SECONDARY_CHECK_PAGE;
    }

    @Override
    public String openBindPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        Connection<?> connection = ProviderBindHelper.getConnection(request);
        if (connection == null) {
            return "redirect:login";
        }
        setPageDefaultData(request, session, model);

        String template = (String) session.getAttribute(LoginUtil.FIELD_TEMPLATE);

        return template + SLASH + OPEN_BIND_PAGE;
    }

    @Override
    public List<Language> pageLanguages() {
        List<Language> list;
        if (configGetter.isTrue(ProfileCode.OAUTH_SHOW_LANGUAGE)) {
            list = languageService.listLanguage();
        } else {
            list = Collections.emptyList();
        }
        return list;
    }

    @Override
    public void changeLanguage(HttpServletRequest request, HttpSession session, String lang) {
        // 如果session中语言为空，设置初始化标识为true，否则设置为false
        session.setAttribute(SecurityAttributes.FIELD_INIT_LANG_FLAG,
                Optional.ofNullable(session.getAttribute(SecurityAttributes.FIELD_LANG))
                        .map(obj -> Boolean.FALSE).orElse(Boolean.TRUE));
        session.removeAttribute(SecurityAttributes.FIELD_LANG);
        if (StringUtils.isBlank(lang) || !lang.contains(BaseConstants.Symbol.LOWER_LINE)) {
            lang = configGetter.getValue(ProfileCode.OAUTH_DEFAULT_LANGUAGE);
        }
        session.setAttribute(SecurityAttributes.FIELD_LANG, lang);
    }

    @Override
    public void initLanguage(HttpServletRequest request, HttpSession session) {
        String lang = this.getChangeLang(String.valueOf(request.getLocale()));
        this.changeLanguage(request, session, lang);
    }

    @Override
    public Principal principal(HttpServletRequest request, Principal principal) {
        String implicitCall = request.getParameter("implicitCall");
        boolean implicit = false;
        if (StringUtils.isNotBlank(implicitCall) && Boolean.parseBoolean(implicitCall)) {
            implicit = true;
        }

        if (implicit) {
            return null;
        }

        return principal;
    }

    protected void setPageDefaultData(HttpServletRequest request, HttpSession session, Model model) {
        // 模板
        String template = this.getLoginTemplate(request,session);
        // 控制用户类型
        String userType = RequestUtil.getParameterValueFromRequestOrSavedRequest(request, UserType.PARAM_NAME, UserType.DEFAULT_USER_TYPE);
        // 控制登录字段
        String loginField = RequestUtil.getParameterValueFromRequestOrSavedRequest(request, LoginUtil.FIELD_LOGIN_FIELD, null);

        model.addAttribute(LoginUtil.FIELD_TEMPLATE, template);

        session.setAttribute(LoginUtil.FIELD_TEMPLATE, template);
        session.setAttribute(UserType.PARAM_NAME, userType);
        session.setAttribute(LoginUtil.FIELD_LOGIN_FIELD, loginField);

        // 是否加密
        boolean isPasswordEncrypt = securityProperties.getLogin().isPasswordEncrypt();
        boolean isAccountEncrypt = securityProperties.getLogin().isAccountEncrypt();
        model.addAttribute(LoginUtil.FIELD_PASSWORD_ENCRYPT, isPasswordEncrypt);
        model.addAttribute(LoginUtil.FIELD_ACCOUNT_ENCRYPT, isAccountEncrypt);
        if (isPasswordEncrypt || isAccountEncrypt) {
            String publicKey = encryptClient.getPublicKey();
            model.addAttribute(LoginUtil.FIELD_PUBLIC_KEY, publicKey);
            session.setAttribute(LoginUtil.FIELD_PUBLIC_KEY, publicKey);
        }

        setCommonPageConfigData(model);

        // 三方登录方式
        List<BaseOpenApp> apps = userLoginService.queryOpenLoginWays(request);
        model.addAttribute(SecurityAttributes.FIELD_OPEN_LOGIN_WAYS, apps);
        model.addAttribute(SecurityAttributes.FIELD_OPEN_LOGIN_WAYS_JSON, serialize(apps));
        // 语言
        if (configGetter.isTrue(ProfileCode.OAUTH_SHOW_LANGUAGE)) {
            List<Language> languages = languageService.listLanguage();
            model.addAttribute(SecurityAttributes.FIELD_LANGUAGES, languages);
            model.addAttribute(SecurityAttributes.FIELD_LANGUAGES_JSON, serialize(languages));
        }

        setLoginPageLabel(model, session);
    }

    /**
     * 设置通用的页面配置参数
     */
    private void setCommonPageConfigData(Model model) {
        // 页面标题
        model.addAttribute(LoginUtil.FIELD_TITLE, configGetter.getValue(ProfileCode.OAUTH_TITLE));
        // Logo 地址
        model.addAttribute(LoginUtil.FIELD_LOGO_URL, configGetter.getValue(ProfileCode.OAUTH_LOGO_URL));
        // copyright
        model.addAttribute(LoginUtil.FIELD_COPYRIGHT, configGetter.getValue(ProfileCode.OAUTH_COPYRIGHT));
    }

    /**
     * 设置登录页面多语言标签
     */
    protected void setLoginPageLabel(Model model, HttpSession session) {
        // 默认语言
        String language = (String) session.getAttribute(SecurityAttributes.FIELD_LANG);
        if (StringUtils.isBlank(language)) {
            language = configGetter.getValue(ProfileCode.OAUTH_DEFAULT_LANGUAGE);
            model.addAttribute(SecurityAttributes.FIELD_LANG, language);
        }
        Map<String, String> map = multiLanguageConfig.getLanguageValue(language);
        model.addAllAttributes(map);
    }

    protected void setFindPasswordPageLabel(Model model, String lang) {
        Map<String, String> map = multiLanguageConfig.getLanguageValue(lang);
        model.addAllAttributes(map);
        setCommonPageConfigData(model);
    }

    /**
     * 设置强制修改初始密码页面参数
     *
     * @param model   model对象
     * @param session session对象
     */
    protected void setForceModifyPasswordParams(Model model, HttpSession session) {
        // 是否需要强制验证码
        model.addAttribute(SecurityAttributes.SECURITY_FORCE_CODE_VERIFY,
                session.getAttribute(SecurityAttributes.SECURITY_FORCE_CODE_VERIFY));
        // 获取手机号
        Object phone = session.getAttribute(SecurityAttributes.SECURITY_LOGIN_MOBILE);
        if (Objects.nonNull(phone) && StringUtils.isNotBlank((String) phone)) {
            model.addAttribute(SecurityAttributes.SECURITY_LOGIN_MOBILE, CaptchaMessageHelper.encodeNumber((String) phone));
        }
    }

    /**
     * 设置二次校验参数
     *
     * @param model        model对象
     * @param session      session对象
     * @param secCheckType 参数上指定的二次校验类型
     */
    protected void setSecCheckParameter(Model model, HttpSession session, String secCheckType) {
        Object attribute = session.getAttribute(SecCheckVO.SEC_CHECK_KEY);
        AssertUtils.isTrue(attribute instanceof SecCheckVO, "获取二次校验相关参数失败");
        SecCheckVO secCheckVO = (SecCheckVO) attribute;

        // 当前用户支持的二次校验类型
        Set<String> supportTypes = secCheckVO.getSupportTypes();
        if (supportTypes.size() == 1) {
            // 如果只支持一种二次校验类型，就设置为这一种校验类型
            secCheckType = supportTypes.stream().findFirst().get();
        } else if (!supportTypes.contains(secCheckType)) {
            // 参数指定的二次校验类型不在支持的二次校验类型中，就设置为空
            secCheckType = "";
        }

        model.addAttribute(SecCheckVO.FIELD_SUPPORT_TYPES, supportTypes);
        model.addAttribute(SecCheckVO.PARAMETER_SEC_CHECK_TYPE, secCheckType);
        if (StringUtils.isNotBlank(secCheckVO.getPhone())) {
            model.addAttribute(SecCheckVO.FIELD_PHONE, CaptchaMessageHelper.encodeNumber(secCheckVO.getPhone()));
        }
        if (StringUtils.isNotBlank(secCheckVO.getEmail())) {
            model.addAttribute(SecCheckVO.FIELD_EMAIL, CaptchaMessageHelper.encodeNumber(secCheckVO.getEmail()));
        }
    }

    private String serialize(Object data) {
        try {
            return BaseConstants.MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取默认模板
     *
     * @param request 请求
     * @param session 会话
     * @return 模板编码
     */
    private String getLoginTemplate(HttpServletRequest request, HttpSession session) {
        String template = request.getParameter(LoginUtil.FIELD_TEMPLATE);
        ClassPathResource templateResource = new ClassPathResource("templates/" + template);
        if (!templateResource.exists()) {
            template = (String) session.getAttribute(LoginUtil.FIELD_TEMPLATE);
        }
        return StringUtils.isNotBlank(template) ? template : configGetter.getValue(ProfileCode.OAUTH_DEFAULT_TEMPLATE);
    }

    /**
     * 获取浏览器切换语言
     *
     * @param lang 语言代码
     * @return 切换语言
     */
    private String getChangeLang(String lang) {
        String langCode = lang;
        Map<String, String> langMap = loginLanguageMapConfig.getLangMap();
        if (langMap != null) {
            langCode = langMap.get(lang);
        }
        return StringUtils.isBlank(langCode) ? lang : langCode;
    }
}
