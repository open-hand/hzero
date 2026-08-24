package org.hzero.gateway.helper.entity;

import io.choerodon.core.oauth.CustomUserDetails;

public class RequestContext {

    private static final ThreadLocal<RequestContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static RequestContext initRequestContext(CheckRequest request, CheckResponse response){
        RequestContext requestContext = new RequestContext(request, response);
        CONTEXT_THREAD_LOCAL.set(requestContext);
        return requestContext;
    }

    public static RequestContext currentRequestContext(){
        return CONTEXT_THREAD_LOCAL.get();
    }

    public static void clearRequestContext(){
        CONTEXT_THREAD_LOCAL.remove();
    }

    public final CheckRequest request;

    public final CheckResponse response;

    private String requestKey;

    private PermissionDO permission;

    private CommonRoute route;

    private String trueUri;

    private CustomUserDetails customUserDetails;

    private String lovCode;

    private Object servletRequest;

    private Long menuId;

    public RequestContext(CheckRequest request, CheckResponse builder) {
        this.request = request;
        this.response = builder;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public PermissionDO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDO permission) {
        this.permission = permission;
    }

    public CommonRoute getRoute() {
        return route;
    }

    public void setRoute(CommonRoute route) {
        this.route = route;
    }

    public String getTrueUri() {
        return trueUri;
    }

    public void setTrueUri(String trueUri) {
        this.trueUri = trueUri;
    }

    public CustomUserDetails getCustomUserDetails() {
        return customUserDetails;
    }

    public void setCustomUserDetails(CustomUserDetails customUserDetails) {
        this.customUserDetails = customUserDetails;
    }

    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    public Object getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(Object servletRequest) {
        this.servletRequest = servletRequest;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "request=" + request +
                ", response=" + response +
                ", requestKey='" + requestKey + '\'' +
                ", menuId='" + menuId + '\'' +
                ", permission=" + permission +
                ", route=" + route +
                ", trueUri='" + trueUri + '\'' +
                ", customUserDetails=" + customUserDetails +
                '}';
    }


}

