package org.hzero.iam.domain.repository;


import java.util.List;

import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 用户默认配置资源库
 *
 * @author zhiying.dong@hand-china.com 2018-09-14 10:46:53
 */
public interface UserConfigRepository extends BaseRepository<UserConfig> {
    /**
     * 创建用户默认配置
     *
     * @param userConfig UserConfig
     */
    void createUserConfig(UserConfig userConfig);

    /**
     * 查询用户默认配置列表
     *
     * @param userId 用户ID
     * @return 用户默认配置列表
     */
    List<UserConfig> selectByUserId(Long userId);

    /**
     * 查询用户默认配置明细
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 用户默认配置
     */
    UserConfig queryUserConfig(Long userId, Long tenantId);

    /**
     * 更新用户默认配置
     *
     * @param record UserConfig
     */
    void updateUserConfig(UserConfig record);

    /**
     * 根据 userId + tenantId，没有就新增，有就更新
     */
    UserConfig createOrUpdate(UserConfig config);
}
