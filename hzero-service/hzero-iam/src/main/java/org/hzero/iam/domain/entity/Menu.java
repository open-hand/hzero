package org.hzero.iam.domain.entity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.Regexs;
import org.hzero.iam.infra.constant.HiamMenuType;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.util.ChineseUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author wuguokai
 * @author allen 2018/6/29
 */
@ModifyAudit
@VersionAudit
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "iam_menu")
public class Menu extends AuditDomain {

    public static final String ENCRYPT_KEY = "iam_menu";

    public static final String FIELD_ID = "id";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_QUICK_INDEX = "quickIndex";
    public static final String FIELD_LEVEL = "level";
    public static final String FIELD_PARENT_ID = "parentId";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_SORT = "sort";
    public static final String FIELD_IS_DEFAULT = "isDefault";
    public static final String FIELD_ICON = "icon";
    public static final String FIELD_ROUTE = "route";
    public static final String FIELD_CUSTOM_FLAG = "customFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LEVEL_PATH = "levelPath";
    public static final String FIELD_VIRTUAL_FLAG = "virtualFlag";
    public static final String FIELD_CONTROLLER_TYPE = "controllerType";
    public static final String FIELD_PERMISSION_TYPE = "permissionType";

    public static final String ROOT_LEVEL_PATH = "0";
    public static final Long ROOT_ID = 0L;
    public static final String PS_DEFAULT = ".ps.default";
    public static final String PS_DEFAULT_NAME = "hiam.info.menu.ps.defaultName";

    public static final ControllerType DEFAULT_CONTROLLER_TYPE = ControllerType.DISABLED;
    public static final String MENU_EXPORT_FILE_PREFIX = "menu_";
    public static final String MENU_EXPORT_FILE_TYPE = ".json";
    public static final String MENU_CODE_SPLIT = ".";


    public static final Set<String> MENU_DIR = ImmutableSet.of("root", "dir", "menu", "link", "inner-link", "window");
    public static final Set<String> MENU_PS = ImmutableSet.of("root", "dir", "menu", "link", "inner-link", "window", "ps");
    /**
     * 菜单标签数据类型
     */
    public static final String LABEL_DATA_TYPE = "MENU";

    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @NotNull(message = "error.menuCode.null", groups = Valid.class)
    @Pattern(regexp = Regexs.CODE, message = "error.menu.code.illegal", groups = Valid.class)
    @Length(max = 255)
    private String code;
    @NotNull(message = "error.menuName.null", groups = Valid.class)
    @MultiLanguageField
    @Length(max = 128)
    private String name;
    /**
     * 名称首字母
     */
    @Pattern(regexp = "^[a-zA-Z0-9]{0,30}$", message = "error.menu.quickIndexIllegal", groups = Valid.class)
    @Column(name = "h_quick_index")
    private String quickIndex;
    @NotNull(message = "error.menuLevel.null", groups = Valid.class)
    @Column(name = "fd_level")
    @LovValue(lovCode = "HIAM.RESOURCE_LEVEL", groups = Valid.class)
    private String level;
    @Encrypt
    private Long parentId;
    @NotNull(message = "error.menuType.null", groups = Valid.class)
    // 1、root 根目录 2、dir 目录 3、menu 菜单
    @LovValue(lovCode = "HIAM.MENU_TYPE")
    private String type;
    private Integer sort;
    /**
     * boolean -> Integer
     */
    @Range(min = 0, max = 1, groups = Valid.class)
    private Integer isDefault;
    private String icon;
    private String route;
    /**
     * boolean -> Integer
     */
    @Range(min = 0, max = 1)
    @Column(name = "h_custom_flag")
    private Integer customFlag;
    @Column(name = "h_tenant_id")
    @NotNull(message = "error.tenantId.null", groups = Valid.class)
    @MultiLanguageField
    private Long tenantId;
    /**
     * boolean -> Integer
     */
    @Range(min = 0, max = 1)
    @Column(name = "h_enabled_flag")
    private Integer enabledFlag;
    @Column(name = "h_description")
    private String description;
    @Column(name = "h_level_path")
    @JsonIgnore
    private String levelPath;
    /**
     * 是否虚拟菜单标识, 虚拟菜单将不参与左侧菜单栏展示
     */
    @Column(name = "h_virtual_flag")
    @Range(min = 0, max = 1)
    private Integer virtualFlag;

    //
    // getter/setter
    // ------------------------------------------------------------------------------
    /**
     * 权限集控制类型
     */
    @Column(name = "h_controller_type")
    @LovValue(lovCode = "HIAM.CONTROLLER_TYPE")
    private String controllerType;
    /**
     * 权限组件类型
     */
    @Column(name = "h_permission_type")
    private String permissionType;
    /**
     * 是否可以编辑权限集权限明细
     */
    @Transient
    private Integer editDetailFlag;
    @Transient
    private Integer newSubnodeFlag;
    /**
     * 权限集末级节点标识
     */
    @Transient
    private Integer psLeafFlag;
    /**
     * 选中状态, 角色分配权限时使用 Y - checked, N - unchecked
     * 安全组访问权限也在使用该字段，请谨慎修改
     */
    @Transient
    private String checkedFlag;
    @Transient
    @JsonIgnore
    private Menu parentMenu;
    @Transient
    private String parentName;
    @Transient
    private List<Menu> subMenus;
    @Transient
    private List<Permission> permissions;
    @Transient
    private List<MenuPermission> menuPermissions;
    @Transient
    private List<MenuTl> menuTls;
    @Transient
    private String parentCode;
    // end
    @Transient
    private Long parentTenantId;
    @Transient
    private String zhName;
    @Transient
    private String enName;
    @Transient
    private String levelMeaning;
    @Transient
    private String typeMeaning;
    @Transient
    private String controllerTypeMeaning;
    @Transient
    private String viewCode;
    @Transient
    private String inheritFlag;
    @Transient
    private String createFlag;
    /**
     * 是否屏蔽标志 1-屏蔽 0-不屏蔽。用于安全组展示访问权限使用
     */
    @Transient
    private Integer shieldFlag;
    /**
     * 所属安全组访问权限ID。用于安全组展示访问权限使用
     */
    @Transient
    @Encrypt
    private String secGrpAclId;
    /**
     * 菜单的标签
     */
    @Transient
    private List<Label> labels;

    @Transient
    private String tenantName;

    /**
     * 是否是根节点
     */
    @Transient
    private boolean rootNode;

    public Long getParentTenantId() {
        return parentTenantId;
    }

    public void setParentTenantId(Long parentTenantId) {
        this.parentTenantId = parentTenantId;
    }

    public String getInheritFlag() {
        return inheritFlag;
    }

    public void setInheritFlag(String inheritFlag) {
        this.inheritFlag = inheritFlag;
    }

    public String getCreateFlag() {
        return createFlag;
    }

    public void setCreateFlag(String createFlag) {
        this.createFlag = createFlag;
    }

    public Integer getShieldFlag() {
        return shieldFlag;
    }

    public void setShieldFlag(Integer shieldFlag) {
        this.shieldFlag = shieldFlag;
    }

    public String getSecGrpAclId() {
        return secGrpAclId;
    }

    public void setSecGrpAclId(String secGrpAclId) {
        this.secGrpAclId = secGrpAclId;
    }

    /**
     * 菜单创建初始化
     */
    public void initMenu() {
        Assert.notNull(tenantId, "menu's tenantId must not be null.");
        this.parentId = Optional.ofNullable(this.parentId).orElse(ROOT_ID);
        this.isDefault = BaseConstants.Flag.YES;
        if (HiamMenuType.ROOT.value().equals(this.type)) {
            this.parentId = ROOT_ID;
        }
        // code 去除重复的点号
        this.code = StringUtils.replaceAll(this.code, "\\.{2,}", ".");
    }

    public void initQuickIndex() {
        // 初始化首字母
        if (StringUtils.isBlank(this.quickIndex)) {
            this.quickIndex = ChineseUtils.extractCapitalInitial(this.name);
        }
    }

    public void validate(Menu parentMenu) {
        // 验证菜单数据的有效性
        HiamMenuType.throwExceptionNotMatch(this.type);
        HiamResourceLevel.levelOf(this.level);
        if (!Menu.ROOT_ID.equals(this.parentId)) {
            if (!parentMenu.getLevel().equals(this.level)) {
                throw new CommonException("hiam.error.menu.levelNotEqualsParentLevel");
            }
        }
        if (StringUtils.equalsAny(this.type, HiamMenuType.MENU.value(), HiamMenuType.LINK.value(), HiamMenuType.INNER_LINK.value(), HiamMenuType.WINDOW.value())) {
            if (StringUtils.isBlank(this.route)) {
                throw new CommonException("hiam.error.menu.routeNotNull");
            }
        }
    }

    /**
     * 设置 levelPath，使用 code 作路径
     *
     * @param parentMenu not null
     */
    public void initLevelPath(Menu parentMenu) {
        if (Objects.equals(this.parentId, Menu.ROOT_ID)) {
            this.levelPath = this.code;
        } else {
            this.levelPath = parentMenu.getLevelPath() + BaseConstants.PATH_SEPARATOR + this.code;
        }
    }

    /**
     * 调整父级菜单时重置编码
     */
    public void resetCode(String originParentCode, String nowParentCode) {
        //if (this.code.startsWith(originParentCode)) {
        //    this.code = StringUtils.replaceFirst(this.code, originParentCode, nowParentCode);
        //}
    }

    /**
     * 生成默认权限集
     *
     * @return 菜单默认权限集
     */
    public Menu generateDefaultPermissionSet() {
        Menu ps = new Menu();
        ps.setCode(this.code + PS_DEFAULT);
        String defaultPsName = MessageAccessor.getMessage(Menu.PS_DEFAULT_NAME, LanguageHelper.language()).desc();
        ps.setName(defaultPsName);
        ps.setLevel(this.level);
        ps.setParentId(this.id);
        ps.setType(HiamMenuType.PS.value());
        ps.setSort(10);
        ps.setCustomFlag(this.getCustomFlag());
        ps.setTenantId(this.getTenantId());
        ps.setControllerType(Menu.DEFAULT_CONTROLLER_TYPE.type());

        return ps;
    }

    public String getViewCode() {
        if (StringUtils.isNoneBlank(this.code) && StringUtils.isNoneBlank(this.type)) {
            if (StringUtils.equalsAny(this.type, HiamMenuType.ROOT.value(), HiamMenuType.DIR.value(), HiamMenuType.MENU.value())) {
                String[] codeArr = StringUtils.split(this.code, ".");

                if (BaseConstants.Flag.YES.equals(this.customFlag)) {
                    codeArr = ArrayUtils.subarray(codeArr, 3, codeArr.length);
                } else {
                    codeArr = ArrayUtils.subarray(codeArr, 2, codeArr.length);
                }
                return StringUtils.join(codeArr, ".");
            } else if (HiamMenuType.PS.value().equals(this.type)) {
                int startIndex = StringUtils.indexOf(this.code, ".ps.");
                return StringUtils.substring(this.code, startIndex + 4);
            }
        }
        return this.code;
    }

    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDefault() {
        return isDefault;
    }

    public void setDefault(Integer aDefault) {
        isDefault = aDefault;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public void setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Integer getCustomFlag() {
        return customFlag;
    }

    public void setCustomFlag(Integer customFlag) {
        this.customFlag = customFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Menu> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(List<Menu> subMenus) {
        this.subMenus = subMenus;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public String getControllerType() {
        return controllerType;
    }

    public void setControllerType(String controllerType) {
        this.controllerType = controllerType;
    }

    public Integer getEditDetailFlag() {
        return editDetailFlag;
    }

    public void setEditDetailFlag(Integer editDetailFlag) {
        this.editDetailFlag = editDetailFlag;
    }

    public Integer getNewSubnodeFlag() {
        return newSubnodeFlag;
    }

    public void setNewSubnodeFlag(Integer newSubnodeFlag) {
        this.newSubnodeFlag = newSubnodeFlag;
    }

    public Integer getPsLeafFlag() {
        return psLeafFlag;
    }

    public void setPsLeafFlag(Integer psLeafFlag) {
        this.psLeafFlag = psLeafFlag;
    }

    public String getCheckedFlag() {
        return this.checkedFlag;
    }

    public void setCheckedFlag(String checkedFlag) {
        this.checkedFlag = checkedFlag;
    }

    public String getControllerTypeMeaning() {
        return controllerTypeMeaning;
    }

    public void setControllerTypeMeaning(String controllerTypeMeaning) {
        this.controllerTypeMeaning = controllerTypeMeaning;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public Menu setPermissionType(String permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public List<MenuPermission> getMenuPermissions() {
        return menuPermissions;
    }

    public void setMenuPermissions(List<MenuPermission> menuPermissions) {
        this.menuPermissions = menuPermissions;
    }

    public List<MenuTl> getMenuTls() {
        return menuTls;
    }

    public void setMenuTls(List<MenuTl> menuTls) {
        this.menuTls = menuTls;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Menu setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public boolean isRootNode() {
        return rootNode;
    }

    public void setRootNode(boolean rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", quickIndex='" + quickIndex + '\'' +
                ", level='" + level + '\'' +
                ", parentId=" + parentId +
                ", type='" + type + '\'' +
                ", sort=" + sort +
                ", isDefault=" + isDefault +
                ", icon='" + icon + '\'' +
                ", route='" + route + '\'' +
                ", customFlag=" + customFlag +
                ", tenantId=" + tenantId +
                ", enabledFlag=" + enabledFlag +
                ", description='" + description + '\'' +
                ", levelPath='" + levelPath + '\'' +
                ", virtualFlag=" + virtualFlag +
                ", controllerType='" + controllerType + '\'' +
                ", permissionType='" + permissionType + '\'' +
                ", editDetailFlag=" + editDetailFlag +
                '}';
    }

    /**
     * 按钮控制类型
     */
    public enum ControllerType {
        HIDDEN("hidden"),
        DISABLED("disabled");

        private final String type;

        ControllerType(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }
    }

    public interface Valid {
    }
}
