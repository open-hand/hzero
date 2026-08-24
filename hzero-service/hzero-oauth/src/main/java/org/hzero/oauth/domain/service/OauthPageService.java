package org.hzero.oauth.domain.service;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import org.hzero.oauth.domain.entity.Language;

/**
 * 登录首页服务
 *
 * @author bojiangzhou 2020/10/21
 */
public interface OauthPageService {

    /**
     * @return 登录首页地址
     */
    String indexPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model);

    /**
     * @return 登录页面地址
     */
    String loginPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model);

    /**
     * @param template 模板
     * @param view     视图
     * @return 渲染页面
     */
    String renderView(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, String template, String view);

    /**
     * @return 密码过期页面
     */
    String passwordExpiredPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model);

    /**
     * @return 强制修改密码页面
     */
    String forceModifyPasswordPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model);

    /**
     * @return 二次校验页面
     */
    String secondCheckPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model);

    /**
     * @return 三方账号绑定页面
     */
    String openBindPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model);

    /**
     * @return 登录页面语言列表
     */
    List<Language> pageLanguages();

    /**
     * 登录页面初始化语言
     *
     * @param request 请求
     * @param session 会话
     */
    void initLanguage(HttpServletRequest request, HttpSession session);

    /**
     * 修改登录页面语言
     */
    void changeLanguage(HttpServletRequest request, HttpSession session, String lang);

    /**
     * 获取系统 Principal
     */
    Principal principal(HttpServletRequest request, Principal principal);


}
