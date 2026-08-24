package org.hzero.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO {

    public static final RoleDTO EMPTY_ROLE = new RoleDTO();

    private Long id;
    private String name;
    private String code;
    private String description;
    private String level;
    private Boolean isEnabled;
    private Boolean isModified;
    private Boolean isEnableForbidden;
    private Boolean isBuiltIn;
    private Boolean isAssignable;
    private Long objectVersionNumber;

    private Long tenantId;
    private Long inheritRoleId;
    private Long parentRoleId;
    private String parentRoleAssignLevel;
    private Long parentRoleAssignLevelValue;

    private String viewCode;
    private String levelMeaning;
    private String inheritedRoleName;
    private String tenantName;
    private String roleSource;

    @JsonFormat(pattern = "")
    private Long memberId;
    private Long sourceId;
    private String sourceType;
    private String assignLevel;
    private Long assignLevelValue;
    private String assignLevelValueMeaning;

    private List<RoleDTO> childRoles;
    private RoleDTO inheritedRole;

    public String getViewCode() {
        if (StringUtils.isNotBlank(code)) {
            /**
             * Fix-20180829: 兼容之前的代码, 并处理更新之后的代码
             */
            if (code.contains("-(")) {
                /**
                 * 兼容之前的错误数据
                 */
                String tmp = StringUtils.substringBefore(code, "-(");
                String[] codeArr = StringUtils.split(tmp, "/");
                codeArr = ArrayUtils.subarray(codeArr, 3, codeArr.length);
                viewCode = StringUtils.join(codeArr, "/");
            } else if (code.contains("]/")) {
                /**
                 * 更新后的代码标识
                 */
                viewCode = StringUtils.substringAfter(code, "]/");
            } else {
                String[] codeArr = StringUtils.split(code, "/");
                codeArr = ArrayUtils.subarray(codeArr, 3, codeArr.length);
                viewCode = StringUtils.join(codeArr, "/");
            }
        }
        return viewCode;
    }

    public String getRoleSource() {
        /**
         * code为空时, 返回roleSource本身
         * mingwei.liu@hand-china.com on 2018/8/4 for query parameters
         *
         * 如果此RoleVO为查询参数时, 而恰好code也为参数, 例如, code="test",
         * 那么此处会报错:
         * org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.exceptions.PersistenceException:
         * Error querying database.  Cause: java.lang.ArrayIndexOutOfBoundsException: 2
         * ... RoleDTO.getRoleSource ...
         * 原因在于: mybatis mapper中使用了#{roleSource}查询参数导致报错。
         *
         * 不在此处转化, 将roleSource的取值放到mapper中
         * @since 2018/11/27
         * @author mingwei.liu@hand-china.com
         */
        // return Optional.ofNullable(code).map(c -> StringUtils.split(c, "/")[2]).orElse(roleSource);
        return this.roleSource;
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
        return isEnabled;
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

    public String getAssignLevelValueMeaning() {
        return assignLevelValueMeaning;
    }

    public void setAssignLevelValueMeaning(String assignLevelValueMeaning) {
        this.assignLevelValueMeaning = assignLevelValueMeaning;
    }

    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
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

    /**
     * @return 继承的角色
     */
    public RoleDTO getInheritedRole() {
        return inheritedRole;
    }

    public void setInheritedRole(RoleDTO inheritedRole) {
        this.inheritedRole = inheritedRole;
    }

    /**
     * @return 子角色
     */
    public List<RoleDTO> getChildRoles() {
        return childRoles;
    }

    public void setChildRoles(List<RoleDTO> childRoles) {
        this.childRoles = childRoles;
    }
}
