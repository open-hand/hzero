package org.hzero.iam.api.dto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.cache.CacheValue;
import org.hzero.core.cache.Cacheable;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jianbo.li
 * @date 2019/8/12 9:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO extends Child<RoleDTO> implements SecurityToken, Cacheable {
    @Encrypt
    private Long id;
    private String name;
    private String code;
    private String description;
    @LovValue("HIAM.RESOURCE_LEVEL")
    private String level;
    private Boolean isEnabled;
    private Boolean isModified;
    private Boolean isEnableForbidden;
    private Boolean isBuiltIn;
    private Boolean isAssignable;
    private Long tenantId;
    @Encrypt
    private Long inheritRoleId;
    @Encrypt
    private Long parentRoleId;
    private String parentRoleAssignLevel;
    private Long parentRoleAssignLevelValue;
    private Long createdBy;
    private Date creationDate;
    private Long objectVersionNumber;
    private String viewCode;
    private String levelMeaning;
    private String inheritedRoleName;
    private String parentRoleName;
    private String tenantName;
    @LovValue("HIAM.ROLE_SOURCE")
    private String roleSource;
    @Encrypt
    private Long defaultRoleId;
    /**
     * 角色标签集合
     */
    private List<Label> labels;
    /**
     * 是否为默认角色
     */
    private Boolean isDefaultRole;
    /**
     * 创建者名称
     */
    @CacheValue(
            key = HZeroCacheKey.USER,
            primaryKey = "createdBy",
            searchKey = "realName",
            structure = CacheValue.DataStructure.MAP_OBJECT
    )
    private String createdUserName;
    //
    // member_role 相关
    // ------------------------------------------------------------------------------
    @Encrypt
    private Long memberId;
    private String memberType;
    private Long sourceId;
    private String sourceType;
    @LovValue("HIAM.RESOURCE_LEVEL")
    private String assignLevel;
    private String assignLevelMeaning;
    private Long assignLevelValue;
    private String assignLevelValueMeaning;
    private String roleSourceMeaning;
    private List<PermissionVO> permissions;
    private RoleVO inheritedRole;
    /**
     * 孩子数量
     */
    private Integer childrenNum;
    /**
     * 路径
     */
    private String levelPath;
    /**
     * 管理标识(可以创建、修改角色)
     */
    private Integer adminFlag = 0;
    /**
     * 分配标识
     */
    private Integer assignedFlag = 0;

    //
    // 管理角色相关
    // ------------------------------------------------------------------------------
    /**
     * 是否有父级管理角色
     */
    private Integer haveAdminFlag = 0;
    //
    // 父级管理角色相关
    // ------------------------------------------------------------------------------
    @Encrypt
    private Long adminRoleId;
    private String adminRoleCode;
    private String adminRoleName;
    private String adminRoleLevel;
    private Long adminRoleTenantId;
    private String adminRoleTenantNum;
    private String adminRoleTenantName;
    // 父级角色分配组织ID
    @Encrypt
    private Long parentRoleAssignUnitId;
    // 父级角色分配组织名称
    private String parentRoleAssignUnitName;
    // 是否在管理范围内
    private Integer manageableFlag = 0;
    /**
     * 当前用户
     */
    @JsonIgnore
    private Long userId;
    /**
     * 查询参数:
     * 排除角色ID列表
     * 排除用户已分配角色, 用户ID列表
     */
    private List<Long> excludeRoleIds;
    private List<Long> excludeUserIds;
    // 排除的用户ID
    @Encrypt
    private Long excludeUserId;
    // 查询可分配的角色
    private boolean selectAssignedRoleFlag = false;
    /**
     * token
     */
    private String _token;
    /**
     * 用于按照分配先后次序排序
     */
    @Encrypt
    private Long memberRoleId;

    /**
     * 继承角色的租户名称
     */
    private String inheritedRoleTenantName;
    /**
     * 父级角色的租户名称
     */
    private String parentRoleTenantName;
    /**
     * 是否可删除，为 1 时可删除，null 或者 0 不可删除
     */
    private Integer removableFlag;

    public RoleDTO() {
    }

    public RoleDTO(Long id) {
        this.id = id;
    }

    /**
     * 实体转树形node
     *
     * @param roleVOList
     * @return
     */
    public static List<RoleDTO> convertEntityToTreeDTO(List<RoleVO> roleVOList) {
        if (CollectionUtils.isEmpty(roleVOList)) {
            return Collections.emptyList();
        }
        return roleVOList.stream()
                .map(item -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(item, roleDTO);
                    return roleDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Role.class;
    }

    public String getViewCode() {
        if (StringUtils.isNotBlank(code)) {
            String[] codeArr = StringUtils.split(code, BaseConstants.Symbol.SLASH);
            this.viewCode = codeArr[codeArr.length - 1];
        }
        return viewCode;
    }

    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public Long getId() {
        return id;
    }

    public RoleDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getEnabled() {
        return isEnabled != null ? isEnabled : false;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Boolean getModified() {
        return isModified;
    }

    public void setModified(Boolean modified) {
        isModified = modified;
    }

    public Boolean getEnableForbidden() {
        return isEnableForbidden;
    }

    public void setEnableForbidden(Boolean enableForbidden) {
        isEnableForbidden = enableForbidden;
    }

    public Boolean getBuiltIn() {
        return isBuiltIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        isBuiltIn = builtIn;
    }

    public Boolean getAssignable() {
        return isAssignable;
    }

    public void setAssignable(Boolean assignable) {
        isAssignable = assignable;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getInheritRoleId() {
        return inheritRoleId;
    }

    public void setInheritRoleId(Long inheritRoleId) {
        this.inheritRoleId = inheritRoleId;
    }

    public Long getParentRoleId() {
        return parentRoleId;
    }

    public RoleDTO setParentRoleId(Long parentRoleId) {
        this.parentRoleId = parentRoleId;
        return this;
    }

    public String getParentRoleAssignLevel() {
        return parentRoleAssignLevel;
    }

    public RoleDTO setParentRoleAssignLevel(String parentRoleAssignLevel) {
        this.parentRoleAssignLevel = parentRoleAssignLevel;
        return this;
    }

    public Long getParentRoleAssignLevelValue() {
        return parentRoleAssignLevelValue;
    }

    public RoleDTO setParentRoleAssignLevelValue(Long parentRoleAssignLevelValue) {
        this.parentRoleAssignLevelValue = parentRoleAssignLevelValue;
        return this;
    }

    public String getAssignLevelMeaning() {
        return assignLevelMeaning;
    }

    public void setAssignLevelMeaning(String assignLevelMeaning) {
        this.assignLevelMeaning = assignLevelMeaning;
    }

    public String getAssignLevelValueMeaning() {
        return assignLevelValueMeaning;
    }

    public void setAssignLevelValueMeaning(String assignLevelValueMeaning) {
        this.assignLevelValueMeaning = assignLevelValueMeaning;
    }

    public String getRoleSource() {
        return this.roleSource;
    }

    public void setRoleSource(String roleSource) {
        this.roleSource = roleSource;
    }

    /**
     * @return 层级含义
     */
    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    /**
     * @return 继承的角色名称
     */
    public String getInheritedRoleName() {
        return inheritedRoleName;
    }

    public void setInheritedRoleName(String inheritedRoleName) {
        this.inheritedRoleName = inheritedRoleName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public RoleDTO setMemberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    /**
     * @return 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getAssignLevel() {
        return assignLevel;
    }

    public void setAssignLevel(String assignLevel) {
        this.assignLevel = assignLevel;
    }

    public Long getAssignLevelValue() {
        return assignLevelValue;
    }

    public void setAssignLevelValue(Long assignLevelValue) {
        this.assignLevelValue = assignLevelValue;
    }

    public List<PermissionVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionVO> permissions) {
        this.permissions = permissions;
    }

    /**
     * @return 继承的角色
     */
    public RoleVO getInheritedRole() {
        return inheritedRole;
    }

    public void setInheritedRole(RoleVO inheritedRole) {
        this.inheritedRole = inheritedRole;
    }

    public String getParentRoleName() {
        return parentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }

    public List<Long> getExcludeRoleIds() {
        return excludeRoleIds;
    }

    public void setExcludeRoleIds(List<Long> excludeRoleIds) {
        this.excludeRoleIds = excludeRoleIds;
    }

    public List<Long> getExcludeUserIds() {
        return excludeUserIds;
    }

    public void setExcludeUserIds(List<Long> excludeUserIds) {
        this.excludeUserIds = excludeUserIds;
    }

    public Long getExcludeUserId() {
        return excludeUserId;
    }

    public void setExcludeUserId(Long excludeUserId) {
        this.excludeUserId = excludeUserId;
    }

    public Long getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public Long getDefaultRoleId() {
        return defaultRoleId;
    }

    public void setDefaultRoleId(Long defaultRoleId) {
        this.defaultRoleId = defaultRoleId;
    }

    public Boolean getDefaultRole() {
        return isDefaultRole;
    }

    public void setDefaultRole(Boolean defaultRole) {
        isDefaultRole = defaultRole;
    }

    public Integer getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Integer adminFlag) {
        this.adminFlag = adminFlag;
    }

    public Integer getAssignedFlag() {
        return assignedFlag;
    }

    public void setAssignedFlag(Integer assignedFlag) {
        this.assignedFlag = assignedFlag;
    }

    public Integer getHaveAdminFlag() {
        return haveAdminFlag;
    }

    public void setHaveAdminFlag(Integer haveAdminFlag) {
        this.haveAdminFlag = haveAdminFlag;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleSourceMeaning() {
        return roleSourceMeaning;
    }

    public void setRoleSourceMeaning(String roleSourceMeaning) {
        this.roleSourceMeaning = roleSourceMeaning;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    public void setAdminRoleId(Long adminRoleId) {
        this.adminRoleId = adminRoleId;
    }

    public String getAdminRoleCode() {
        return adminRoleCode;
    }

    public void setAdminRoleCode(String adminRoleCode) {
        this.adminRoleCode = adminRoleCode;
    }

    public String getAdminRoleName() {
        return adminRoleName;
    }

    public void setAdminRoleName(String adminRoleName) {
        this.adminRoleName = adminRoleName;
    }

    public String getAdminRoleLevel() {
        return adminRoleLevel;
    }

    public void setAdminRoleLevel(String adminRoleLevel) {
        this.adminRoleLevel = adminRoleLevel;
    }

    public Long getAdminRoleTenantId() {
        return adminRoleTenantId;
    }

    public void setAdminRoleTenantId(Long adminRoleTenantId) {
        this.adminRoleTenantId = adminRoleTenantId;
    }

    public String getAdminRoleTenantNum() {
        return adminRoleTenantNum;
    }

    public void setAdminRoleTenantNum(String adminRoleTenantNum) {
        this.adminRoleTenantNum = adminRoleTenantNum;
    }

    public String getAdminRoleTenantName() {
        return adminRoleTenantName;
    }

    public void setAdminRoleTenantName(String adminRoleTenantName) {
        this.adminRoleTenantName = adminRoleTenantName;
    }

    public Long getParentRoleAssignUnitId() {
        return parentRoleAssignUnitId;
    }

    public void setParentRoleAssignUnitId(Long parentRoleAssignUnitId) {
        this.parentRoleAssignUnitId = parentRoleAssignUnitId;
    }

    public String getParentRoleAssignUnitName() {
        return parentRoleAssignUnitName;
    }

    public void setParentRoleAssignUnitName(String parentRoleAssignUnitName) {
        this.parentRoleAssignUnitName = parentRoleAssignUnitName;
    }

    public boolean isSelectAssignedRoleFlag() {
        return selectAssignedRoleFlag;
    }

    public void setSelectAssignedRoleFlag(boolean selectAssignedRoleFlag) {
        this.selectAssignedRoleFlag = selectAssignedRoleFlag;
    }

    public Integer getManageableFlag() {
        return manageableFlag;
    }

    public void setManageableFlag(Integer manageableFlag) {
        this.manageableFlag = manageableFlag;
    }


    public Integer getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(Integer childrenNum) {
        this.childrenNum = childrenNum;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    @Override
    public String get_token() {
        return _token;
    }

    @Override
    public void set_token(String tokenValue) {
        this._token = tokenValue;
    }

    public String getInheritedRoleTenantName() {
        return inheritedRoleTenantName;
    }

    public void setInheritedRoleTenantName(String inheritedRoleTenantName) {
        this.inheritedRoleTenantName = inheritedRoleTenantName;
    }

    public String getParentRoleTenantName() {
        return parentRoleTenantName;
    }

    public void setParentRoleTenantName(String parentRoleTenantName) {
        this.parentRoleTenantName = parentRoleTenantName;
    }

    public Integer getRemovableFlag() {
        return removableFlag;
    }

    public void setRemovableFlag(Integer removableFlag) {
        this.removableFlag = removableFlag;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", level='" + level + '\'' +
                ", isEnabled=" + isEnabled +
                ", isModified=" + isModified +
                ", isEnableForbidden=" + isEnableForbidden +
                ", isBuiltIn=" + isBuiltIn +
                ", isAssignable=" + isAssignable +
                ", tenantId=" + tenantId +
                ", inheritRoleId=" + inheritRoleId +
                ", parentRoleId=" + parentRoleId +
                ", parentRoleAssignLevel='" + parentRoleAssignLevel + '\'' +
                ", parentRoleAssignLevelValue=" + parentRoleAssignLevelValue +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", objectVersionNumber=" + objectVersionNumber +
                ", viewCode='" + viewCode + '\'' +
                ", levelMeaning='" + levelMeaning + '\'' +
                ", inheritedRoleName='" + inheritedRoleName + '\'' +
                ", parentRoleName='" + parentRoleName + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", roleSource='" + roleSource + '\'' +
                ", defaultRoleId=" + defaultRoleId +
                ", isDefaultRole=" + isDefaultRole +
                ", createdUserName='" + createdUserName + '\'' +
                ", memberId=" + memberId +
                ", memberType='" + memberType + '\'' +
                ", sourceId=" + sourceId +
                ", sourceType='" + sourceType + '\'' +
                ", assignLevel='" + assignLevel + '\'' +
                ", assignLevelMeaning='" + assignLevelMeaning + '\'' +
                ", assignLevelValue=" + assignLevelValue +
                ", assignLevelValueMeaning='" + assignLevelValueMeaning + '\'' +
                ", roleSourceMeaning='" + roleSourceMeaning + '\'' +
                ", permissions=" + permissions +
                ", inheritedRole=" + inheritedRole +
                ", childrenNum=" + childrenNum +
                ", levelPath='" + levelPath + '\'' +
                ", adminFlag=" + adminFlag +
                ", assignedFlag=" + assignedFlag +
                ", haveAdminFlag=" + haveAdminFlag +
                ", adminRoleId=" + adminRoleId +
                ", adminRoleCode='" + adminRoleCode + '\'' +
                ", adminRoleName='" + adminRoleName + '\'' +
                ", adminRoleLevel='" + adminRoleLevel + '\'' +
                ", adminRoleTenantId=" + adminRoleTenantId +
                ", adminRoleTenantNum='" + adminRoleTenantNum + '\'' +
                ", adminRoleTenantName='" + adminRoleTenantName + '\'' +
                ", parentRoleAssignUnitId=" + parentRoleAssignUnitId +
                ", parentRoleAssignUnitName='" + parentRoleAssignUnitName + '\'' +
                ", manageableFlag=" + manageableFlag +
                ", userId=" + userId +
                ", excludeRoleIds=" + excludeRoleIds +
                ", excludeUserIds=" + excludeUserIds +
                ", excludeUserId=" + excludeUserId +
                ", selectAssignedRoleFlag=" + selectAssignedRoleFlag +
                ", _token='" + _token + '\'' +
                ", memberRoleId=" + memberRoleId +
                '}';
    }
}
