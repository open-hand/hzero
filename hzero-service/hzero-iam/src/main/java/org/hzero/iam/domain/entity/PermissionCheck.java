package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

@ModifyAudit
@VersionAudit
@Table(name = "hiam_permission_check")
public class PermissionCheck extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_permission_check";

    public final static String FIELD_PERMISSION_CHECK_ID = "permissionCheckId";
    public final static String FIELD_HANDLE_STATUS = "handleStatus";
    public final static String FIELD_PERMISSION_CODE = "permissionCode";
    public final static String FIELD_CREATION_DATE = "creationDate";

    /**
     * 已处理
     */
    private static final String HANDLE_STATUS_PROCESSED = "PROCESSED";
    /**
     * 未处理
     */
    private static final String HANDLE_STATUS_UNTREATED = "UNTREATED";

    private static final String PERMISSION_NOT_PASS = "PERMISSION_NOT_PASS";
    private static final String PERMISSION_MISMATCH = "PERMISSION_MISMATCH";
    private static final String PERMISSION_MENU_MISMATCH = "PERMISSION_MENU_MISMATCH";

    public void processed() {
        this.handleStatus = HANDLE_STATUS_PROCESSED;
    }

    public void untreated() {
        this.handleStatus = HANDLE_STATUS_UNTREATED;
    }

    public void mismatch() {
        this.checkState = PERMISSION_MISMATCH;
    }

    public void notPass() {
        this.checkState = PERMISSION_NOT_PASS;
    }

    public void menuMismatch () {
        this.checkState = PERMISSION_MENU_MISMATCH;
    }

    @JsonIgnore
    public boolean isUntreated() {
        return StringUtils.equals(this.handleStatus, HANDLE_STATUS_UNTREATED);
    }

    @GeneratedValue
    @Id
    @Encrypt
    private Long permissionCheckId;

    @LovValue(lovCode = "HIAM.PERMISSION_CHECK.HANDLE_STATUS")
    private String handleStatus;

    private String permissionCode;

    @NotNull
    private String permissionType;

    @NotNull
    private String apiPath;

    @NotNull
    @LovValue(lovCode = "HIAM.REQUEST_METHOD", meaningField = "apiMethodMeaning")
    private String apiMethod;

    private String serviceName;

    @NotNull
    @LovValue(lovCode = "HIAM.PERMISSION_CHECK.CHECK_STATE", meaningField = "checkStateMeaning")
    private String checkState;

    @LovValue(lovCode = "HIAM.PERMISSION_LEVEL", meaningField = "levelMeaning")
    private String fdLevel;

    private String accessToken;

    private String checkMessage;

    private String permissionDetails;

    private String routeDetails;

    private String userDetails;

    private Long objectVersionNumber;

    private Date creationDate;

    private Long menuId;

    @Transient
    private String handleStatusMeaning;
    @Transient
    private String apiMethodMeaning;
    @Transient
    private String checkStateMeaning;
    @Transient
    private String levelMeaning;
    @Transient
    private String menuName;
    @Transient
    private Long permissionSetId;
    @Transient
    private String menuLevel;
    @Transient
    private String menuType;

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(String menuLevel) {
        this.menuLevel = menuLevel;
    }

    public Long getPermissionCheckId() {
        return permissionCheckId;
    }

    public void setPermissionCheckId(Long permissionCheckId) {
        this.permissionCheckId = permissionCheckId;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
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

    public String getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(String fdLevel) {
        this.fdLevel = fdLevel;
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

    public String getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(String userDetails) {
        this.userDetails = userDetails;
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

    public String getApiMethodMeaning() {
        return apiMethodMeaning;
    }

    public void setApiMethodMeaning(String apiMethodMeaning) {
        this.apiMethodMeaning = apiMethodMeaning;
    }

    public String getCheckStateMeaning() {
        return checkStateMeaning;
    }

    public void setCheckStateMeaning(String checkStateMeaning) {
        this.checkStateMeaning = checkStateMeaning;
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    public String getHandleStatusMeaning() {
        return handleStatusMeaning;
    }

    public void setHandleStatusMeaning(String handleStatusMeaning) {
        this.handleStatusMeaning = handleStatusMeaning;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Long getPermissionSetId() {
        return permissionSetId;
    }

    public void setPermissionSetId(Long permissionSetId) {
        this.permissionSetId = permissionSetId;
    }

}
