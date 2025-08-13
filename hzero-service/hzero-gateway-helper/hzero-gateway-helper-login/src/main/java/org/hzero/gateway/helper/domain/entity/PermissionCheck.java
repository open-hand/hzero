package org.hzero.gateway.helper.domain.entity;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Sets;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.util.StringUtil;

import org.hzero.core.base.BaseConstants;
import org.hzero.gateway.helper.entity.CheckResponse;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;

@ModifyAudit
@VersionAudit
@Table(name = "hiam_permission_check")
public class PermissionCheck extends AuditDomain {


    private final static String LOV = "LOV";
    private final static String API = "API";
    public final static Set<String> PERMISSION_CHECK_STATE =
            Collections.unmodifiableSet(Sets.newHashSet(
                    CheckState.PERMISSION_MISMATCH.name(),
                    CheckState.PERMISSION_NOT_PASS.name(),
                    CheckState.PERMISSION_NOT_PASS_ORG.name(),
                    CheckState.PERMISSION_MENU_MISMATCH.name()
            ));

    @GeneratedValue
    @Id
    private Long permissionCheckId;

    private String permissionCode;

    @NotNull
    private String permissionType;

    @NotNull
    private String apiPath;

    @NotNull
    private String apiMethod;

    private String fdLevel;

    private String serviceName;

    @NotNull
    private String checkState;

    private String accessToken;

    private String checkMessage;

    private String permissionDetails;

    private String routeDetails;

    private String userDetails;

    private Long menuId;

    private Long objectVersionNumber;

    private Date creationDate;


    public static PermissionCheck build(RequestContext requestContext, CheckResponse checkResponse) {
        PermissionCheck permissionCheck = new PermissionCheck();
        if (requestContext.getPermission() != null) {
            permissionCheck.setPermissionCode(requestContext.getPermission().getCode());
            permissionCheck.setFdLevel(requestContext.getPermission().getFdLevel());
            permissionCheck.setPermissionDetails(requestContext.getPermission().toJSONString());
        }
        if (requestContext.getRoute() != null) {
            permissionCheck.setServiceName(requestContext.getRoute().getServiceId());
            permissionCheck.setRouteDetails(requestContext.getRoute().toJSONString());
        }
        if (requestContext.getCustomUserDetails() != null) {
            permissionCheck.setUserDetails(requestContext.getCustomUserDetails().toJSONString());
        }
        if (StringUtil.isNotEmpty(requestContext.getLovCode())) {
            permissionCheck.setPermissionType(LOV);
            permissionCheck.setPermissionCode(requestContext.getLovCode());
        } else {
            permissionCheck.setPermissionType(API);
        }
        permissionCheck.setApiPath(requestContext.request.uri);
        permissionCheck.setApiMethod(requestContext.request.method);
        permissionCheck.setCheckState(checkResponse.getStatus().name());
        permissionCheck.setCheckMessage(checkResponse.getMessage());
        permissionCheck.setAccessToken(requestContext.request.accessToken);
        permissionCheck.setMenuId(requestContext.getMenuId());
        return permissionCheck;
    }

    private static String toJson(Object obj) {
        try {
            return BaseConstants.MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getPermissionCheckId() {
        return permissionCheckId;
    }

    public void setPermissionCheckId(Long permissionCheckId) {
        this.permissionCheckId = permissionCheckId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCheckMessage() {
        return checkMessage;
    }

    public void setCheckMessage(String checkMessage) {
        this.checkMessage = checkMessage;
    }

    public String getPermissionDetails() {
        return permissionDetails;
    }

    public void setPermissionDetails(String permissionDetails) {
        this.permissionDetails = permissionDetails;
    }

    public String getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(String routeDetails) {
        this.routeDetails = routeDetails;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(String userDetails) {
        this.userDetails = userDetails;
    }

    public String getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(String fdLevel) {
        this.fdLevel = fdLevel;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
