package io.choerodon.core.oauth;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * 定制的clientDetail对象，添加了组织ID
 *
 * @author wuguokai
 */
public class CustomClientDetails extends BaseClientDetails {
    private static final long serialVersionUID = 4647677232738772939L;
    /**
     * 兼容猪齿鱼魔改之后的ID
     */
    private Long id;

    private Long organizationId;

    /**
     * currentRoleId : 当前角色Id
     */
    private Long currentRoleId;

    /**
     * currentTenantId : 当前租户，如果切换租户，该ID为切换租户后的租户ID
     */
    private Long currentTenantId;

    /**
     * roleIds : 当前可选角色ID
     */
    private List<Long> roleIds;

    /**
     * tenantIds: 当前可选租户
     */
    private List<Long> tenantIds;

    /**
     * 时区
     */
    private String timeZone;

    /**
     * 接口加密标识
     */
    private Integer apiEncryptFlag;
    /**
     * api防重放标识
     */
    private Integer apiReplayFlag;

    public Long getId() {
        return id;
    }

    public CustomClientDetails setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public List<Long> getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(List<Long> tenantIds) {
        this.tenantIds = tenantIds;
    }

    public Long getCurrentRoleId() {
        return currentRoleId;
    }

    public CustomClientDetails setCurrentRoleId(Long currentRoleId) {
        this.currentRoleId = currentRoleId;
        return this;
    }

    public Long getCurrentTenantId() {
        return currentTenantId;
    }

    public CustomClientDetails setCurrentTenantId(Long currentTenantId) {
        this.currentTenantId = currentTenantId;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public CustomClientDetails setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Integer getApiEncryptFlag() {
        return apiEncryptFlag;
    }

    public CustomClientDetails setApiEncryptFlag(Integer apiEncryptFlag) {
        this.apiEncryptFlag = apiEncryptFlag;
        return this;
    }

    public Integer getApiReplayFlag() {
        return apiReplayFlag;
    }

    public CustomClientDetails setApiReplayFlag(Integer apiReplayFlag) {
        this.apiReplayFlag = apiReplayFlag;
        return this;
    }

    public String toJSONString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomClientDetails that = (CustomClientDetails) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(currentRoleId, that.currentRoleId) &&
                Objects.equals(currentTenantId, that.currentTenantId) &&
                Objects.equals(roleIds, that.roleIds) &&
                Objects.equals(tenantIds, that.tenantIds) &&
                Objects.equals(timeZone, that.timeZone)&&
                Objects.equals(apiEncryptFlag, that.apiEncryptFlag) &&
                Objects.equals(apiReplayFlag, that.apiReplayFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, organizationId, currentRoleId, currentTenantId, roleIds, tenantIds, timeZone, apiEncryptFlag, apiReplayFlag);
    }

    @Override
    public String toString() {
        return "CustomClientDetails{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", currentRoleId=" + currentRoleId +
                ", currentTenantId=" + currentTenantId +
                ", roleIds=" + roleIds +
                ", tenantIds=" + tenantIds +
                ", timeZone='" + timeZone + '\'' +
                ", apiEncryptFlag='" + apiEncryptFlag +
                ", apiReplayFlag='" + apiReplayFlag +
                '}';
    }
}
