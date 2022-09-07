package org.hzero.message.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.api.dto.UserReceiveConfigDTO;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.message.domain.vo.UserInfoVO;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 用户接收配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
public interface UserReceiveConfigMapper extends BaseMapper<UserReceiveConfig> {

    /**
     * 根据用户id查询用户配置
     *
     * @param userId   用户Id
     * @param tenantId 租户Id
     * @return 用户配置
     */
    List<UserReceiveConfigDTO> listConfigByUserId(@Param("userId") Long userId,
                                                  @Param("tenantId") Long tenantId);

    /**
     * 查询父节点
     *
     * @param userId      用户Id
     * @param receiveCode 编码
     * @return 用户配置
     */
    UserReceiveConfig selectParent(@Param("userId") Long userId,
                                   @Param("receiveCode") String receiveCode);

    /**
     * 查询子节点
     *
     * @param userId      用户Id
     * @param receiveCode 编码
     * @return 用户配置
     */
    List<UserReceiveConfig> selectChildren(@Param("userId") Long userId,
                                           @Param("receiveCode") String receiveCode);

    /**
     * 根据编码删除
     *
     * @param receiveCode 编码
     */
    void deleteByCode(@Param("receiveCode") String receiveCode);

    /**
     * 查询用户信息
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 用户信息
     */
    List<UserInfoVO> getUserInfo(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    /**
     * 获取所接收配置下有租户ID
     *
     * @return 租户ID
     */
    List<Long> listAllTenantId();
}
