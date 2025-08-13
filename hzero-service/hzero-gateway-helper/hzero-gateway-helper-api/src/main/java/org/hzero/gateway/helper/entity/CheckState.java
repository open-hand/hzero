
package org.hzero.gateway.helper.entity;

public final class CheckState {

    /**
     * 全局层接口权限校验通过
     */
    public static final CheckState SUCCESS_PASS_SITE = new CheckState(201, "success.permission.sitePass", "SUCCESS_PASS_SITE");

    /**
     * 项目层权限校验通过
     */
    public static final CheckState SUCCESS_PASS_PROJECT = new CheckState(202, "success.permission.projectPass", "SUCCESS_PASS_PROJECT");

    /**
     * 组织层权限校验通过
     */
    public static final CheckState SUCCESS_PASS_ORG = new CheckState(203, "success.permission.organizationPass", "SUCCESS_PASS_ORG");

    /**
     * 该接口为gateway-helper设置的跳过权限校验的接口，允许通过
     */
    public static final CheckState SUCCESS_SKIP_PATH = new CheckState(204, "success.permission.skipPath", "SUCCESS_SKIP_PATH");

    /**
     * 公共接口，允许访问
     */
    public static final CheckState SUCCESS_PUBLIC_ACCESS = new CheckState(205, "success.permission.publicAccess", "SUCCESS_PUBLIC_ACCESS");

    /**
     * loginAccess接口且已经登录，允许访问
     */
    public static final CheckState SUCCESS_LOGIN_ACCESS = new CheckState(206, "success.permission.loginAccess", "SUCCESS_LOGIN_ACCESS");

    /**
     * 超级管理员用户，且接口为非内部接口，允许访问
     */
    public static final CheckState SUCCESS_ADMIN = new CheckState(207, "success.permission.adminUser", "SUCCESS_ADMIN");

    /**
     * gateway-helper关闭权限校验，一切请求都允许访问
     */
    public static final CheckState SUCCESS_PERMISSION_DISABLED = new CheckState(208, "success.permission.disabled", "SUCCESS_PERMISSION_DISABLED");

    /**
     * 开发者路由，不做权限校验
     */
    public static final CheckState SUCCESS_DEVELOPER_ROUTE = new CheckState(209, "success.developer.route", "SUCCESS_DEVELOPER_ROUTE");

    /**
     * 签名接口验证通过，允许访问
     */
    public static final CheckState SUCCESS_SIGNATURE_ACCESS = new CheckState(210, "success.permission.signAccess", "SUCCESS_SIGNATURE_ACCESS");

    /**
     * 内部接口禁止访问，只允许服务内部调用
     */
    public static final CheckState PERMISSION_WITH_IN = new CheckState(402, "error.permission.withinForbidden", "PERMISSION_WITH_IN");

    /**
     * 未找到与该请求相匹配的权限
     */
    public static final CheckState PERMISSION_MISMATCH = new CheckState(403, "error.permission.mismatch", "PERMISSION_MISMATCH");

    /**
     * 该登录用户没有此接口访问权限
     */
    public static final CheckState PERMISSION_NOT_PASS = new CheckState(404, "error.permission.notPass", "PERMISSION_NOT_PASS");

    /**
     * 该登录用户没有在此项目下的此接口访问权限
     */
    public static final CheckState PERMISSION_NOT_PASS_PROJECT = new CheckState(405, "error.permission.projectNotPass", "PERMISSION_NOT_PASS_PROJECT");

    /**
     * 该登录用户没有在此组织下的此接口访问权限
     */
    public static final CheckState PERMISSION_NOT_PASS_ORG = new CheckState(406, "error.permission.organizationNotPass", "PERMISSION_NOT_PASS_ORG");

    /**
     * 请求头部没有access_token
     */
    public static final CheckState PERMISSION_ACCESS_TOKEN_NULL = new CheckState(407, "error.permission.accessTokenNull", "PERMISSION_ACCESS_TOKEN_NULL");

    /**
     * accessToken不合法
     */
    public static final CheckState PERMISSION_ACCESS_TOKEN_INVALID = new CheckState(408, "error.permission.accessTokenInvalid", "PERMISSION_ACCESS_TOKEN_INVALID");

    /**
     * accessToken已过期
     */
    public static final CheckState PERMISSION_ACCESS_TOKEN_EXPIRED = new CheckState(409, "error.permission.accessTokenExpired", "PERMISSION_ACCESS_TOKEN_EXPIRED");

    /**
     * 通过access_token从oauthServer获取userDetails失败
     */
    public static final CheckState PERMISSION_GET_USE_DETAIL_FAILED = new CheckState(410, "error.permission.getUserDetailsFromOauthServer", "PERMISSION_GET_USE_DETAIL_FAILED");


    /**
     * 该项目已经被禁用
     */
    public static final CheckState PERMISSION_DISABLED_PROJECT = new CheckState(411, "error.permission.projectDisabled", "PERMISSION_DISABLED_PROJECT");

    /**
     * 该组织已经被禁用
     */
    public static final CheckState PERMISSION_DISABLED_ORG = new CheckState(412, "error.permission.organizationDisabled", "PERMISSION_DISABLED_ORG");
    /**
     * 白名单拦截
     */
    public static final CheckState PERMISSION_WHITE_LIST_FORBIDDEN = new CheckState(413, "error.permission.whiteListForbidden", "PERMISSION_WHITE_LIST_FORBIDDEN");
    /**
     * 黑名单拦截
     */
    public static final CheckState PERMISSION_BLACK_LIST_FORBIDDEN = new CheckState(414, "error.permission.blackListForbidden", "PERMISSION_BLACK_LIST_FORBIDDEN");
    /**
     * 签名认证不通过
     */
    public static final CheckState PERMISSION_NOT_PASS_SIGNATURE = new CheckState(415, "error.permission.signature", "PERMISSION_NOT_PASS_SIGNATURE");
    /**
     * 访问的菜单没有API权限
     */
    public static final CheckState PERMISSION_MENU_MISMATCH = new CheckState(416, "error.permission.menuMismatch", "PERMISSION_MENU_MISMATCH");
    /**
     * 访问权限层级不匹配
     */
    public static final CheckState PERMISSION_LEVEL_MISMATCH = new CheckState(417, "error.permission.levelMismatch", "PERMISSION_LEVEL_MISMATCH");
    /**
     * 分配角色过期
     */
    public static final CheckState MEMBER_ROLE_EXPIRED = new CheckState(418, "error.permission.memberRoleExpired", "MEMBER_ROLE_EXPIRED");
    /**
     * 没有角色
     */
    public static final CheckState ROLE_IS_EMPTY = new CheckState(419, "error.permission.roleIsEmpty", "ROLE_IS_EMPTY");
    /**
     * 重放请求
     */
    public static final CheckState REQUEST_REPEAT = new CheckState(425, "error.request.repeat", "REQUEST_REPEAT");

    /**
     * 访问过于频繁
     */
    public static final CheckState RATE_LIMIT_NOT_PASS = new CheckState(301, "error.visit.frequent", "RATE_LIMIT_NOT_PASS");

    /**
     * gatewayHelper发生异常
     */
    public static final CheckState EXCEPTION_GATEWAY_HELPER = new CheckState(501, "error.gatewayHelper.exception", "EXCEPTION_GATEWAY_HELPER");

    /**
     * 无法获取jwt
     */
    public static final CheckState EXCEPTION_OAUTH_SERVER = new CheckState(502, "error.oauthServer.exception", "EXCEPTION_OAUTH_SERVER");

    /**
     * 接口异常。项目下的接口路径必须包含project_id
     */
    public static final CheckState API_ERROR_PROJECT_ID = new CheckState(503, "error.api.projectId", "API_ERROR_PROJECT_ID");

    /**
     * 接口异常。组织下的接口路径必须包含organization_id
     */
    public static final CheckState API_ERROR_ORG_ID = new CheckState(504, "error.api.orgId", "API_ERROR_ORG_ID");

    /**
     * 接口异常。本请求可以同时匹配到多个接口
     */
    public static final CheckState API_ERROR_MATCH_MULTIPLY = new CheckState(505, "error.api.matchMultiplyPermission", "API_ERROR_MATCH_MULTIPLY");

    /**
     * 未找到该请求对应的zuul路由，请在路由管理页面添加路由
     */
    public static final CheckState PERMISSION_SERVICE_ROUTE = new CheckState(506, "error.permission.routeNotFound", "PERMISSION_SERVICE_ROUTE");

    private final int value;

    private final String code;

    private final String name;

    private CheckState(int value, String code, String name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    public static CheckState newState(int value, String code, String name) {
        return new CheckState(value, code, name);
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

