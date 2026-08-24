package org.hzero.iam.api.dto;

import java.util.List;
import java.util.Set;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author mingwei.liu@hand-china.com 2018/8/16
 */
public class MenuSearchDTO {

    //
    // 菜单控制参数
    // ------------------------------------------------------------------------------
    @Encrypt
    private Long id;
    private String code;
    private String name;
    private String quickIndex;
    private String level;
    private Long parentId;
    private String lang;

    @Transient
    private String parentCode;
    @Transient
    private String parentName;

    /**
     * 1、root 根目录 2、dir 目录 3、menu 菜单
     */
    private String type;
    private Boolean isDefault;
    private String route;
    private Boolean enabledFlag;
    private String description;

    @NotNull(message = "tenant id cannot be null")
    private Long tenantId;

    //
    // 权限测试控制参数
    // ------------------------------------------------------------------------------
    private Long roleId;
    private String memberType;
    private Long memberId;
    private String sourceType;
    private Long sourceId;

    //
    // 其他
    // ------------------------------------------------------------------------------
    /**
     * 范围, STANDARD/CUSTOM/BOTH, 仅查询标准菜单/仅查询租户客户化菜单/二者同时查询
     */
    @Pattern(regexp = "BOTH|STANDARD|CUSTOM", message = "scope must be one of BOTH|STANDARD|CUSTOM")
    private String scope;
    /**
     * 是否是菜单管理: 是-菜单管理, 否-菜单展示
     */
    private Boolean manageFlag;

    /**
     * 查询的层级
     */
    @JsonIgnore
    private String queryLevel = HiamResourceLevel.SITE.value();
    /**
     * 是否有查询条件
     */
    @JsonIgnore
    private boolean queryParam;
    /**
     * 查询菜单ID参数
     */
    private List<Long> menuIds;

    /**
     * 标签ID参数
     */
    private Set<String> labels;

    public void setupSiteQueryLevel() {
        this.queryLevel = HiamResourceLevel.SITE.value();
    }

    public void setupOrganizationQueryLevel() {
        this.queryLevel = HiamResourceLevel.ORGANIZATION.value();
    }

    public void setupQueryParam() {
        this.queryParam = !StringUtils.isAllBlank(this.code, this.name, this.quickIndex, this.description, this.parentName, this.parentCode);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public void setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getManageFlag() {
        return manageFlag;
    }

    public void setManageFlag(Boolean manageFlag) {
        this.manageFlag = manageFlag;
    }

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }

    public String getQueryLevel() {
        return queryLevel;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public boolean isQueryParam() {
        return queryParam;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
}
