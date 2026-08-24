package org.hzero.iam.domain.entity;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections4.CollectionUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.common.HZeroConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.Regexs;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.LabelAssignType;
import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 增加了继承角色ID、父级三维角色定位字段。
 *
 * @author bojiangzhou 2018/07/04
 */
@ModifyAudit
@VersionAudit
@MultiLanguage
@Table(name = "iam_role")
public class Role extends AuditDomain {

    public static final String ENCRYPT_KEY = "iam_role";

    public static final String FIELD_ID = "id";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_LEVEL = "level";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LEVEL_PATH = "levelPath";
    public static final String FIELD_INHERIT_LEVEL_PATH = "inheritLevelPath";
    public static final String FIELD_IS_ENABLED = "isEnabled";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PARENT_ROLE_ID = "parentRoleId";
    public static final String FIELD_INHERIT_ROLE_ID = "inheritRoleId";
    public static final String FIELD_PARENT_ROLE_ASSIGN_LEVEL = "parentRoleAssignLevel";
    public static final String FIELD_PARENT_ROLE_ASSIGN_LEVEL_VALUE = "parentRoleAssignLevelValue";
    public static final String FIELD_BUILD_IN = "isBuiltIn";
    public static final String FIELD_CREATED_BY_TENANT_ID = "createdByTenantId";
    public static final String FIELD_TPL_ROLE_NAME = "tpl_role_name";

    public static final Long ROOT_ID = 0L;
    public static final String LEVEL_PATH_SEPARATOR = BaseConstants.PATH_SEPARATOR;

    // 如下常量在 SQL 中有用到
    public static final String LABEL_DATA_TYPE = "ROLE";
    public static final String LABEL_TENANT_ADMIN = "TENANT_ADMIN";
    // 租户模板标签
    public static final String LABEL_TENANT_ROLE_TPL = "TENANT_ROLE_TPL";

    // 模板角色标签
    public static final String LABEL_ROLE_TPL = "_ROLE_TPL";
    public static final String LABEL_ROLE_TPL_LIKE = "%" + LABEL_ROLE_TPL;

    public static final String SUPER_SITE_ROLE = HZeroConstant.RoleCode.SITE;
    public static final String SUPER_TENANT_ROLE = HZeroConstant.RoleCode.TENANT;
    public static final String TENANT_ROLE_TPL = HZeroConstant.RoleCode.TENANT_TEMPLATE;


    @Id
    @Where
    @GeneratedValue
    @Encrypt
    private Long id;
    @MultiLanguageField
    @NotEmpty
    private String name;
    @NotEmpty
    @Pattern(regexp = Regexs.CODE_LOWER)
    @Where
    private String code;
    private String description;
    @Column(name = "fd_level")
    private String level;
    private Boolean isEnabled;

    //
    // getter/setter
    // ------------------------------------------------------------------------------
    private Boolean isModified;
    private Boolean isEnableForbidden;
    private Boolean isBuiltIn;
    private Boolean isAssignable;
    private Long objectVersionNumber;
    @Column(name = "h_tenant_id")
    @MultiLanguageField
    private Long tenantId;
    @Column(name = "h_inherit_role_id")
    @Where
    @Encrypt
    private Long inheritRoleId;
    @Column(name = "h_parent_role_id")
    @Where
    @Encrypt
    private Long parentRoleId;
    @Column(name = "h_parent_role_assign_level")
    private String parentRoleAssignLevel;
    @Column(name = "h_parent_role_assign_level_val")
    private Long parentRoleAssignLevelValue;
    /**
     * 父子层级关系
     */
    @Column(name = "h_level_path")
    @Where
    private String levelPath;
    /**
     * 继承层级关系
     */
    @Column(name = "h_inherit_level_path")
    private String inheritLevelPath;
    /**
     * 创建者租户ID
     */
    private Long createdByTenantId;
    /**
     * 模板角色的子角色名称
     */
    @MultiLanguageField
    private String tplRoleName;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    /**
     * 角色权限集关系
     */
    @Transient
    private List<RolePermission> permissionSets = new ArrayList<>();
    @Transient
    @Encrypt
    private Long copyFromRoleId;
    @Transient
    private String tenantNum;
    @Transient
    private String createdByTenantNum;

    @Transient
    @JsonIgnore
    private Role parentRole;
    @Transient
    @JsonIgnore
    private List<Role> createdSubRoles;
    @Transient
    @JsonIgnore
    private Role inheritRole;
    @Transient
    @JsonIgnore
    private Role copyRole;
    @Transient
    @JsonIgnore
    private List<Role> inheritSubRoles;
    @Transient
    private List<Label> roleLabels;
    @Transient
    @JsonIgnore
    private LabelAssignType labelAssignType;

    public static void setupRoleControlFlag(List<RoleVO> roles, List<Long> allManageRoleIds,
                                            List<MemberRole> cantRemoveRoles, Long memberId, CustomUserDetails self) {
        Set<Long> cantRemoveRoleIds = cantRemoveRoles.stream().filter(item -> memberId.equals(item.getMemberId()))
                .map(MemberRole::getRoleId).collect(Collectors.toSet());
        boolean isCurrentMemberUser = self.getUserId().equals(memberId);
        for (RoleVO role : roles) {
            if (!allManageRoleIds.contains(role.getId())) {
                role.setManageableFlag(BaseConstants.Flag.NO);
                role.setRemovableFlag(BaseConstants.Flag.NO);
                // 您对此角色无分配权限，禁止删除
                role.setTipMessage(MessageAccessor.getMessage("hiam.info.role.denyOperateNoManageableRole").desc());
            }

            if (cantRemoveRoleIds.contains(role.getId())) {
                role.setRemovableFlag(BaseConstants.Flag.NO);
                if (isCurrentMemberUser) {
                    // 您不能删除分配给自己的顶层角色
                    role.setTipMessage(MessageAccessor.getMessage("hiam.info.role.denyOperateSelfTopRole").desc());
                } else {
                    // 您对此角色无分配权限，禁止删除
                    role.setTipMessage(MessageAccessor.getMessage("hiam.info.role.denyOperateNoManageableRole").desc());
                }
            }
        }
    }

    public static Set<Long> filterTopRoleIds(List<RoleVO> roles) {
        return filterTopRoles(roles).stream().map(RoleVO::getId).collect(Collectors.toSet());
    }

    public static List<RoleVO> filterTopRoles(List<RoleVO> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        Set<String> levelPaths = roles.stream().map(RoleVO::getLevelPath).collect(Collectors.toSet());

        List<RoleVO> topRole = new ArrayList<>(roles.size());
        for (RoleVO role : roles) {
            String levelPath = role.getLevelPath();
            boolean isTop = levelPaths.stream().filter(path -> !levelPath.equals(path)).noneMatch(path -> levelPath.startsWith(path + "|"));
            if (isTop) {
                topRole.add(role);
            }
        }

        return topRole;
    }

    /**
     * 设置父级分配层级 <br>
     * <p>
     * Note: 分配层级和分配层级值已经没有实际意义，只是为了兼容而保留
     */
    public void setupParentAssignLevel(Role parentRole) {
        // 分配层级 固定为 租户层
        this.parentRoleAssignLevel = HiamResourceLevel.ORGANIZATION.value();
        // 分配层级值 固定为 父级角色的租户ID
        this.parentRoleAssignLevelValue = parentRole.getTenantId();
    }

    /**
     * 构建 levelPath
     *
     * @param parentRole 父级角色
     */
    public void buildCreatedRoleLevelPath(Role parentRole) {
        if (Boolean.TRUE.equals(this.isBuiltIn) && parentRole == null) {
            this.levelPath = code;
            return;
        }
        if (parentRole == null) { // 非内置角色正常来说必定会有父级角色
            this.levelPath = this.code;
        } else {
            String path = generatePath();
            this.levelPath = String.format("%s%s%s", parentRole.getLevelPath(), LEVEL_PATH_SEPARATOR, path);
        }
    }

    /**
     * 构建 inheritLevelPath
     *
     * @param inheritRole 继承角色
     */
    public void buildInheritRoleLevelPath(Role inheritRole) {
        if (Boolean.TRUE.equals(this.isBuiltIn) && inheritRole == null) {
            this.inheritLevelPath = code;
            return;
        }
        String path = generatePath();
        if (inheritRole != null) {
            this.inheritLevelPath = String.format("%s%s%s", inheritRole.getInheritLevelPath(), LEVEL_PATH_SEPARATOR, path);
        } else {
            this.inheritLevelPath = path;
        }
    }

    /**
     * path 规则：角色租户编码/创建者租户编码/当前角色代码
     */
    private String generatePath() {
        String createdByFlag = getCreatedByTenantId() == null ? "null" : Optional.ofNullable(getCreatedByTenantNum()).orElse(getCreatedByTenantId().toString());
        return String.format("%s.%s.%s", getTenantNum(), createdByFlag, getCode());
    }

    /**
     * 初始化超级管理员角色的路径
     */
    public void initSupperRoleLevelPath() {
        this.levelPath = this.code;
        this.inheritLevelPath = this.code;
    }

    public List<Label> getRoleLabels() {
        return roleLabels;
    }

    public void setRoleLabels(List<Label> roleLabels) {
        this.roleLabels = roleLabels;
    }

    public LabelAssignType getLabelAssignType() {
        return labelAssignType;
    }

    public void setLabelAssignType(LabelAssignType labelAssignType) {
        this.labelAssignType = labelAssignType;
    }

    public Long getId() {
        return id;
    }

    public Role setId(Long id) {
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

    public Role setCode(String code) {
        this.code = code;
        return this;
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

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Role setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
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

    public void setParentRoleId(Long parentRoleId) {
        this.parentRoleId = parentRoleId;
    }

    public String getParentRoleAssignLevel() {
        return parentRoleAssignLevel;
    }

    public void setParentRoleAssignLevel(String parentRoleAssignLevel) {
        this.parentRoleAssignLevel = parentRoleAssignLevel;
    }

    public Long getParentRoleAssignLevelValue() {
        return parentRoleAssignLevelValue;
    }

    public void setParentRoleAssignLevelValue(Long parentRoleAssignLevelValue) {
        this.parentRoleAssignLevelValue = parentRoleAssignLevelValue;
    }

    public List<RolePermission> getPermissionSets() {
        return permissionSets;
    }

    public void setPermissionSets(List<RolePermission> permissionSets) {
        this.permissionSets = permissionSets;
    }

    public Long getCopyFromRoleId() {
        return copyFromRoleId;
    }

    public void setCopyFromRoleId(Long copyFromRoleId) {
        this.copyFromRoleId = copyFromRoleId;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public Role setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public String getInheritLevelPath() {
        return inheritLevelPath;
    }

    public void setInheritLevelPath(String inheritLevelPath) {
        this.inheritLevelPath = inheritLevelPath;
    }

    public Long getCreatedByTenantId() {
        return createdByTenantId;
    }

    public void setCreatedByTenantId(Long createdByTenantId) {
        this.createdByTenantId = createdByTenantId;
    }

    public String getTplRoleName() {
        return tplRoleName;
    }

    public void setTplRoleName(String tplRoleName) {
        this.tplRoleName = tplRoleName;
    }

    public Role getParentRole() {
        return parentRole;
    }

    public void setParentRole(Role parentRole) {
        this.parentRole = parentRole;
    }

    public List<Role> getCreatedSubRoles() {
        return createdSubRoles;
    }

    public void setCreatedSubRoles(List<Role> createdSubRoles) {
        this.createdSubRoles = createdSubRoles;
    }

    public Role getInheritRole() {
        return inheritRole;
    }

    public void setInheritRole(Role inheritRole) {
        this.inheritRole = inheritRole;
    }

    public Role getCopyRole() {
        return copyRole;
    }

    public void setCopyRole(Role copyRole) {
        this.copyRole = copyRole;
    }

    public List<Role> getInheritSubRoles() {
        return inheritSubRoles;
    }

    public void setInheritSubRoles(List<Role> inheritSubRoles) {
        this.inheritSubRoles = inheritSubRoles;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getCreatedByTenantNum() {
        return createdByTenantNum;
    }

    public void setCreatedByTenantNum(String createdByTenantNum) {
        this.createdByTenantNum = createdByTenantNum;
    }

    @Override
    public String toString() {
        return "Role{" +
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
                ", objectVersionNumber=" + objectVersionNumber +
                ", tenantId=" + tenantId +
                ", inheritRoleId=" + inheritRoleId +
                ", parentRoleId=" + parentRoleId +
                ", parentRoleAssignLevel='" + parentRoleAssignLevel + '\'' +
                ", parentRoleAssignLevelValue=" + parentRoleAssignLevelValue +
                ", levelPath='" + levelPath + '\'' +
                ", inheritLevelPath='" + inheritLevelPath + '\'' +
                ", copyFromRoleId=" + copyFromRoleId +
                ", roleLabels=" + roleLabels +
                '}';
    }
}
