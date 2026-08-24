package org.hzero.platform.domain.repository;

import java.util.List;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.ProfileDTO;
import org.hzero.platform.domain.entity.Profile;

/**
 * 配置维护仓储层接口
 *
 * @author yunxiang.zhou01 @hand-china.com 2018/06/05
 */
public interface ProfileRepository extends BaseRepository<Profile> {

    /**
     * 根据主键id查询
     *
     * @param profile 配置维护
     * @return 配置维护
     */
    Profile query(Profile profile);

    /**
     * 查询带描述的profile
     *
     * @param profile
     * @return
     */
    ProfileDTO selectProfile(Profile profile);

    /**
     * 模糊查询配置维护
     *
     * @param pageRequest 分页
     * @param profile 配置维护
     * @return 配置维护list
     */
    List<ProfileDTO> selectProfileLike(PageRequest pageRequest, Profile profile);

    /**
     * 模糊查询租户配置维护
     *
     * @param pageRequest 分页
     * @param profile 配置维护
     * @return 配置维护list
     */
    List<ProfileDTO> selectTenantProfileLike(PageRequest pageRequest, Profile profile);

    /**
     * 获取最低层次的获取配置维护值， 用户级 < 角色级 < 租户级
     *
     * @param tenantId     租户Id
     * @param profileName  配置维护名称
     * @param userId       用户Id
     * @param roleId       角色Id
     * @return  profileValue
     */
    String getProfileValueByLevel(Long tenantId, String profileName, Long userId, Long roleId);
}
