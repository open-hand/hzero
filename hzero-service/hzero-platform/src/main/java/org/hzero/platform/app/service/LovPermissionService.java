package org.hzero.platform.app.service;

import java.util.Arrays;
import java.util.Collection;

/**
 * 值集访问权限服务。
 *
 * @author 25287
 */
public interface LovPermissionService {

    /**
     * 校验当前登录成员是否至少拥有一个角色，不查询值集权限数据。
     *
     * @return 当前登录成员拥有角色时返回 {@code true}
     */
    boolean checkRole();

    /**
     * 校验当前登录成员是否拥有全部指定值集的访问权限。
     *
     * @param lovCodes 值集编码
     * @return 全部值集均有权限时返回 {@code true}
     */
    boolean checkPermission(Collection<String> lovCodes);

    /**
     * 校验当前登录成员是否拥有全部指定值集的访问权限。
     *
     * @param lovCodes 值集编码
     * @return 全部值集均有权限时返回 {@code true}
     */
    default boolean checkPermission(String... lovCodes) {
        return lovCodes != null && checkPermission(Arrays.asList(lovCodes));
    }
}
