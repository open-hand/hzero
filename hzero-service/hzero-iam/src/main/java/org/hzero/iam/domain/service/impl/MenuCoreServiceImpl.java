package org.hzero.iam.domain.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Pair;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.MenuCoreService;
import org.hzero.iam.domain.service.role.RolePermissionSetAssignService;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.infra.constant.*;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 菜单服务
 *
 * @author bojiangzhou 2019/01/17
 */
@Component
public class MenuCoreServiceImpl implements MenuCoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuCoreServiceImpl.class);

    /**
     * 标签：HZERO_MENU
     */
    private static final String LABEL_HZERO_MENU = "HZERO_MENU";

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    @Lazy
    private PermissionRepository permissionRepository;
    @Autowired
    private MenuPermissionRepository menuPermissionRepository;
    @Autowired
    private RolePermissionSetAssignService rolePermissionSetAssignService;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private LabelRelRepository labelRelRepository;
    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    /**
     * HZERO_MENU标签缓存
     */
    private Label hzeroMenuLabelCache;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Menu createMenu(Menu menu, boolean createDefaultPs) {
        // 初始化
        menu.initMenu();
        menu.initQuickIndex();

        Menu parentMenu = null;
        if (!Menu.ROOT_ID.equals(menu.getParentId())) {
            parentMenu = menuRepository.selectByPrimaryKey(menu.getParentId());
            if (parentMenu == null) {
                throw new CommonException("hiam.error.menu.parentMenuNotFound");
            }
        }

        // 验证
        menu.validate(parentMenu);

        // 菜单是否重复
        checkMenuExists(menu);

        // level path
        menu.initLevelPath(parentMenu);

        // 创建并返回
        menuRepository.insertSelective(menu);

        // 处理菜单标签
        handleCreateMenuLabels(parentMenu, menu);

        // 创建默认权限集
        if (createDefaultPs && StringUtils.equalsAny(menu.getType(),
                HiamMenuType.MENU.value(),
                HiamMenuType.LINK.value(),
                HiamMenuType.INNER_LINK.value(),
                HiamMenuType.WINDOW.value())) {
            Menu defaultPs = menu.generateDefaultPermissionSet();
            this.createMenu(defaultPs, false);
        }

        if (HiamMenuType.PS.value().equals(menu.getType())) {
            // @since 2020.05.08: 无需将权限集挂到超级管理员上
            //rolePermissionSetAssignService.assignPermissionSetsToSuperAdmin(menu.getLevel(), menu.getId(), RolePermissionType.PS);

            //如果层级是organization  且客户化标志为1，还需要将角色新增到当前租户的租户管理员下
            if (HiamResourceLevel.ORGANIZATION.value().equals(menu.getLevel()) &&
                    BaseConstants.Flag.YES.equals(menu.getCustomFlag())) {
                rolePermissionSetAssignService.assignPermissionSetsToTenantManager(menu.getTenantId(), menu.getId(), RolePermissionType.PS);
            }
        }

        LOGGER.info("create menu {}", menu);

        return menu;
    }

    @Override
    public void checkMenuExists(Menu menu) {
        Menu param = new Menu();
        param.setTenantId(menu.getTenantId());
        param.setCode(menu.getCode());
        param.setLevel(menu.getLevel());

        int count = menuRepository.selectCount(param);
        if (count > 0) {
            throw new CommonException("error.menu.duplicate");
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Menu updateMenu(Menu menu) {
        Assert.notNull(menu.getId(), "menuId must not be null when update menu.");
        // 初始化
        menu.initQuickIndex();

        // 判断菜单是否存在
        Menu self = menuRepository.selectByPrimaryKey(menu.getId());
        if (self == null) {
            throw new CommonException("error.menu.not.exist");
        }
        menu.setCode(self.getCode());
        menu.setLevelPath(self.getLevelPath());

        if (menu.getParentId() == null) {
            menu.setParentId(self.getParentId());
        }

        // 控制菜单的父级菜单不能为自己
        if (Objects.equals(menu.getParentId(), menu.getId())) {
            throw new CommonException("hiam.warn.menu.parentIdNotSelf");
        }

        if (!Objects.equals(self.getParentId(), menu.getParentId())) {
            // 原父级菜单
            Menu originParent = menuRepository.selectByPrimaryKey(self.getParentId());
            if (originParent == null) {
                throw new CommonException("hiam.error.menu.parentMenuNotFound");
            }
            // 先父级菜单
            Menu nowParent = menuRepository.selectByPrimaryKey(menu.getParentId());
            if (nowParent == null) {
                throw new CommonException("hiam.error.menu.parentMenuNotFound");
            }

            //menu.resetCode(originParent.getCode(), nowParent.getCode());
            menu.initLevelPath(nowParent);
            // 更新子菜单 level_path
            setupSubMenuLevelPath(menu, self);
        }

        // 更新时处理菜单标签
        handleUpdateMenuLabels(menu);

        // 更新
        menuRepository.updateOptional(menu,
                Menu.FIELD_NAME,
                Menu.FIELD_QUICK_INDEX,
                Menu.FIELD_PARENT_ID,
                Menu.FIELD_SORT,
                Menu.FIELD_IS_DEFAULT,
                Menu.FIELD_ICON,
                Menu.FIELD_ROUTE,
                Menu.FIELD_ENABLED_FLAG,
                Menu.FIELD_DESCRIPTION,
                Menu.FIELD_LEVEL_PATH,
                Menu.FIELD_VIRTUAL_FLAG,
                Menu.FIELD_CONTROLLER_TYPE
        );
        return menu;
    }

    /**
     * 处理菜单创建时的标签
     *
     * @param parentMenu 菜单的父级菜单
     * @param menu       菜单对象
     */
    private void handleCreateMenuLabels(Menu parentMenu, Menu menu) {
        if (Objects.nonNull(parentMenu)) {
            // 查询可继承的标签IDs
            Set<Long> inheritLabelIds = this.labelRelRepository.selectInheritLabelIdsByDataTypeAndDataIds(Menu.LABEL_DATA_TYPE,
                    Collections.singleton(parentMenu.getId()));
            if (CollectionUtils.isNotEmpty(inheritLabelIds)) {
                // 分配标签
                this.labelRelRepository.addLabels(Menu.LABEL_DATA_TYPE, menu.getId(), LabelAssignType.AUTO, inheritLabelIds);

                // 获取菜单标签视图
                List<Label> labels = Optional.ofNullable(menu.getLabels()).orElse(new ArrayList<>());
                // 添加已分配的继承标签
                labels.addAll(inheritLabelIds.stream().map(labelId -> {
                    Label label = new Label();
                    label.setId(labelId);
                    return label;
                }).collect(toSet()));
                // 设置菜单标签
                menu.setLabels(labels);
            }
        }

        // 处理创建菜单时的初始化标签
        this.handleCreateMenuInitLabels(menu);
        // 处理菜单本身带有的标签
        this.handleUpdateMenuLabels(menu);
    }

    /**
     * 处理创建菜单时的初始化标签
     *
     * @param menu 菜单对象
     */
    private void handleCreateMenuInitLabels(Menu menu) {
        if (HiamMenuType.ROOT.value().equals(menu.getType())) {
            // 只有根目录会有该逻辑
            // 处理 HZERO_MENU 标签，即新建菜单未传标签默认添加HZERO_MENU标签
            List<Label> labels = menu.getLabels();
            // 获取 HZERO_MENU 标签
            Label hzeroMenuLabel = this.getHzeroMenuLabel();
            if (Objects.isNull(labels)) {
                labels = new ArrayList<>();
                labels.add(hzeroMenuLabel);
                menu.setLabels(labels);
                return;
            }

            // 判断已有标签中是否存在 HZERO_MENU 标签，如果存在就直接返回
            if (labels.stream().map(Label::getId).anyMatch(labelId -> Objects.equals(labelId, hzeroMenuLabel.getId()))) {
                return;
            }

            // 添加初始标签
            labels.add(hzeroMenuLabel);
        }
    }

    /**
     * 获取 HZERO_MENU 标签对象
     *
     * @return HZERO_MENU 标签对象
     */
    private Label getHzeroMenuLabel() {
        // 因为只是获取数据，没有强制性的并发要求，在此处就没有进行加锁
        if (Objects.isNull(this.hzeroMenuLabelCache)) {
            // 查询数据
            this.hzeroMenuLabelCache = this.labelRepository.findByNameAndType(LABEL_HZERO_MENU, Menu.LABEL_DATA_TYPE);
        }

        // 判断数据是否存在
        if (Objects.isNull(this.hzeroMenuLabelCache)) {
            throw new CommonException("内置标签 [HZERO_MENU] 不存在，请核查标签初始化数据是否正常！！！");
        }

        // 返回结果
        return this.hzeroMenuLabelCache;
    }

    /**
     * 处理菜单更新时的标签
     *
     * @param menu 菜单对象
     */
    private void handleUpdateMenuLabels(Menu menu) {
        // 获取菜单标签视图数据
        List<Label> menuLabels = menu.getLabels();
        if (Objects.isNull(menuLabels)) {
            // ！！！注意：如果菜单标签的数据是 null，就代表不处理角色标签
            return;
        }
        // 更新标签数据
        Pair<List<Label>, List<Label>> updateResult = this.labelRelRepository.updateLabelRelationsByLabelView(Menu.LABEL_DATA_TYPE,
                menu.getId(), LabelAssignType.MANUAL, menuLabels.stream().map(Label::getId).filter(Objects::nonNull).collect(toSet()));
        if (CollectionUtils.isEmpty(updateResult.getFirst()) && CollectionUtils.isEmpty(updateResult.getSecond())) {
            return;
        }

        // 查询子菜单
        List<Menu> subMenus = this.menuRepository.selectByCondition(Condition.builder(Menu.class)
                .andWhere(Sqls.custom()
                        // 子路径
                        .andLike(Menu.FIELD_LEVEL_PATH, menu.getLevelPath().concat(BaseConstants.Symbol.VERTICAL_BAR)
                                .concat(BaseConstants.Symbol.PERCENTAGE))
                ).build());
        if (CollectionUtils.isEmpty(subMenus)) {
            return;
        }
        // 子菜单
        Set<Long> subMenuIds = subMenus.stream().map(Menu::getId).collect(toSet());

        // 分配子菜单可继承的标签
        this.assignSubMenuLabels(subMenuIds, updateResult.getFirst());
        // 回收子菜单可继承的标签
        this.recycleSubMenuLabels(subMenuIds, updateResult.getSecond());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void deleteMenuById(Long tenantId, Long menuId) {
        // 验证菜单是否存在
        Menu menuParam = new Menu();
        menuParam.setTenantId(tenantId);
        menuParam.setId(menuId);
        Menu self = menuRepository.selectOne(menuParam);
        if (self == null) {
            throw new CommonException("error.menu.not.exist");
        }

        // 校验菜单是否禁用，若禁用则不允许删除
        Assert.isTrue(BaseConstants.Flag.NO.equals(self.getEnabledFlag()), HiamError.ErrorCode.ENABLE_MENU_NOT_DELETE);

        // 校验菜单是否分配了角色
        validMenuAssignRole(self, tenantId);

        // 删除子菜单
        deleteChildMenu(self.getId(), tenantId);

        // 删除自身
        menuRepository.deleteByPrimaryKey(self.getId());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void changeEnableFlag(Long tenantId, Long menuId, Integer enableFlag) {
        // 验证菜单是否存在
        Menu menuParam = new Menu();
        menuParam.setTenantId(tenantId);
        menuParam.setId(menuId);
        Menu self = menuRepository.selectOne(menuParam);
        if (self == null) {
            throw new CommonException("error.menu.not.exist");
        }

        if (!Menu.ROOT_ID.equals(self.getParentId())) {
            Menu parentMenu = menuRepository.selectByPrimaryKey(self.getParentId());
            if (parentMenu == null) {
                throw new CommonException("hiam.error.menu.parentMenuNotFound");
            }
            // 启用时判断父级是否启用，当租户和客制化标识相同才判断
            if (BaseConstants.Flag.YES.equals(enableFlag) && BaseConstants.Flag.NO.equals(parentMenu.getEnabledFlag())
                    && Objects.equals(self.getTenantId(), parentMenu.getTenantId()) && Objects.equals(self.getCustomFlag(), parentMenu.getCustomFlag())) {
                throw new CommonException("hiam.warn.menu.parentMenuNotEnabled");
            }
        }

        // 更新子菜单
        changeChildEnableFlag(self.getId(), self.getCustomFlag(), tenantId, enableFlag);

        // 更新自身
        updateEnableFlag(self, enableFlag);
    }

    @Override
    public void assignPsPermissions(Long permissionSetId, PermissionType permissionType, String[] permissionCodes) {
        if (ArrayUtils.isEmpty(permissionCodes)) {
            return;
        }
        Menu ps = menuRepository.selectByPrimaryKey(permissionSetId);
        if (ps == null) {
            throw new CommonException("hiam.error.ps.notFound");
        }

        if (!HiamMenuType.PS.value().equals(ps.getType())) {
            throw new CommonException("hiam.error.ps.errorType");
        }
        List<String> permissionCodeList = new ArrayList<>(Arrays.asList(permissionCodes));
        // 权限需验证层级 权限层级必须和菜单层级一致
        if (PermissionType.PERMISSION.equals(permissionType)) {
            Set<String> apiLevel = HiamResourceLevel.levelOf(ps.getLevel()).getApiLevel();
            List<Permission> permissions = permissionRepository.selectByCodes(permissionCodes);
            if (permissions.stream().anyMatch(p -> !apiLevel.contains(p.getLevel()))) {
                throw new CommonException("hiam.warn.ps.permissionLevelNotMatch");
            }
        }
        // Lov校验租户是否一样
        else if (PermissionType.LOV.equals(permissionType)) {
            Long tenantId = ps.getTenantId();
            List<Lov> lovs = permissionRepository.selectLovByCodes(permissionCodes, tenantId);
            for (Lov lov : lovs) {
                if (!Objects.equals(lov.getTenantId(), BaseConstants.DEFAULT_TENANT_ID) && !Objects.equals(lov.getTenantId(), tenantId)) {
                    throw new CommonException("hiam.warn.ps.lovDenyToTenant", lov.getLovCode());
                }
                // 获取lov对应的url编码
                String apiCode = getApiCodeByLov(lov, ps.getLevel(), true);
                if (StringUtils.isNotBlank(apiCode)) {
                    permissionCodeList.add(apiCode);
                }
            }
        }

        for (String code : permissionCodeList) {
            MenuPermission mp = new MenuPermission();
            mp.setMenuId(permissionSetId);
            mp.setTenantId(ps.getTenantId());
            mp.setPermissionCode(code);

            // 防止who字段查询条件影响查询结果
            int count = menuPermissionRepository.selectCountByCondition(Condition.builder(MenuPermission.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(MenuPermission.FIELD_MENU_ID, permissionSetId)
                            .andEqualTo(MenuPermission.FIELD_PERMISSION_CODE, code)
                    )
                    .build());

            if (count == 0) {
                menuPermissionRepository.insertSelective(mp);

            }
        }
    }

    /**
     * 获取lov对应的api
     *
     * @param lov   lov
     * @param level 层级
     * @return api路径
     */
    private String getApiCodeByLov(Lov lov, String level, boolean check) {
        // 若lov为URL类型，查找对应的接口编码插入
        if (Constants.LovType.URL.equals(lov.getLovTypeCode())) {
            String serverName = getServerName(lov.getRouteName());
            String path = lov.getCustomUrl();
            if (path.contains(BaseConstants.Symbol.QUESTION)) {
                path = path.substring(0, path.indexOf(BaseConstants.Symbol.QUESTION));
            }
            Permission param = new Permission().setPath(path).setMethod(HttpMethod.GET.name().toLowerCase()).setServiceName(serverName);
            Permission permission = permissionRepository.selectOne(param);
            if (permission == null) {
                if (check) {
                    throw new CommonException("hiam.error.permission.lov_url.not_found", lov.getLovCode());
                }
                return null;
            }
            if (permission.getLoginAccess() || permission.getPublicAccess()) {
                // 不需要分配
                return null;
            }
            // 判断接口层级
            if (check && !Objects.equals(level, permission.getLevel())) {
                throw new CommonException("hiam.warn.ps.permissionLevelNotMatch");
            }
            return permission.getCode();
        }
        return null;
    }

    /**
     * 获取服务全名
     *
     * @param serverCode 服务简码
     * @return 服务全称
     */
    private String getServerName(String serverCode) {
        this.redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
        String serverName = redisHelper.hshGet(HZeroService.Admin.CODE + ":routes", serverCode);
        this.redisHelper.clearCurrentDatabase();
        return serverName;
    }

    @Override
    public void recyclePsPermissions(Long permissionSetId, String[] permissionCodes, PermissionType permissionType) {
        permissionType = PermissionType.LOV;
        List<String> permissionCodeList = new ArrayList<>(Arrays.asList(permissionCodes));
        Menu ps = menuRepository.selectByPrimaryKey(permissionSetId);
        if (ps == null) {
            throw new CommonException("hiam.error.ps.notFound");
        }
        if (!HiamMenuType.PS.value().equals(ps.getType())) {
            throw new CommonException("hiam.error.ps.errorType");
        }
        if (PermissionType.LOV.equals(permissionType)) {
            // 删除类型为值集，查找对应api编码
            List<Lov> lovs = permissionRepository.selectLovByCodes(permissionCodes, ps.getTenantId());
            for (Lov lov : lovs) {
                // 获取lov对应的url编码
                String apiCode = getApiCodeByLov(lov, ps.getLevel(), false);
                if (StringUtils.isNotBlank(apiCode)) {
                    permissionCodeList.add(apiCode);
                }
            }
        }
        menuPermissionRepository.deleteByIdAndCodes(permissionSetId, permissionCodeList);
    }

    @Override
    public int initLevelPath(HiamMenuType type, boolean initAll) {
        // 初始化查询条件
        Menu param = new Menu();
        param.setType(type.value());
        int count = 0;
        LOGGER.info("init menu level path, type={}", type);

        // 更新Level Path
        List<Menu> menuList = menuRepository.select(param);
        for (Menu menu : menuList) {
            if (initAll || StringUtils.isEmpty(menu.getLevelPath())) {
                // 非ROOT类型菜单, 父级LevelPath需要重新获取
                Menu parentMenu = null;
                if (!HiamMenuType.ROOT.equals(type)) {
                    parentMenu = menuRepository.selectByPrimaryKey(menu.getParentId());
                    if (parentMenu == null) {
                        LOGGER.warn("menu's parent menu not found. [menuId={}, parentMenuId={}]", menu.getId(), menu.getParentId());
                        continue;
                    }
                }
                menu.initLevelPath(parentMenu);

                menuRepository.updateOptional(menu, Menu.FIELD_LEVEL_PATH);
                count++;
            }
        }
        return count;
    }

    //
    // private method
    // ------------------------------------------------------------------------------

    /**
     * 更新菜单时，如果更新了父级角色，需更新子菜单的level path
     *
     * @param menu 菜单
     */
    private void setupSubMenuLevelPath(Menu menu, Menu self) {
        // 根据 level_path 查询子角色
        List<Menu> subMenus = menuRepository.selectByCondition(Condition.builder(Menu.class)
                .andWhere(Sqls.custom().andLike(Menu.FIELD_LEVEL_PATH, self.getLevelPath() + BaseConstants.PATH_SEPARATOR))
                .orderByAsc(Menu.FIELD_LEVEL_PATH)
                .build());

        setupSubMenuLevelPath(menu, self.getCode(), subMenus);

        menuRepository.batchUpdateOptional(subMenus, Menu.FIELD_LEVEL_PATH);
    }

    private void setupSubMenuLevelPath(Menu parentMenu, String originParentCode, List<Menu> subMenus) {
        subMenus.stream().filter(sm -> parentMenu.getId().equals(sm.getParentId())).forEach(menu -> {
            String curOriginParentCode = menu.getCode();
            //menu.resetCode(originParentCode, parentMenu.getCode());
            menu.initLevelPath(parentMenu);
            setupSubMenuLevelPath(menu, curOriginParentCode, subMenus);
        });
    }

    // 递归删除子菜单
    private void deleteChildMenu(Long parentId, Long tenantId) {
        Menu param = new Menu();
        param.setParentId(parentId);
        param.setTenantId(tenantId);
        List<Menu> childList = menuRepository.select(param);
        if (CollectionUtils.isNotEmpty(childList)) {
            for (Menu menu : childList) {
                menuRepository.deleteByPrimaryKey(menu.getId());
                deleteChildMenu(menu.getId(), tenantId);
            }
        }
    }

    // 递归禁用或启用子菜单
    private void changeChildEnableFlag(Long parentId, Integer customFlag, Long tenantId, Integer enableFlag) {
        Menu param = new Menu();
        param.setParentId(parentId);
        param.setTenantId(tenantId);
        List<Menu> childList = menuRepository.select(param);
        if (CollectionUtils.isNotEmpty(childList)) {
            for (Menu menu : childList) {
                if (Objects.equals(customFlag, menu.getCustomFlag())) {
                    updateEnableFlag(menu, enableFlag);
                    changeChildEnableFlag(menu.getId(), menu.getCustomFlag(), tenantId, enableFlag);
                }
            }
        }
    }

    private void updateEnableFlag(Menu menu, Integer enableFlag) {
        menu.setEnabledFlag(enableFlag);
        menuRepository.updateOptional(menu, Menu.FIELD_ENABLED_FLAG);
    }

    /**
     * 分配子菜单标签(用于给子菜单分配可继承的标签，如果标签不是可继承的，就不处理)
     *
     * @param subMenuIds   子菜单IDs
     * @param assignLabels 待分配的标签
     */
    private void assignSubMenuLabels(Set<Long> subMenuIds, List<Label> assignLabels) {
        // 增加的标签里属于可继承的标签IDs
        Set<Long> addedInheritLabelIds = Optional.ofNullable(assignLabels).orElse(Collections.emptyList())
                .stream().filter(label -> BaseConstants.Flag.YES.equals(label.getInheritFlag()))
                .map(Label::getId)
                .collect(toSet());

        // 处理添加的可继承的标签
        this.labelRelRepository.assignLabels(Menu.LABEL_DATA_TYPE, LabelAssignType.AUTO, subMenuIds, addedInheritLabelIds);
    }

    /**
     * 回收子菜单标签(用于回收子菜单可继承的标签，如果标签不是可继承的，就不处理)
     *
     * @param subMenuIds    子菜单IDs
     * @param recycleLabels 待回收的标签
     */
    private void recycleSubMenuLabels(Set<Long> subMenuIds, List<Label> recycleLabels) {
        // 移除的标签里属于可继承的标签IDs
        Set<Long> removedInheritLabelIds = Optional.ofNullable(recycleLabels).orElse(Collections.emptyList())
                .stream().filter(label -> BaseConstants.Flag.YES.equals(label.getInheritFlag()))
                .map(Label::getId)
                .collect(toSet());

        // 处理移除的可继承的标签
        this.labelRelRepository.recycleLabels(Menu.LABEL_DATA_TYPE, subMenuIds, removedInheritLabelIds);
    }

    /**
     * 校验菜单是否已经分配给了角色
     *
     * @param menu 菜单参数
     * @param tenantId 租户Id
     */
    private void validMenuAssignRole(Menu menu, Long tenantId) {
        List<RolePermission> rolePermissions =
                rolePermissionRepository.getRoleAssignPermissionSet(menu, tenantId);
        // 租户层客制化菜单需删除租户管理员下默认分配的权限集
        if (HiamResourceLevel.ORGANIZATION.value().equals(menu.getLevel()) &&
                BaseConstants.Flag.YES.equals(menu.getCustomFlag())) {
            boolean result = rolePermissions.stream().anyMatch(rolePermission -> BooleanUtils.isFalse(rolePermission.getTenantAdmin()));
            if (result) {
                throw new CommonException(HiamError.ErrorCode.MENU_ALREADY_ASSIGN_ROLE);
            } else {
                // 删除租户管理员自动分配的权限数据
                rolePermissionRepository.batchDeleteBySql(rolePermissions);
            }
        } else {
            // 非租户层客制化菜单，此时查到记录说明存在菜单分配到角色上
            if (rolePermissions.size() > 0) {
                throw new CommonException(HiamError.ErrorCode.MENU_ALREADY_ASSIGN_ROLE);
            }
        }
    }
}
