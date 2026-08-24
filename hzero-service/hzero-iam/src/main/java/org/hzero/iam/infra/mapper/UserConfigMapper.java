package org.hzero.iam.infra.mapper;


import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.UserConfig;

import java.util.List;

/**
 * 用户默认配置Mapper
 *
 * @author zhiying.dong@hand-china.com 2018-09-14 10:46:53
 */
public interface UserConfigMapper extends BaseMapper<UserConfig> {
    /**
     * 查询用户默认配置列表
     *
     * @param userId 用户ID
     * @return 用户默认配置列表
     */
    List<UserConfig> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户默认配置明细
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 用户默认配置
     */
    UserConfig queryUserConfig(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
