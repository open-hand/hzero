package org.hzero.oauth.security.secheck;

import org.apache.commons.lang3.BooleanUtils;
import org.hzero.core.util.AssertUtils;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.LoginField;
import org.hzero.oauth.security.util.RequestUtil;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 二次校验Helper
 *
 * @author bergturing 2021/06/18
 */
public final class SecCheckHelper {
    private SecCheckHelper() {
    }

    /**
     * 判断并处理是否跳转到二次校验的页面路径
     *
     * @param request            请求域对象
     * @param response           响应域对象
     * @param redirectStrategy   重定向策略对象
     * @param securityProperties 配置对象
     * @param localLoginUser     用户对象
     * @return 是否跳转 true 跳转 false 未跳转
     * @throws IOException 处理异常
     */
    public static boolean redirectToSecCheck(HttpServletRequest request, HttpServletResponse response,
                                             RedirectStrategy redirectStrategy, SecurityProperties securityProperties,
                                             User localLoginUser) throws IOException {
        // 非ldap用户且开启了二次校验才进行二次校验的逻辑
        if (Boolean.FALSE.equals(localLoginUser.getLdap()) && localLoginUser.isOpenSecCheck()) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            AssertUtils.notNull(requestAttributes, "Request Attributes Required");

            SecCheckVO secCheckVO = new SecCheckVO();
            // 判断是否可使用手机号验证
            if (BooleanUtils.isTrue(localLoginUser.getSecCheckPhoneFlag())) {
                secCheckVO.addSupportTypes(LoginField.PHONE.code());
                secCheckVO.setPhone(localLoginUser.getPhone());
            }
            // 判断是否可使用邮箱验证
            if (BooleanUtils.isTrue(localLoginUser.getSecCheckEmailFlag())) {
                secCheckVO.addSupportTypes(LoginField.EMAIL.code());
                secCheckVO.setEmail(localLoginUser.getEmail());
            }

            // 在SESSION中缓存二次校验信息
            requestAttributes.setAttribute(SecCheckVO.SEC_CHECK_KEY, secCheckVO, RequestAttributes.SCOPE_SESSION);
            // 跳转
            redirectStrategy.sendRedirect(request, response, getSecCheckPage(request, securityProperties));
            return true;
        }

        return false;
    }

    /**
     * 获取二次校验页面路径
     *
     * @param request 请求对象
     * @return 二次校验页面路径
     */
    private static String getSecCheckPage(HttpServletRequest request, SecurityProperties securityProperties) {
        // 请求路径
        return RequestUtil.getBaseURL(request) + securityProperties.getLogin().getPassSecondaryCheckPage();
    }
}
