package org.hzero.core.util;

import java.util.Set;
import java.util.TreeSet;

import org.hzero.core.exception.DangerousRouteException;
import org.hzero.core.exception.ServiceStartException;

/**
 * 路由校验工具类
 *
 * @author XCXCXCXCX
 * @date 2020/5/28 11:37 上午
 */
public class RouteValidateUtil {

    private RouteValidateUtil() {
    }

    private static final String CODE = "error.dangerous.route.pattern";
    private static final String FAILURE_CAUSE = "该路由[\"{routePath}\"]可能导致环境崩溃，请修改路由后重试!";
    private static final Set<String> DANGEROUS_ROUTES = new TreeSet<>();

    static {
        DANGEROUS_ROUTES.add("/**");
    }

    public static void assertDangerousRoute(String routePath) {
        if (DANGEROUS_ROUTES.contains(routePath)) {
            throw new DangerousRouteException(CODE, routePath);
        }
    }

    public static void assertDangerousRouteWhenStarted(String routePath) {
        if (DANGEROUS_ROUTES.contains(routePath)) {
            throw new ServiceStartException(CODE, FAILURE_CAUSE.replace("{routePath}", routePath), routePath);
        }
    }

}
