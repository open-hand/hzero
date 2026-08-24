package org.hzero.iam.infra.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.infra.constant.HiamMenuType;

/**
 * 递归解析菜单列表为菜单树的形式
 */
public class HiamMenuUtils {

    private HiamMenuUtils() {}

    /**
     * 菜单管理/菜单展示 - 将菜单格式化成树形
     * 
     * @param menuList 菜单列表
     * @param manageFlag TRUE - 如果为菜单管理之用, 则可以渲染子树; FALSE - 如果为菜单展示之用, 则必须从根节点开始渲染到子节点
     * @return 结构化的菜单列表
     */
    public static List<Menu> formatMenuListToTree(List<Menu> menuList, Boolean manageFlag) {
        if (CollectionUtils.isEmpty(menuList)) {
            return Collections.emptyList();
        }

        // 组装成树状结构
        menuList.forEach(item -> {
            formatMenuTreeInternal(item, menuList);
        });

        // 获取顶点
        List<Menu> rootMenuList = menuList.stream().filter(item -> {
            if (manageFlag) {
                return item.getParentMenu() == null;
            } else {
                return item.getParentMenu() == null
                        && (item.getParentId() == null || item.getParentId() == 0)
                        && (HiamMenuType.ROOT.value().equals(item.getType()) || HiamMenuType.DIR.value().equals(item.getType()))
                        && isDisplay(item);
            }
        }).collect(Collectors.toList());

        return sortMenu(rootMenuList);
    }

    /**
     * 递归形成树状结构<br/>
     */
    private static void formatMenuTreeInternal(Menu parentMenu, List<Menu> menuList) {
        List<Menu> childMenuList = menuList.stream().filter(item -> parentMenu.getId().equals(item.getParentId()))
                        .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(childMenuList)) {
            // 设置父子关系, 并进行下一次递归
            parentMenu.setSubMenus(sortMenu(childMenuList));
            childMenuList.forEach(item -> {
                item.setParentMenu(parentMenu);
                item.setParentName(parentMenu.getName());
                formatMenuTreeInternal(item, menuList);
            });
        }
    }

    /**
     * 检测菜单是否需要展示<br/>
     * 只有存在末级菜单时, 才需要展示
     */
    public static boolean isDisplay(Menu menu) {
        if (HiamMenuType.MENU.value().equals(menu.getType())
                || HiamMenuType.LINK.value().equals(menu.getType())
                || HiamMenuType.INNER_LINK.value().equals(menu.getType())
                || HiamMenuType.WINDOW.value().equals(menu.getType())
        ) {
            // 递归结束条件 #1
            return true;
        } else {
            List<Menu> subMenuList = menu.getSubMenus();
            if (CollectionUtils.isEmpty(subMenuList)) {
                // 递归结束条件 #2
                return false;
            } else {
                List<Menu> displaySubMenuList = new ArrayList<>();
                subMenuList.forEach(item -> {
                    boolean isDisplay = isDisplay(item);
                    if (isDisplay) {
                        displaySubMenuList.add(item);
                    }
                });
                if (CollectionUtils.isEmpty(displaySubMenuList)) {
                    // 递归结束条件 #3
                    return false;
                } else {
                    menu.setSubMenus(displaySubMenuList);
                    return true;
                }
            }
        }
    }

    /**
     * 菜单排序
     */
    public static List<Menu> sortMenu(List<Menu> menuList) {
        menuList.sort((Menu m1, Menu m2) -> {
            if (m1.getSort() == null) {
                return -1;
            }
            if (m2.getSort() == null || m1.getSort() > m2.getSort()) {
                return 1;
            }
            if (m1.getSort().equals(m2.getSort())) {
                return 0;
            }
            return -1;
        });
        return menuList;
    }

    /**
     * 将树形菜单解析成列表形式<br/>
     */
    public static List<Menu> formatMenuTreeToList(List<Menu> menuTreeList) {
        if (CollectionUtils.isEmpty(menuTreeList)) {
            return Collections.emptyList();
        }
        List<Menu> menuList = new ArrayList<>();
        for (Menu menu : menuTreeList) {
            menuList.add(menu);
            if (CollectionUtils.isNotEmpty(menu.getSubMenus())) {
                for (Menu subMenu : menu.getSubMenus()) {
                    subMenu.setParentId(menu.getParentId());
                    menuList.add(subMenu);
                    menuList.addAll(formatMenuTreeToList(subMenu.getSubMenus()));
                }
            }
        }
        return menuList;
    }

}
