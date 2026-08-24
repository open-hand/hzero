package org.hzero.iam.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.codec.Charsets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.iam.api.dto.MenuCopyDataDTO;
import org.hzero.iam.api.dto.MenuSearchDTO;
import org.hzero.iam.api.dto.MenuSiteExportDTO;
import org.hzero.iam.app.service.MenuService;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.MenuPermission;
import org.hzero.iam.domain.repository.MenuPermissionRepository;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.service.MenuCoreService;
import org.hzero.iam.infra.common.utils.HiamMenuUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamMenuType;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.PermissionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bojiangzhou 2019/01/18
 * @author allen 2018/7/2
 */
@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Autowired
    private MenuCoreService menuCoreService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MenuPermissionRepository menuPermissionRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Menu createMenuInSite(Menu menu) {
        menu.setTenantId(Constants.SITE_TENANT_ID);
        // 平台创建菜单为标准菜单
        menu.setCustomFlag(BaseConstants.Flag.NO);
        return menuCoreService.createMenu(menu, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Menu createMenuInTenant(Long tenantId, Menu menu) {
        Assert.notNull(tenantId, "tenantId should not be null.");
        menu.setTenantId(tenantId);
        // 租户级菜单只能是组织层
        menu.setLevel(HiamResourceLevel.ORGANIZATION.value());
        // 租户级菜单都设置为客制化菜单
        menu.setCustomFlag(BaseConstants.Flag.YES);

        return menuCoreService.createMenu(menu, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMenuForCopy(HiamResourceLevel queryLevel, HiamResourceLevel level, MenuCopyDataDTO menuCopyData) {
        Long targetTenantId = menuCopyData.getTargetTenantId();
        Long sourceTenantId = menuCopyData.getSourceTenantId();

        List<Long> copyMenuIds = menuCopyData.getCopyMenuIds();
        if (CollectionUtils.isEmpty(copyMenuIds)) {
            return;
        }
        //查询来源菜单数据
        List<Menu> copyData = menuRepository.selectMenuDetail(sourceTenantId, queryLevel, level, copyMenuIds);
        if (CollectionUtils.isEmpty(copyData)) {
            return;
        }

        Menu newRootMenu = menuCopyData.getRootMenu();
        Assert.notNull(newRootMenu, BaseConstants.ErrorCode.DATA_INVALID);
        // 复制到目标租户下
        newRootMenu.setTenantId(targetTenantId);
        //重新构建树
        List<Menu> sourceMenuTreeList = HiamMenuUtils.formatMenuListToTree(copyData, true);
        //复制的数据只能是一棵树，否则数据有问题
        Assert.isTrue(sourceMenuTreeList.size() == 1, BaseConstants.ErrorCode.DATA_INVALID);
        Menu sourceRootMenu = sourceMenuTreeList.get(0);
        //单独用一个字符串保存，是为了替换menuCode时候不受影响
        String oldRootMenuCode = sourceRootMenu.getCode();
        //替换所有的menuCode并设置目标租户
        copyData.forEach(item -> {
            if (item.getCode().startsWith(oldRootMenuCode)) {
                item.setCode(item.getCode().replace(oldRootMenuCode, newRootMenu.getCode()));
            } else {
                //兼容原始数据做出的妥协，如果子菜单的code不是以父级菜单结尾的，则取子菜单code的最后一个分隔符后面的内容
                //并拼接到新的菜单前缀
                item.setCode(newRootMenu.getCode() + item.getCode().substring(item.getCode().lastIndexOf(Menu.MENU_CODE_SPLIT)));
            }

            item.setTenantId(targetTenantId);
        });

        //先根据复制的数据构造出树形，然后用新的根节点替换树形的根节点即可
        List<Menu> newMenuTreeList = new ArrayList<>(1);
        newRootMenu.setSubMenus(sourceRootMenu.getSubMenus());
        newRootMenu.setLevel(sourceRootMenu.getLevel());
        newRootMenu.setCustomFlag(sourceRootMenu.getCustomFlag());
        newMenuTreeList.add(newRootMenu);

        List<MenuPermission> menuPermissionList = new ArrayList<>(1024);
        Long userId = Optional.ofNullable(DetailsHelper.getUserDetails()).map(CustomUserDetails::getUserId).orElse(-1L);

        //递归插入
        recursiveInsertMenuTree(newMenuTreeList, menuPermissionList, userId);

        LOGGER.debug("Copy menu_permission, size: {}", menuPermissionList.size());
        // 插入菜单权限
        menuPermissionRepository.batchInsertBySql(menuPermissionList);
    }

    @Override
    public void insertCustomMenu(Long tenantId, Menu menu) {
        Assert.notNull(tenantId, "tenantId should not be null.");
        menu.setTenantId(tenantId);
        // 租户级菜单只能是组织层
        menu.setLevel(HiamResourceLevel.ORGANIZATION.value());
        // 租户级菜单都设置为客制化菜单
        menu.setCustomFlag(BaseConstants.Flag.YES);
        menuCoreService.createMenu(menu, true);
    }

    @Override
    public List<MenuSiteExportDTO> exportSiteMenuData(MenuSearchDTO menuSearchDTO) {
        return menuRepository.exportSiteMenuData(menuSearchDTO);
    }

    /**
     * 递归插入菜单树
     *
     * @param menuTreeList 菜单树
     */
    private void recursiveInsertMenuTree(List<Menu> menuTreeList, List<MenuPermission> menuPermissionList, Long userId) {
        for (Menu menuTree : menuTreeList) {
            //置空主键
            menuTree.setId(null);
            List<Menu> subMenus = menuTree.getSubMenus();
            menuTree.setSubMenus(null);
            menuTree.setParentMenu(null);
            menuCoreService.createMenu(menuTree, false);
            //插入权限
            List<MenuPermission> menuPermissions = menuTree.getMenuPermissions();
            if (CollectionUtils.isNotEmpty(menuPermissions)) {
                menuPermissions.forEach(item -> {
                    item.setMenuId(menuTree.getId());
                    item.setTenantId(menuTree.getTenantId());
                    item.setCreatedBy(userId);
                    item.setLastUpdatedBy(userId);
                });
                menuPermissionList.addAll(menuPermissions);
            }
            if (CollectionUtils.isNotEmpty(subMenus)) {
                //继续插入子节点
                subMenus.forEach(item -> {
                    item.setParentId(menuTree.getId());
                });
                recursiveInsertMenuTree(subMenus, menuPermissionList, userId);
            }
        }
    }


    @Override
    public void checkDuplicate(Menu menu) {
        menu.initMenu();
        menuCoreService.checkMenuExists(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Menu update(Menu menu) {
        return menuCoreService.updateMenu(menu);
    }

    @Override
    public Menu updateCustomMenu(Long tenantId, Menu menu) {
        Assert.notNull(menu.getId(), "menuId must not be null when update menu.");
        Menu db = menuRepository.selectByPrimaryKey(menu.getId());
        Assert.notNull(db, "menuId must not be null when update menu.");
        if (Objects.equals(db.getCustomFlag(), BaseConstants.Flag.NO) || !Objects.equals(tenantId, db.getTenantId())) {
            throw new CommonException("error.menu.update");
        }
        return menuCoreService.updateMenu(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(Long tenantId, Long menuId) {
        menuCoreService.deleteMenuById(tenantId, menuId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void enableMenu(Long tenantId, Long menuId) {
        menuCoreService.changeEnableFlag(tenantId, menuId, BaseConstants.Flag.YES);
    }

    @Override
    public void enableCustomMenu(Long tenantId, Long menuId) {
        Menu db = menuRepository.selectByPrimaryKey(menuId);
        Assert.notNull(db, "menuId must not be null when update menu.");
        if (Objects.equals(db.getCustomFlag(), BaseConstants.Flag.NO) || !Objects.equals(tenantId, db.getTenantId())) {
            throw new CommonException("error.menu.update");
        }
        menuCoreService.changeEnableFlag(tenantId, menuId, BaseConstants.Flag.YES);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void disableMenu(Long tenantId, Long menuId) {
        menuCoreService.changeEnableFlag(tenantId, menuId, BaseConstants.Flag.NO);
    }

    @Override
    public void disableCustomMenu(Long tenantId, Long menuId) {
        Menu db = menuRepository.selectByPrimaryKey(menuId);
        Assert.notNull(db, "menuId must not be null when update menu.");
        if (Objects.equals(db.getCustomFlag(), BaseConstants.Flag.NO) || !Objects.equals(tenantId, db.getTenantId())) {
            throw new CommonException("error.menu.update");
        }
        menuCoreService.changeEnableFlag(tenantId, menuId, BaseConstants.Flag.NO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignPsPermissions(Long permissionSetId, PermissionType permissionType, String[] permissionCodes) {
        menuCoreService.assignPsPermissions(permissionSetId, permissionType, permissionCodes);
    }

    @Override
    public void assignPsPermissions(Long tenantId, String code, String level, PermissionType permissionType, String[] permissionCodes) {
        Menu ps = selectOne(tenantId, code, level);
        this.assignPsPermissions(ps.getId(), permissionType, permissionCodes);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void recyclePsPermissions(Long permissionSetId, String[] permissionCodes, PermissionType permissionType) {
        menuCoreService.recyclePsPermissions(permissionSetId, permissionCodes, permissionType);
    }

    @Override
    public void recyclePsPermissions(Long tenantId, String code, String level, String[] permissionCodes, PermissionType permissionType) {
        Menu ps = selectOne(tenantId, code, level);
        this.recyclePsPermissions(ps.getId(), permissionCodes, permissionType);
    }

    private Menu selectOne(Long tenantId, String code, String level) {
        Menu params = new Menu();
        params.setTenantId(tenantId);
        params.setCode(code);
        params.setLevel(level);
        // 通过编码查找权限集
        Menu menu = menuRepository.selectOne(params);
        if (null == menu) {
            throw new CommonException("hiam.warn.menu.notFoundByCode");
        }
        return menu;
    }

    @Override
    public Map<String, Object> fixMenuData(boolean initAll) {
        Map<String, Object> result = new HashMap<>();

        int updateMenuLevelPathCount = 0;
        for (HiamMenuType menuType : HiamMenuType.values()) {
            updateMenuLevelPathCount += menuCoreService.initLevelPath(menuType, initAll);
        }

        result.put("updateMenu[levelPath]Count", updateMenuLevelPathCount);
        return result;
    }

    /**
     * @param menuTreeList 待导出的树形结构数据
     * @param response     HttpServletResponse对象
     * @throws IOException
     */
    @Override
    public void handleCustomMenuExportData(Long tenantId, List<Menu> menuTreeList, HttpServletResponse response) throws IOException {
        if (CollectionUtils.isEmpty(menuTreeList)) {
            return;
        }
        menuTreeList.forEach(item -> {
            //正常的前端操作是不会有问题的，所以无需多语言报错
            Assert.isTrue(Objects.equals(tenantId, item.getTenantId()), BaseConstants.ErrorCode.DATA_INVALID);
        });

        response.setStatus(HttpStatus.OK.value());
        String fileName = new StringBuilder(Menu.MENU_EXPORT_FILE_PREFIX).append(System.currentTimeMillis()).append(Menu.MENU_EXPORT_FILE_TYPE).toString();

        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(Charsets.UTF_8.displayName()), Charsets.UTF_8.displayName()));


        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(redisHelper.toJson(menuTreeList).getBytes(Charsets.UTF_8.displayName()));
        outputStream.close();
    }

    @Override
    public List<Menu> handleCustomMenuImportData(Long tenantId, MultipartFile customMenuFile) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = customMenuFile.getInputStream();
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8.displayName()));
        try {
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            bf.close();
        }

        ObjectMapper objectMapper = RedisHelper.getObjectMapper();
        List<Menu> menuList = objectMapper.readValue(stringBuilder.toString(), new TypeReference<List<Menu>>() {
        });
        if (CollectionUtils.isEmpty(menuList)) {
            return menuList;
        }
        //由于前端传的是打平的数据，所以需要重新构建树
        menuList.forEach(item -> item.setSubMenus(null));
        List<Menu> menuTreeList = HiamMenuUtils.formatMenuListToTree(menuList, true);

        //处理根节点
        for (Menu menuTree : menuTreeList) {
            //如果根节点不为0，需要先查出所挂的父级菜单的ID，查不到则报错
            if (!Menu.ROOT_ID.equals(menuTree.getParentId())) {
                String parentCode = menuTree.getParentCode();
                if (StringUtils.isEmpty(parentCode)) {
                    throw new CommonException("error.parentMenuCode.null", menuTree.getCode());
                }
                Menu queryParam = new Menu();
                queryParam.setCode(parentCode);
                queryParam.setTenantId(menuTree.getParentTenantId());
                //客户化菜单的父级菜单必定为租户级
                queryParam.setLevel(HiamResourceLevel.ORGANIZATION.value());
                Menu parentMenu = menuRepository.selectOne(queryParam);
                if (parentMenu == null) {
                    throw new CommonException("error.parentMenu.not.exist", menuTree.getCode());
                }
                menuTree.setParentId(parentMenu.getParentId());
            }
        }

        return menuTreeList;
    }

    /**
     * 递归导入菜单树上的所有节点
     *
     * @param menuTreeList 具有树形结构的菜单树
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importMenuTree(Long tenantId, List<Menu> menuTreeList) {
        for (Menu menuTree : menuTreeList) {
            //判断是更新还是新增
            Menu menu = menuRepository.selectMenuUnique(tenantId, menuTree.getCode(), HiamResourceLevel.ORGANIZATION.value());
            if (menu == null) {
                //新增
                menuTree.setTenantId(tenantId);
                menuTree.setId(null);
                // 租户级菜单只能是组织层
                menuTree.setLevel(HiamResourceLevel.ORGANIZATION.value());
                // 租户级菜单都设置为客制化菜单
                menuTree.setCustomFlag(BaseConstants.Flag.YES);

                /*
                 * fix: 2020/11/27 新建创建的菜单对象，将父级子级设置为空，防止父级子级菜单导致SecurityToken递归校验导致栈溢出
                 * 之所以不用SecurityToken.close()，是因为创建菜单的过程中会多次查询菜单相关数据，会导致SecurityToken.close()的线程变量被清理
                 * 详情请参照org.hzero.mybatis.security.SecurityTokenInterceptor.intercept逻辑
                 */
                Menu newMenu = new Menu();
                BeanUtils.copyProperties(menuTree, newMenu);
                newMenu.setParentMenu(null);
                newMenu.setSubMenus(null);
                menuCoreService.createMenu(newMenu, false);
                // 回写ID
                menuTree.setId(newMenu.getId());
            } else {
                if (BaseConstants.Flag.NO.equals(menu.getCustomFlag())) {
                    //防止手动篡改影响到标准菜单
                    throw new CommonException("error.menu.illegal-data", menuTree.getCode());
                }
                //复制指定字段更新
                menu.setName(menuTree.getName());
                menu.setQuickIndex(menuTree.getQuickIndex());
                menu.setRoute(menuTree.getRoute());
                menu.setSort(menuTree.getSort());
                menu.setIcon(menuTree.getIcon());
                menu.setDescription(menuTree.getDescription());
                menu.setVirtualFlag(menuTree.getVirtualFlag());
                menu.setEnabledFlag(menuTree.getEnabledFlag());
                menuCoreService.updateMenu(menu);

                //清空其权限,后续会重新插入。
                menuPermissionRepository.deleteByMenuId(menu.getId());

            }
            //插入权限
            List<MenuPermission> menuPermissions = menuTree.getMenuPermissions();
            if (CollectionUtils.isNotEmpty(menuPermissions)) {
                menuPermissions.forEach(item -> {
                    item.setMenuId(menuTree.getId());
                    item.setTenantId(menuTree.getTenantId());
                });
                menuPermissionRepository.batchInsert(menuPermissions);
            }

            List<Menu> subMenus = menuTree.getSubMenus();
            if (CollectionUtils.isNotEmpty(subMenus)) {
                //继续插入子节点
                subMenus.forEach(item -> item.setParentId(menuTree.getId()));
                importMenuTree(tenantId, subMenus);
            }
        }
    }

    @Override
    public Map<Long, String> queryMenus(Menu menu) {
        // 查询菜单
        return this.menuRepository.select(menu)
                .parallelStream()
                .collect(Collectors.toMap(Menu::getId, Menu::getName));
    }
}
