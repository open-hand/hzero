package org.hzero.mybatis.config.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.mybatis.helper.CrossSchemaHelper;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.MultiLanguageHelper;
import org.hzero.mybatis.helper.TenantLimitedHelper;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * <p>
 * 线程缓存清空
 * </p>
 *
 * @author qingsheng.chen 2018/11/10 星期六 10:00
 */
public class HelperInitInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CrossSchemaHelper.clear();
        DataSecurityHelper.clear();
        MultiLanguageHelper.clear();
        TenantLimitedHelper.clear();
        return super.preHandle(request, response, handler);
    }
}
