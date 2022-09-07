package org.hzero.oauth.domain.service;

import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.core.captcha.CaptchaResult;
import org.hzero.core.user.UserType;
import org.hzero.oauth.api.dto.Result;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.vo.AuthenticationResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 用户相关业务
 *
 * @author bojiangzhou 2019/02/25
 */
public interface UserLoginService {

    /**
     * 根据 request 中的 username 字段查询用户
     *
     * @param request HttpServletRequest
     * @return User
     */
    User queryRequestUser(HttpServletRequest request);

    /**
     * 是否需要验证码
     *
     * @param user 用户
     */
    boolean isNeedCaptcha(User user);

    /**
     * 查询三方登录方式
     */
    List<BaseOpenApp> queryOpenLoginWays(String channel);

    /**
     * 查询三方登录方式
     */
    List<BaseOpenApp> queryOpenLoginWays(HttpServletRequest request);

    /**
     * 发送手机验证码
     *
     * @param internationalTelCode 国际冠码
     * @param phone                手机号
     * @param userType             用户类型
     * @param businessScope        验证码业务范围
     * @param checkRegistered      检查是否已注册
     */
    CaptchaResult sendPhoneCaptcha(String internationalTelCode, String phone, UserType userType,
                                   String businessScope, boolean checkRegistered);

    /**
     * 获取登录的初始化参数
     *
     * @param request Request
     * @return 登录初始化参数
     */
    Map<String, Object> getLoginInitParams(HttpServletRequest request);

    /**
     * 手机+验证码登录获取Token
     *
     * @param request HttpServletRequest
     * @return Token
     */
    AuthenticationResult loginMobileForToken(HttpServletRequest request);

    /**
     * 三方登录获取Token
     *
     * @param request HttpServletRequest
     * @return Token
     */
    AuthenticationResult loginOpenForToken(HttpServletRequest request);

    /**
     * 绑定三方账号
     *
     * @param request HttpServletRequest
     * @return Token
     */
    AuthenticationResult bindOpenAccount(HttpServletRequest request);

    /**
     * 强制修改密码发送验证码
     *
     * @param session              session对象
     * @param internationalTelCode 国冠码
     * @param phone                手机号
     * @param userType             用户类型
     * @param businessScope        业务范围
     * @return 验证码发送结果
     */
    Result forceModifyPwdSendCaptcha(HttpSession session, String internationalTelCode,
                                     String phone, String userType, String businessScope);

    /**
     * @param session
     * @param secCheckType
     * @param internationalTelCode
     * @param userType
     * @param businessScope
     * @return
     */
    Result secCheckSendCaptcha(HttpSession session, String secCheckType, String internationalTelCode,
                               String userType, String businessScope);
}
