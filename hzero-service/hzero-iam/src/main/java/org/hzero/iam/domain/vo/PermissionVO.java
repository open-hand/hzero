package org.hzero.iam.domain.vo;

import java.util.Map;
import java.util.Set;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 权限VO
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/21 20:35
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionVO implements SecurityToken {

    @Encrypt
    private Long id;
    private String code;
    private String path;
    @LovValue(value = "HIAM.REQUEST_METHOD", meaningField = "methodMeaning")
    private String method;
    @LovValue(value = "HIAM.PERMISSION_LEVEL", meaningField = "levelMeaning")
    private String fdLevel;
    private String description;
    private String action;
    private String fdResource;
    private Boolean publicAccess;
    private Boolean loginAccess;
    private Boolean signAccess;
    private String serviceName;
    private Boolean isWithin;
    private String tag;
    private Long objectVersionNumber;

    private String createFlag;
    private String inheritFlag;
    @Encrypt
    private Long roleId;


    //非数据字段

    private String methodMeaning;
    private String levelMeaning;
    private Long tenantId;
    private String tenantName;
    private String _token;
    private Map<String, String[]> apiTags;
    /**
     * 查询条件：标签s
     */
    private Set<String> labels;

    public Map<String, String[]> getApiTags() {
        return apiTags;
    }

    public void setApiTags(Map<String, String[]> apiTags) {
        this.apiTags = apiTags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }



    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public Boolean getLoginAccess() {
        return loginAccess;
    }

    public void setLoginAccess(Boolean loginAccess) {
        this.loginAccess = loginAccess;
    }

    public Boolean getSignAccess() {
        return signAccess;
    }

    public void setSignAccess(Boolean signAccess) {
        this.signAccess = signAccess;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getCreateFlag() {
        return createFlag;
    }

    public void setCreateFlag(String createFlag) {
        this.createFlag = createFlag;
    }

    public String getInheritFlag() {
        return inheritFlag;
    }

    public void setInheritFlag(String inheritFlag) {
        this.inheritFlag = inheritFlag;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getMethodMeaning() {
        return methodMeaning;
    }

    public void setMethodMeaning(String methodMeaning) {
        this.methodMeaning = methodMeaning;
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(String fdLevel) {
        this.fdLevel = fdLevel;
    }

    public String getFdResource() {
        return fdResource;
    }

    public void setFdResource(String fdResource) {
        this.fdResource = fdResource;
    }

    public Boolean getWithin() {
        return isWithin;
    }

    public void setWithin(Boolean within) {
        isWithin = within;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }


    @Override
    public String get_token() {
        return _token;
    }

    @Override
    public void set_token(String tokenValue) {
        this._token = tokenValue;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Permission.class;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
}
