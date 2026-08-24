package org.hzero.iam.domain.service.user;

import java.util.List;

/**
 * <p>
 * 当前用户角色管理接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 15:36
 */
public interface UserDetailsService {
    /**
     * 查询当前用户的当前角色
     *
     * @return 当前角色
     */
    Long readUserRole();

    /**
     * 查询当前用户的当前租户
     *
     * @return 当前租户
     */
    Long readUserTenant();

    /**
     * 更新用户的当前角色
     *
     * @param roleId 当前角色
     */
    void storeUserRole(Long roleId);

    /**
     * 更新用户的当前租户
     *
     * @param tenantId 当前租户
     */
    void storeUserTenant(Long tenantId);

    /**
     * 更新用户的当前语言
     *
     * @param language 当前语言
     */
    void storeUserLanguage(String language);

    /**
     * 更新用户的当前时区
     *
     * @param timeZone 当前时区
     */
    void storeUserTimeZone(String timeZone);

    /**
     * 刷新可访问租户列表和可选角色列表
     *
     * @param loginNameList 登录名列表
     */
    void refresh(List<String> loginNameList);
}
