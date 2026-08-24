package org.hzero.starter.social.core.provider;

import java.util.List;

import org.hzero.starter.social.core.common.connect.SocialUserData;

/**
 * 用户与三方应用绑定关系
 *
 * @author bojiangzhou 2019/08/30
 */
public interface SocialUserProviderRepository {

    /**
     * 查找三方账号关联的用户名
     * 
     * @param providerId 三方类型
     * @param providerUserId 三方账号openId
     * @return 用户名
     */
    String findUsernameByProviderId(String providerId, String providerUserId);

    /**
     * 查询 unionId 关联的用户
     * @param providerId 三方类型
     * @param providerUnionId 三方账号unionId
     * @return 用户名集合
     */
    List<String> findUsernameByUnionId(String providerId, String providerUnionId);

    /**
     *
     * @param providerId 三方类型
     * @param username 用户名
     * @return 三方账号
     */
    List<SocialUserData> findProviderUser(String providerId, String username);

    /**
     * 更新用户绑定信息
     * 
     * @param username 用户名
     * @param providerId 三方类型
     * @param providerUserId 三方账号
     * @param data 三方账号信息
     */
    default void updateUserBind(String username, String providerId, String providerUserId, SocialUserData data) {}

    /**
     * 创建用户绑定信息
     *
     * @param username 用户名
     * @param providerId 三方类型
     * @param providerUserId 三方账号
     * @param data 三方账号信息
     */
    void createUserBind(String username, String providerId, String providerUserId, SocialUserData data);

}
