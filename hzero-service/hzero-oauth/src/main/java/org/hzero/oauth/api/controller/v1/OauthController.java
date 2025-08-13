package org.hzero.oauth.api.controller.v1;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import org.hzero.core.util.Results;
import org.hzero.oauth.api.dto.Result;
import org.hzero.oauth.domain.service.OauthPageService;
import org.hzero.oauth.security.constant.SecurityAttributes;


/**
 * @author bojiangzhou
 */
@RefreshScope
@Controller
public class OauthController {

    private final OauthPageService oauthPageService;

    public OauthController(OauthPageService oauthPageService) {
        this.oauthPageService = oauthPageService;
    }

    @GetMapping(value = "/")
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return oauthPageService.indexPage(request, response, session, model);
    }

    @GetMapping("/public/{template}/{view}")
    public String renderView(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @PathVariable String template, @PathVariable String view) {
        return oauthPageService.renderView(request, response, session, model, template, view);
    }

    /**
     * 默认登录页面
     */
    @GetMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        if (ObjectUtils.isEmpty(session.getAttribute(SecurityAttributes.FIELD_LANG))) {
            oauthPageService.initLanguage(request, session);
        }
        return oauthPageService.loginPage(request, response, session, model);
    }

    /**
     * 密码过期页面
     */
    @GetMapping(value = "/pass-page/expired")
    public String passExpired(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return oauthPageService.passwordExpiredPage(request, response, session, model);
    }

    /**
     * 强制修改初始密码页面
     */
    @GetMapping(value = "/pass-page/force-modify")
    public String passForceModify(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return oauthPageService.forceModifyPasswordPage(request, response, session, model);
    }

    @GetMapping(value = "/pass-page/secondary-check")
    public String passSecondaryCheck(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return oauthPageService.secondCheckPage(request, response, session, model);
    }

    /**
     * 跳转到绑定账号页面
     */
    @GetMapping(value = "/open-bind")
    public String bind(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return oauthPageService.openBindPage(request, response, session, model);
    }

    //
    // /api/user
    // ------------------------------------------------------------------------------

    @ResponseBody
    @RequestMapping("/api/user")
    public Principal user(HttpServletRequest request, Principal principal) {
        return oauthPageService.principal(request, principal);
    }

    /**
     * 查询登录语言
     */
    @GetMapping(value = "/login/lang")
    public ResponseEntity<Result> listLang() {
        Result result = new Result(true);
        result.setData(oauthPageService.pageLanguages());
        return Results.success(result);
    }

    /**
     * 设置登录语言
     */
    @PostMapping(value = "/login/lang")
    public ResponseEntity<Result> saveLang(HttpServletRequest request, HttpSession session, @RequestParam(required = false) String lang) {
        oauthPageService.changeLanguage(request, session, lang);
        return Results.success(new Result(true));
    }

}
