package org.hzero.iam.infra.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.Role;

/**
 * 递归解析角色列表为角色树的形式
 */
public class HiamRoleUtils {

    private HiamRoleUtils() {}

    /**
     * 将角色列表转为树形结构
     *
     * @param roleList 角色列表
     * @param inherited true-返回继承树; false-返回创建树
     * @return 结构化的角色列表
     */
    public static List<Role> formatRoleListToTree(List<Role> roleList, boolean inherited) {
        if (CollectionUtils.isEmpty(roleList)) {
            return Collections.emptyList();
        }

        // 组装成树状结构
        if (inherited) {
            roleList.forEach(item -> {
                formatInheritRoleTreeInternal(item, roleList);
            });
            // 获取顶点
            return roleList.stream().filter(item -> item.getInheritRole() == null).collect(Collectors.toList());
        } else {
            roleList.forEach(item -> {
                formatCreatedRoleTreeInternal(item, roleList);
            });
            // 获取顶点
            return roleList.stream().filter(item -> item.getParentRole() == null).collect(Collectors.toList());
        }
    }

    /**
     * 递归形成创建树结构<br/>
     */
    private static void formatCreatedRoleTreeInternal(Role parentRole, List<Role> roleList) {
        List<Role> childList = roleList.stream().filter(item -> parentRole.getId().equals(item.getParentRoleId()))
                        .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(childList)) {
            // 设置父子关系, 并进行下一次递归
            parentRole.setCreatedSubRoles(childList);
            childList.forEach(item -> {
                item.setParentRole(parentRole);
                formatCreatedRoleTreeInternal(item, roleList);
            });
        }
    }

    /**
     * 递归形成继承树状结构<br/>
     */
    private static void formatInheritRoleTreeInternal(Role inheritRole, List<Role> roleList) {
        List<Role> childList = roleList.stream().filter(item -> inheritRole.getId().equals(item.getInheritRoleId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(childList)) {
            // 设置父子关系, 并进行下一次递归
            inheritRole.setInheritSubRoles(childList);
            childList.forEach(item -> {
                item.setInheritRole(inheritRole);
                formatInheritRoleTreeInternal(item, roleList);
            });
        }
    }

}
