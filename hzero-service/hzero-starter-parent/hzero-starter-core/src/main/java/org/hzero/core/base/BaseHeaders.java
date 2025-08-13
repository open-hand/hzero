package org.hzero.core.base;

/**
 * 请求头，包含HZERO自定义的请求头(H_开头)
 *
 * @author bojiangzhou 2020/04/02
 */
public class BaseHeaders {

    /**
     * 资源根路径，例如请求 http://domain/api/iam/v1，在 nginx 要转发到 localhost:8080/iam/v1，那 /api 就是 RootPath.
     */
    public static final String H_ROOT_PATH = "H-Root-Path";

    /**
     * 请求来源，在 gateway 固定的源
     */
    public static final String H_REQUEST_FROM = "H-Request-From";

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * 请求头：菜单ID
     */
    public static final String H_MENU_ID = "H-Menu-Id";
    /**
     * 请求头：请求ID
     */
    public static final String H_REQUEST_ID = "H-Request-Id";

    /**
     * 请求头：租户ID
     */
    public static final String H_TENANT_ID = "H-Tenant-Id";

    public static final String X_REAL_IP = "X-Real-IP";

}
