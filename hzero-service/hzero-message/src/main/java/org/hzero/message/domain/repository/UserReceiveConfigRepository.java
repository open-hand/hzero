package org.hzero.message.domain.repository;

import org.hzero.message.api.dto.UserReceiveConfigDTO;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.message.domain.vo.UserInfoVO;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 用户接收配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
public interface UserReceiveConfigRepository extends BaseRepository<UserReceiveConfig> {

    /**
     * 根据用户id查询配置
     *
     * @param userId   用户Id
     * @param tenantId 租户Id
     * @return 配置
     */
    List<UserReceiveConfigDTO> listUserConfig(Long userId, Long tenantId);

    /**
     * 查询父节点
     *
     * @param userId      用户Id
     * @param receiveCode 编码
     * @return 父节点
     */
    UserReceiveConfig selectParent(Long userId, String receiveCode);

    /**
     * 查询子节点
     *
     * @param userId      用户Id
     * @param receiveCode 编码
     * @return 子节点
     */
    List<UserReceiveConfig> selectChildren(Long userId, String receiveCode);

    /**
     * 删除
     *
     * @param receiveCode 编码
     */
    void deleteByCode(String receiveCode);

    /**
     * 查询用户信息
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId, Long tenantId);

    /**
     * 获取所接收配置下有租户ID
     *
     * @return 租户ID
     */
    List<Long> listAllTenantId();

}
