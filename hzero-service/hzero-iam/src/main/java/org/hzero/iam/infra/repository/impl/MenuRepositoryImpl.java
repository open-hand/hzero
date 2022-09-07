package org.hzero.iam.infra.repository.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.util.CommonStream;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.domain.vo.RolePermissionVO;
import org.hzero.iam.infra.common.utils.HiamMenuUtils;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.mapper.MenuMapper;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.helper.SecurityTokenHelper;

/**
 * @author allen 2018/6/29
 */
@Repository
public class MenuRepositoryImpl extends BaseRepositoryImpl<Menu> implements MenuRepository, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuRepositoryImpl.class);
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    @Qualifier("IamCommonAsyncTaskExecutor")
    private ThreadPoolExecutor iamExecutor;

    /**
     * 平台超级管理员ID
     */
    private final AtomicLong siteSuperAdminRoleId = new AtomicLong(-1);
    /**
     * 租户超级管理员ID
     */
    private final AtomicLong tenantSuperAdminRoleId = new AtomicLong(-1);

    @Override
    public void afterPropertiesSet() throws Exception {
        // 租户超级管理员
        Role tenantSuperRole = roleMapper.selectOne(new Role().setCode(Constants.TENANT_SUPER_ROLE_CODE));
        if (tenantSuperRole != null) {
            tenantSuperAdminRoleId.set(tenantSuperRole.getId());
        } else {
            LOGGER.warn("tenant super role admin not found, roleCode={}", Constants.TENANT_SUPER_ROLE_CODE);
        }

        // 平台超级管理员
        if (siteSuperAdminRoleId.get() < 0) {
            Role siteSuperRole = roleMapper.selectOne(new Role().setCode(Constants.SITE_SUPER_ROLE_CODE));
            if (siteSuperRole != null) {
                siteSuperAdminRoleId.set(siteSuperRole.getId());
            } else {
                LOGGER.warn("site super role admin not found， roleCode={}", Constants.SITE_SUPER_ROLE_CODE);
            }
        }
    }

    @Override
    public List<Menu> selectMenuTreeInSite(MenuSearchDTO menuParams) {
        menuParams = Optional.ofNullable(menuParams).orElse(new MenuSearchDTO());
        menuParams.setupSiteQueryLevel();
        menuParams.setTenantId(Constants.SITE_TENANT_ID);
        menuParams.setupQueryParam();
        List<Menu> menuList = menuMapper.selectMenusByCondition(menuParams);

        return HiamMenuUtils.formatMenuListToTree(menuList, menuParams.getManageFlag());
    }

    @Override
    public List<Menu> selectMenuTreeInTenant(MenuSearchDTO menuParams) {
        menuParams = Optional.ofNullable(menuParams).orElse(new MenuSearchDTO());
        CustomUserDetails self = UserUtils.getUserDetails();
        menuParams.setTenantId(self.getTenantId());
        menuParams.setupOrganizationQueryLevel();
        menuParams.setRoleId(self.getRoleId());
        menuParams.setupQueryParam();
        List<Menu> menuList = menuMapper.selectMenusByCondition(menuParams);

        return HiamMenuUtils.formatMenuListToTree(menuList, menuParams.getManageFlag());
    }

    @Override
    public List<Menu> selectRoleMenuTree(MenuTreeQueryDTO menuTreeQueryDTO) {
        CustomUserDetails self = UserUtils.getUserDetails();
        menuTreeQueryDTO = Optional.ofNullable(menuTreeQueryDTO).orElse(new MenuTreeQueryDTO()).defaults();
        List<Long> roleIds = self.roleMergeIds();
        Long tenantId = self.getTenantId();
        String finalLang = menuTreeQueryDTO.getLang();
        Set<String> labels = menuTreeQueryDTO.getLabels();
        Boolean unionLabel = menuTreeQueryDTO.getUnionLabel();


        // 查询角色关联的菜单
        CompletableFuture<List<Menu>> f1 = CompletableFuture.supplyAsync(() -> {
            SecurityTokenHelper.close();
            DetailsHelper.setCustomUserDetails(self);
            List<Menu> menus = menuMapper.selectRoleMenus(roleIds, tenantId, finalLang, labels, unionLabel);
            SecurityTokenHelper.clear();
            return menus;
        }, iamExecutor);

        // 查询安全组关联的菜单
        CompletableFuture<List<Menu>> f2 = CompletableFuture.supplyAsync(() -> {
            SecurityTokenHelper.close();
            DetailsHelper.setCustomUserDetails(self);
            List<Menu> menus = menuMapper.selectSecGrpMenus(roleIds, tenantId, finalLang, labels, unionLabel);
            SecurityTokenHelper.clear();
            return menus;
        }, iamExecutor);


        CompletableFuture<List<Menu>> cf = f1
                .thenCombine(f2, (roleMenus, secGrpMenus) -> {
                    // 去重
                    roleMenus.addAll(secGrpMenus);
                    return roleMenus.parallelStream().filter(CommonStream.distinctByKey(Menu::getId)).collect(Collectors.toList());
                })
                // 转换成树形结构
                .thenApply((menus) -> HiamMenuUtils.formatMenuListToTree(menus, Boolean.FALSE))
                .exceptionally((e) -> {
                    LOGGER.warn("select menus error, ex = {}", e.getMessage(), e);
                    return Collections.emptyList();
                });

        return cf.join();
    }

    @Override
    public Page<Menu> selectMenuDirsInSite(MenuSearchDTO menuParams, PageRequest pageRequest) {
        menuParams = Optional.ofNullable(menuParams).orElse(new MenuSearchDTO());
        menuParams.setTenantId(Constants.SITE_TENANT_ID);
        // 平台级查询菜单
        menuParams.setupSiteQueryLevel();

        MenuSearchDTO finalMenuParams = menuParams;
        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(),
                () -> menuMapper.selectMenuDirs(finalMenuParams));
    }

    @Override
    public Page<Menu> selectMenuDirsInTenant(MenuSearchDTO menuParams, PageRequest pageRequest) {
        menuParams = Optional.ofNullable(menuParams).orElse(new MenuSearchDTO());
        CustomUserDetails self = UserUtils.getUserDetails();
        menuParams.setTenantId(self.getTenantId());
        // 组织级查询菜单
        menuParams.setupOrganizationQueryLevel();
        menuParams.setLevel(HiamResourceLevel.ORGANIZATION.value());

        MenuSearchDTO finalMenuParams = menuParams;
        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(),
                () -> menuMapper.selectMenuDirs(finalMenuParams));
    }

    @Override
    public List<Menu> selectMenuPermissionSet(Long tenantId, Long menuId, PermissionSetSearchDTO permissionSetParam) {
        permissionSetParam = Optional.ofNullable(permissionSetParam).orElse(new PermissionSetSearchDTO());
        permissionSetParam.setParentMenuId(menuId);
        permissionSetParam.setTenantId(tenantId);

        return menuMapper.selectMenuPermissionSet(permissionSetParam);
    }

    @Override
    @ProcessLovValue
    public Menu queryMenu(Long tenantId, String code, String level) {
        Menu params = new Menu();
        params.setTenantId(Optional.ofNullable(tenantId).orElse(BaseConstants.DEFAULT_TENANT_ID));
        params.setCode(code);
        params.setLevel(level);
        return selectOne(params);
    }

    @ProcessLovValue
    @Override
    public Page<Permission> selectPermissionSetPermissions(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(),
                () -> menuMapper.selectPermissionSetPermissions(permissionSetParam));
    }

    @Override
    public Page<Lov> selectPermissionSetLovs(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(),
                () -> menuMapper.selectPermissionSetLovs(permissionSetParam));
    }

    @ProcessLovValue
    @Override
    public Page<Permission> selectAssignablePermissions(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> menuMapper.selectAssignablePermissions(permissionSetParam));
    }

    @ProcessLovValue
    @Override
    public Page<Permission> selectTenantAssignablePermissions(PermissionSetSearchDTO permissionSetSearchDTO, PageRequest pageRequest) {
        permissionSetSearchDTO.setLevel(HiamResourceLevel.ORGANIZATION.value());
        return PageHelper.doPage(pageRequest, () -> menuMapper.selectAssignablePermissions(permissionSetSearchDTO));
    }

    @Override
    public Page<Lov> selectAssignableLovs(PermissionSetSearchDTO permissionSetParam, PageRequest pageRequest) {
        Menu param = new Menu();
        param.setId(permissionSetParam.getPermissionSetId());
        param.setTenantId(permissionSetParam.getTenantId());
        int count = this.selectCount(param);
        if (count == 0) {
            throw new CommonException("hiam.error.permissionSet.notFound");
        }

        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(),
                () -> menuMapper.selectAssignableLovs(permissionSetParam));
    }

    @Override
    public List<Menu> selectRolePermissionSet(Long currentRoleId, Long allocateRoleId,
                                              PermissionSetSearchDTO permissionSetParam) {
        permissionSetParam = Optional.ofNullable(permissionSetParam).orElse(new PermissionSetSearchDTO());
        permissionSetParam.setCurrentRoleId(currentRoleId);
        permissionSetParam.setAllocateRoleId(allocateRoleId);


        PermissionSetSearchDTO finalPermissionSetParam = permissionSetParam;

        CustomUserDetails self = DetailsHelper.getUserDetails();

        // 取出levelPath重复的菜单
        final Map<String, List<Menu>> levelPathMapList = new HashMap<>();
        // 查出所有菜单权限集
        CompletableFuture<List<Menu>> f1 = CompletableFuture.supplyAsync(() -> {
            SecurityTokenHelper.close();
            DetailsHelper.setCustomUserDetails(self);
            List<Menu> menus = menuMapper.listRolePermissionSet(finalPermissionSetParam);
            SecurityTokenHelper.clear();

            menus.stream()
                    .collect(Collectors.groupingBy(Menu::getLevelPath))
                    .forEach((key, value) -> {
                        if (value.size() > 1) {
                            levelPathMapList.put(key, value);
                        }
                    });

            return menus;
        }, iamExecutor);

        Set<String> levelSet = new ConcurrentSet<>();
        Map<String, Set<Long>> levelTenantIdMapList = new ConcurrentHashMap<>();

        // 查询父级角色关联的权限集
        CompletableFuture<List<RolePermissionVO>> f2 = CompletableFuture.supplyAsync(() -> {
            SecurityTokenHelper.close();
            DetailsHelper.setCustomUserDetails(self);
            List<RolePermissionVO> rps = menuMapper.listRolePermission(finalPermissionSetParam);
            SecurityTokenHelper.clear();

            rps.parallelStream()
                    .forEach(item -> {
                        String level = item.getLevelPath();
                        String[] levelPaths = level.split("\\|");
                        StringBuilder key = new StringBuilder();
                        for (String levelPath : levelPaths) {
                            key.append(levelPath).append("|");
                            levelSet.add(key.toString());
                            levelTenantIdMapList.computeIfAbsent(key.toString(), (t) -> new HashSet<>()).add(item.getTenantId());
                        }
                    });
            return rps;
        }, iamExecutor);

        List<Menu> menuList = f1.join();
        List<RolePermissionVO> rolePsList = f2.join();

        // FIX: 由于租户客制化菜单，编码可与平台标准菜单编码一样，导致levelPath一样，因此需排除平台不可分配的菜单权限集
        levelPathMapList.forEach((level, list) -> {
            Set<Long> tenantIdSet = levelTenantIdMapList.get(level + "|");
            if (tenantIdSet != null) {
                for (Menu menu : list) {
                    if (!tenantIdSet.contains(menu.getTenantId())) {
                        menuList.removeIf(item -> item.getId().equals(menu.getId()));
                    }
                }
            }
        });

        List<Menu> menus = menuList.parallelStream()
                .filter(menu -> levelSet.contains(menu.getLevelPath() + "|"))
                .collect(Collectors.toList());

        rolePsList.clear();
        levelSet.clear();

        return menus;
    }

    @Override
    public List<String> selectAccessiblePermissionSets(Long tenantId, String menuCode) {
        CustomUserDetails details = UserUtils.getUserDetails();
        Long roleId = details.getRoleId();

        return menuMapper.selectPermissionSetCodes(tenantId, roleId, menuCode);
    }

    @Override
    public List<PermissionCheckDTO> checkPermissionSets(List<String> codes) {
        return this.checkPermissionSets(codes, null);
    }

    @Override
    public List<PermissionCheckDTO> checkPermissionSets(List<String> codes, Function<List<String>, List<PermissionCheckDTO>> checkSupplier) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }

        CustomUserDetails self = UserUtils.getUserDetails();

        // admin用户、平台/租户超级管理员直接跳过权限检查
        if (BooleanUtils.isTrue(self.getAdmin())
                || self.roleMergeIds().contains(getTenantSuperAdminRoleId())
                || self.roleMergeIds().contains(getSiteSuperAdminRoleId())) {

            return codes.parallelStream().map(code -> new PermissionCheckDTO(code, true)).collect(Collectors.toList());
        }

        // 查询有权限的
        List<PermissionCheckDTO> results = null;
        if (checkSupplier == null) {
            results = menuMapper.checkPermissionSets(self.roleMergeIds(), codes);
        } else {
            results = checkSupplier.apply(codes);
        }

        // 通过的权限
        Set<String> passed = new HashSet<>();
        for (PermissionCheckDTO item : results) {
            String code = codes.stream().filter(c -> item.getCode().endsWith(c)).findFirst().orElse(null);
            if (code != null) {
                item.setCode(code);
                item.setApprove(true);
                passed.add(code);
            }
        }

        // 没有则校验不通过
        List<String> notPass = codes.stream().filter(c -> !passed.contains(c)).collect(Collectors.toList());
        if (!notPass.isEmpty()) {
            boolean isSite = self.getSiteRoleIds().contains(self.getRoleId());
            String level = isSite ? HiamResourceLevel.SITE.value() : HiamResourceLevel.ORGANIZATION.value();
            List<Menu> ps = menuMapper.selectByCodeLike(notPass, level);
            for (String code : notPass) {
                PermissionCheckDTO dto = new PermissionCheckDTO(code, false);

                Menu m = ps.stream().filter(item -> item.getCode().endsWith(code)).findFirst().orElse(null);
                if (m != null) {
                    dto.setControllerType(Optional.ofNullable(m.getControllerType()).orElse(Menu.ControllerType.DISABLED.type()));
                } else {
                    dto.setControllerType(Menu.ControllerType.DISABLED.type());
                }
                results.add(dto);
            }
        }

        return results;
    }


    @Override
    public List<Menu> selectMenuTreeForExport(Long tenantId) {
        //客制化菜单导出查询层级是租户层，菜单层级是租户层
        List<Menu> menuList = selectMenuDetail(tenantId, HiamResourceLevel.ORGANIZATION, HiamResourceLevel.ORGANIZATION, null);
        return HiamMenuUtils.formatMenuListToTree(menuList, true);
    }

    @Override
    public Menu selectMenuUnique(Long tenantId, String code, String level) {
        Menu param = new Menu();
        param.setTenantId(tenantId);
        param.setCode(code);
        param.setLevel(level);
        return menuMapper.selectOne(param);
    }

    @Override
    public List<Menu> selectMenuTreeForCopy(Long tenantId, HiamResourceLevel queryLevel, HiamResourceLevel level, Long menuId) {
        MenuSearchDTO menuParams = new MenuSearchDTO();
        menuParams.setId(menuId);
        menuParams.setLevel(level.value());
        //默认是查询层级是平台级，如果是租户级，需要设置租户ID
        if (queryLevel.level() == HiamResourceLevel.ORGANIZATION.level()) {
            menuParams.setTenantId(tenantId);
            menuParams.setupOrganizationQueryLevel();
        }
        List<Menu> menuList = menuMapper.selectSubMenus(menuParams);
        return HiamMenuUtils.formatMenuListToTree(menuList, true);
    }

    @Override
    public Page<AccessAuthDTO> pageMenuAssignRole(Long menuId, Long tenantId, Menu menu, PageRequest pageRequest) {

        // 查一下菜单
        Menu parentMenu = selectByPrimaryKey(menuId);
        Assert.notNull(parentMenu, BaseConstants.ErrorCode.DATA_INVALID);

        // 查menu下面的权限集
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO();
        permissionSetParam.setParentMenuId(menuId);
        permissionSetParam.setTenantId(tenantId);
        List<Menu> permissionSets = menuMapper.selectMenuPermissionSet(permissionSetParam);

        // 查角色列表
        Page<AccessAuthDTO> accessAuthDTOPage = PageHelper.doPageAndSort(pageRequest, () -> menuMapper.listMenuAssignRole(menu, permissionSets.stream().map(Menu::getId).collect(Collectors.toList())));

        // 查角色权限集的勾选情况
        for (AccessAuthDTO accessAuthDTO : accessAuthDTOPage) {
            List<RolePermission> checkPermission = menuMapper.checkRolePermission(accessAuthDTO.getId(), permissionSets.stream().map(Menu::getId).collect(Collectors.toList()));
            List<Menu> permissionsOfMenu = deepCopyMenuList(permissionSets);
            permissionsOfMenu.forEach(item -> {
                if (checkPermission.stream().anyMatch(checked -> checked.getPermissionSetId().equals(item.getId()))) {
                    item.setCheckedFlag(Constants.YesNoFlag.YES);
                }
            });
            permissionsOfMenu.add(parentMenu);
            // 将权限集构建为树、然后返回
            accessAuthDTO.setPsList(HiamMenuUtils.formatMenuListToTree(permissionsOfMenu, true));
        }
        return accessAuthDTOPage;
    }

    @Override
    public List<Menu> queryMenuTree(Long tenantId, Long menuId) {
        // 查一下菜单
        Menu parentMenu = selectByPrimaryKey(menuId);
        Assert.notNull(parentMenu, BaseConstants.ErrorCode.DATA_INVALID);

        // 查menu下面的权限集
        PermissionSetSearchDTO permissionSetParam = new PermissionSetSearchDTO();
        permissionSetParam.setParentMenuId(menuId);
        permissionSetParam.setTenantId(tenantId);
        List<Menu> permissionSets = menuMapper.selectMenuPermissionSet(permissionSetParam);

        // 添加父节点
        permissionSets.add(parentMenu);

        return HiamMenuUtils.formatMenuListToTree(permissionSets, true);
    }

    @Override
    public List<Menu> selectTenantCustomMenuTree(@Nonnull Long tenantId, MenuSearchDTO menuParams) {
        menuParams.setTenantId(tenantId);
        List<Menu> menus = menuMapper.selectTenantCustomMenu(menuParams);
        // 查询父级
        List<Long> idList = menus.stream().map(Menu::getParentId).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<Menu> parentList;
            while (true) {
                parentList = menuMapper.parentMenu(idList);
                if (CollectionUtils.isEmpty(parentList)) {
                    break;
                }
                menus.addAll(parentList);
                idList = parentList.stream().map(Menu::getParentId).distinct().collect(Collectors.toList());
            }
        }
        // 根据主键去重
        menus = menus.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Menu::getId))), ArrayList::new));
        return HiamMenuUtils.formatMenuListToTree(menus, true);
    }

    private List<Menu> deepCopyMenuList(List<Menu> ori) {
        if (CollectionUtils.isEmpty(ori)) {
            return Collections.emptyList();
        }
        List<Menu> result = new ArrayList<>();
        for (Menu menu : ori) {
            Menu copyMenu = new Menu();
            BeanUtils.copyProperties(menu, copyMenu);
            result.add(copyMenu);
        }
        return result;
    }

    /**
     * 查询菜单详情信息 包含菜单权限以及多语言数据 用于菜单的额复制和菜单的导出数据
     *
     * @param queryLevel 查询层级
     * @param level      菜单层级
     * @param menuIds    菜单ID
     * @return
     */
    @Override
    public List<Menu> selectMenuDetail(Long tenantId, HiamResourceLevel queryLevel, HiamResourceLevel level, List<Long> menuIds) {
        MenuSearchDTO menuParams = new MenuSearchDTO();
        if (!CollectionUtils.isEmpty(menuIds)) {
            menuParams.setMenuIds(menuIds);
        }
        menuParams.setTenantId(tenantId);
        //默认是查询层级是平台级
        if (queryLevel.level() == HiamResourceLevel.ORGANIZATION.level()) {
            menuParams.setupOrganizationQueryLevel();
        }
        //设置查询的菜单层级
        menuParams.setLevel(level.value());
        List<Menu> menuList = menuMapper.selectMenusDetail(menuParams);
        if (CollectionUtils.isEmpty(menuList)) {
            return menuList;
        }
        //处理多语言 Menu对象只有name是多语言字段
        // FIX 20200611 菜单添加租户多语言字段 暂不处理  不清楚该处逻辑
        String language = LanguageHelper.language();
        menuList.forEach(menu -> {
            List<MenuTl> menuTls = menu.getMenuTls();
            if (!CollectionUtils.isEmpty(menuTls)) {
                //构造_tls字段
                Map<String, Map<String, String>> _tls = new HashMap<>(1);
                Map<String, String> languageFieldMap = new HashMap<>(menuTls.size());
                //遍历多语言字段对象，获取当前上下文对应的语言对应的值
                menuTls.forEach(menuTl -> {
                    if (language.equals(menuTl.getLang())) {
                        menu.setName(menuTl.getName());
                    }
                    languageFieldMap.put(menuTl.getLang(), menuTl.getName());
                });
                _tls.put(Menu.FIELD_NAME, languageFieldMap);
                menu.set_tls(_tls);
                menu.setMenuTls(null);
            }
        });
        return menuList;
    }

    @Override
    @ProcessLovValue
    public Page<Menu> selectPermissionSets(PermissionSetSearchDTO permissionSetSearchDTO, PageRequest pageRequest) {
        if (!StringUtils.equalsAny(permissionSetSearchDTO.getLevel(), HiamResourceLevel.SITE.value(), HiamResourceLevel.ORGANIZATION.value())) {
            permissionSetSearchDTO.setLevel(null);
        }
        return PageHelper.doPageAndSort(pageRequest, () -> menuMapper.selectPermissionSets(permissionSetSearchDTO));
    }

    @Override
    public List<Permission> selectPermissionSetPermissions(Long tenantId, Long menuId, PermissionSetSearchDTO permissionSetParam) {
        permissionSetParam = Optional.ofNullable(permissionSetParam).orElse(new PermissionSetSearchDTO());
        permissionSetParam.setPermissionSetId(menuId);
        permissionSetParam.setTenantId(tenantId);
        return menuMapper.selectPermissionSetPermissions(permissionSetParam);
    }

    @Override
    @ProcessLovValue
    public List<MenuSiteExportDTO> exportSiteMenuData(MenuSearchDTO menuSearchDTO) {
        menuSearchDTO = Optional.ofNullable(menuSearchDTO).orElse(new MenuSearchDTO());
        menuSearchDTO.setTenantId(Constants.SITE_TENANT_ID);
        menuSearchDTO.setupSiteQueryLevel();
        return menuMapper.selectExportSiteMenuData(menuSearchDTO);
    }

    protected Long getSiteSuperAdminRoleId() {
        return siteSuperAdminRoleId.get();
    }

    protected Long getTenantSuperAdminRoleId() {
        return tenantSuperAdminRoleId.get();
    }
}
