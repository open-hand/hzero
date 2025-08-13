package io.choerodon.core.oauth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hzero.core.user.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 定制的userDetail对象
 *
 * @author wuguokai
 * @author Eugen
 */
public class CustomUserDetails extends User implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
    private static final long serialVersionUID = -3762281463683847665L;

    private Long userId;

    private String realName;

    private String email;

    private String timeZone;

    private String language;

    private String userType;

    /**
     * roleId : 当前角色Id
     */
    private Long roleId;
    /**
     * 角色标签
     */
    private Set<String> roleLabels;

    /**
     * roleIds : 当前可选角色ID
     */
    private List<Long> roleIds;

    /**
     * 平台级可选角色 ID
     */
    private List<Long> siteRoleIds;

    /**
     * 租户级可选角色 ID
     */
    private List<Long> tenantRoleIds;

    /**
     * 角色合并标记
     */
    private Boolean roleMergeFlag;

    /**
     * tenantId : 当前租户，如果和用户切换租户，该ID为切换租户后的租户ID
     */
    private Long tenantId;
    /**
     * 当前租户的租户编码
     */
    private String tenantNum;
    /**
     * tenantIds: 当前可选租户
     */
    private List<Long> tenantIds;

    /**
     * 用户头像
     */
    private String imageUrl;

    /**
     * 用户所属租户，不会随着用户选择的租户变化
     */
    private Long organizationId;

    private Boolean isAdmin;

    private Long clientId;

    private String clientName;

    private Set<String> clientAuthorizedGrantTypes;

    private Set<String> clientResourceIds;

    private Set<String> clientScope;

    private Set<String> clientRegisteredRedirectUri;

    private Integer clientAccessTokenValiditySeconds;

    private Integer clientRefreshTokenValiditySeconds;

    private Set<String> clientAutoApproveScopes;

    private Map<String, Object> additionInfo;
    private Map<String, String> additionInfoMeaning;

    /**
     * 接口加密标识
     */
    private Integer apiEncryptFlag;
    /**
     * api防重放标识
     */
    private Integer apiReplayFlag;

    public CustomUserDetails(String username,
                             String password,
                             String userType,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userType = userType;
    }

    public CustomUserDetails(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this(username, password, UserType.DEFAULT_USER_TYPE, authorities);
    }

    public CustomUserDetails(@JsonProperty("username") String username,
                             @JsonProperty("password") String password) {
        this(username, password, UserType.DEFAULT_USER_TYPE, Collections.emptyList());
    }

    @JsonCreator
    public CustomUserDetails(@JsonProperty("username") String username,
                             @JsonProperty("password") String password,
                             @JsonProperty("userType") String userType) {
        this(username, password, userType, Collections.emptyList());
    }

    /**
     * @return 获取合并角色列表
     */
    public List<Long> roleMergeIds() {
        if (isRoleMergeFlag()) {
            if (!CollectionUtils.isEmpty(siteRoleIds) && siteRoleIds.contains(roleId)) {
                return siteRoleIds;
            }
            if (!CollectionUtils.isEmpty(tenantRoleIds) && tenantRoleIds.contains(roleId)) {
                return tenantRoleIds;
            }
            logger.error("The current role is not in any of the optional role lists");
        }
        return Collections.singletonList(roleId);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUserType() {
        return userType;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Set<String> getRoleLabels() {
        return roleLabels;
    }

    public CustomUserDetails setRoleLabels(Set<String> roleLabels) {
        this.roleLabels = roleLabels;
        return this;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public List<Long> getSiteRoleIds() {
        return siteRoleIds;
    }

    public CustomUserDetails setSiteRoleIds(List<Long> siteRoleIds) {
        this.siteRoleIds = siteRoleIds;
        return this;
    }

    public List<Long> getTenantRoleIds() {
        return tenantRoleIds;
    }

    public CustomUserDetails setTenantRoleIds(List<Long> tenantRoleIds) {
        this.tenantRoleIds = tenantRoleIds;
        return this;
    }

    public boolean isRoleMergeFlag() {
        return Boolean.TRUE.equals(roleMergeFlag);
    }

    public CustomUserDetails setRoleMergeFlag(Boolean roleMergeFlag) {
        this.roleMergeFlag = roleMergeFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public CustomUserDetails setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public CustomUserDetails setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
        return this;
    }

    public List<Long> getTenantIds() {
        return tenantIds;
    }

    public CustomUserDetails setTenantIds(List<Long> tenantIds) {
        this.tenantIds = tenantIds;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public CustomUserDetails setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public CustomUserDetails setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Map<String, Object> getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(Map<String, Object> additionInfo) {
        this.additionInfo = additionInfo;
    }

    public CustomUserDetails addAdditionInfo(String key, Object value) {
        if (this.additionInfo == null) {
            this.additionInfo = new HashMap<>();
        }
        this.additionInfo.put(key, value);
        return this;
    }

    public Map<String, String> getAdditionInfoMeaning() {
        return additionInfoMeaning;
    }

    public Object readAdditionInfo(String key) {
        if (this.additionInfo == null) {
            return null;
        }
        return this.additionInfo.get(key);
    }

    public CustomUserDetails addAdditionMeaning(String key, String meaning) {
        if (this.additionInfoMeaning == null) {
            this.additionInfoMeaning = new HashMap<>();
        }
        this.additionInfoMeaning.put(key, meaning);
        return this;
    }

    public CustomUserDetails setAdditionInfoMeaning(Map<String, String> additionInfoMeaning) {
        this.additionInfoMeaning = additionInfoMeaning;
        return this;
    }

    public String readAdditionInfoMeaning(String key) {
        if (this.additionInfoMeaning == null) {
            return null;
        }
        return this.additionInfoMeaning.get(key);
    }

    public CustomUserDetails removeAdditionInfo(String key) {
        if (this.additionInfo != null) {
            this.additionInfo.remove(key);
        }
        if (this.additionInfoMeaning != null) {
            this.additionInfoMeaning.remove(key);
        }
        return this;
    }

    public CustomUserDetails removeAdditionInfos(Collection<String> keys) {
        if (!CollectionUtils.isEmpty(keys)) {
            keys.forEach(this::removeAdditionInfo);
        }
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Set<String> getClientAuthorizedGrantTypes() {
        return clientAuthorizedGrantTypes;
    }

    public void setClientAuthorizedGrantTypes(Collection<String> clientAuthorizedGrantTypes) {
        this.clientAuthorizedGrantTypes = clientAuthorizedGrantTypes == null ? Collections
                .<String>emptySet() : new LinkedHashSet<String>(clientAuthorizedGrantTypes);
    }

    public Set<String> getClientResourceIds() {
        return clientResourceIds;
    }

    public void setClientResourceIds(Collection<String> clientResourceIds) {
        this.clientResourceIds = clientResourceIds == null ? Collections
                .<String>emptySet() : new LinkedHashSet<String>(clientResourceIds);
    }

    public Set<String> getClientScope() {
        return clientScope;
    }

    public void setClientScope(Collection<String> clientScope) {
        this.clientScope = clientScope == null ? Collections.<String>emptySet()
                : new LinkedHashSet<String>(clientScope);
    }

    public Set<String> getClientRegisteredRedirectUri() {
        return clientRegisteredRedirectUri;
    }

    public void setClientRegisteredRedirectUri(Collection<String> clientRegisteredRedirectUri) {
        this.clientRegisteredRedirectUri = clientRegisteredRedirectUri == null ? null
                : new LinkedHashSet<String>(clientRegisteredRedirectUri);
    }

    public Integer getClientAccessTokenValiditySeconds() {
        return clientAccessTokenValiditySeconds;
    }

    public void setClientAccessTokenValiditySeconds(Integer clientAccessTokenValiditySeconds) {
        this.clientAccessTokenValiditySeconds = clientAccessTokenValiditySeconds;
    }

    public Integer getClientRefreshTokenValiditySeconds() {
        return clientRefreshTokenValiditySeconds;
    }

    public void setClientRefreshTokenValiditySeconds(Integer clientRefreshTokenValiditySeconds) {
        this.clientRefreshTokenValiditySeconds = clientRefreshTokenValiditySeconds;
    }

    public Set<String> getClientAutoApproveScopes() {
        return clientAutoApproveScopes;
    }

    public void setClientAutoApproveScopes(Collection<String> clientAutoApproveScopes) {
        this.clientAutoApproveScopes = clientAutoApproveScopes == null ? null
                : new LinkedHashSet<String>(clientAutoApproveScopes);
    }

    public Integer getApiEncryptFlag() {
        return apiEncryptFlag;
    }

    public CustomUserDetails setApiEncryptFlag(Integer apiEncryptFlag) {
        this.apiEncryptFlag = apiEncryptFlag;
        return this;
    }

    public Integer getApiReplayFlag() {
        return apiReplayFlag;
    }

    public CustomUserDetails setApiReplayFlag(Integer apiReplayFlag) {
        this.apiReplayFlag = apiReplayFlag;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(realName, that.realName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(timeZone, that.timeZone) &&
                Objects.equals(language, that.language) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(roleIds, that.roleIds) &&
                Objects.equals(siteRoleIds, that.siteRoleIds) &&
                Objects.equals(tenantRoleIds, that.tenantRoleIds) &&
                Objects.equals(roleMergeFlag, that.roleMergeFlag) &&
                Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(tenantNum, that.tenantNum) &&
                Objects.equals(tenantIds, that.tenantIds) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(isAdmin, that.isAdmin) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(clientAuthorizedGrantTypes, that.clientAuthorizedGrantTypes) &&
                Objects.equals(clientResourceIds, that.clientResourceIds) &&
                Objects.equals(clientScope, that.clientScope) &&
                Objects.equals(clientRegisteredRedirectUri, that.clientRegisteredRedirectUri) &&
                Objects.equals(clientAccessTokenValiditySeconds, that.clientAccessTokenValiditySeconds) &&
                Objects.equals(clientRefreshTokenValiditySeconds, that.clientRefreshTokenValiditySeconds) &&
                Objects.equals(clientAutoApproveScopes, that.clientAutoApproveScopes) &&
                Objects.equals(additionInfo, that.additionInfo) &&
                Objects.equals(apiEncryptFlag, that.apiEncryptFlag) &&
                Objects.equals(apiReplayFlag, that.apiReplayFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, realName, email, timeZone, language, roleId, roleIds, siteRoleIds, tenantRoleIds, roleMergeFlag, tenantId, tenantNum, tenantIds, imageUrl, organizationId, isAdmin, clientId, clientName, clientAuthorizedGrantTypes, clientResourceIds, clientScope, clientRegisteredRedirectUri, clientAccessTokenValiditySeconds, clientRefreshTokenValiditySeconds, clientAutoApproveScopes, additionInfo, additionInfoMeaning, apiEncryptFlag, apiReplayFlag);
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "userId=" + userId +
                ", username=" + getUsername() +
                ", realName='" + realName + '\'' +
                ", email='" + email + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", language='" + language + '\'' +
                ", roleId=" + roleId +
                ", roleIds=" + roleIds +
                ", siteRoleIds=" + siteRoleIds +
                ", tenantRoleIds=" + tenantRoleIds +
                ", roleMergeFlag=" + roleMergeFlag +
                ", tenantId=" + tenantId +
                ", tenantNum='" + tenantNum + '\'' +
                ", tenantIds=" + tenantIds +
                ", imageUrl='" + imageUrl + '\'' +
                ", organizationId=" + organizationId +
                ", isAdmin=" + isAdmin +
                ", clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                ", clientAuthorizedGrantTypes=" + clientAuthorizedGrantTypes +
                ", clientResourceIds=" + clientResourceIds +
                ", clientScope=" + clientScope +
                ", clientRegisteredRedirectUri=" + clientRegisteredRedirectUri +
                ", clientAccessTokenValiditySeconds=" + clientAccessTokenValiditySeconds +
                ", clientRefreshTokenValiditySeconds=" + clientRefreshTokenValiditySeconds +
                ", clientAutoApproveScopes=" + clientAutoApproveScopes +
                ", additionInfo=" + additionInfo +
                ", apiEncryptFlag=" + apiEncryptFlag +
                ", apiReplayFlag=" + apiReplayFlag +
                ", roleLabels='" + roleLabels +
                '}';
    }

    public String toJSONString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    /**
     * @return 打印简单必要的用户信息
     */
    public String simpleUserInfo() {
        return "CustomUserDetails{" +
                "userId=" + userId +
                ", username=" + getUsername() +
                ", roleId=" + roleId +
                ", roleIds=" + roleIds +
                ", siteRoleIds=" + siteRoleIds +
                ", tenantRoleIds=" + tenantRoleIds +
                ", roleMergeFlag=" + roleMergeFlag +
                ", tenantId=" + tenantId +
                ", tenantIds=" + tenantIds +
                ", organizationId=" + organizationId +
                ", isAdmin=" + isAdmin +
                ", clientId=" + clientId +
                ", timeZone='" + timeZone +
                ", language='" + language +
                ", roleLabels='" + roleLabels +
                ", apiEncryptFlag=" + apiEncryptFlag +
                '}';
    }
}
