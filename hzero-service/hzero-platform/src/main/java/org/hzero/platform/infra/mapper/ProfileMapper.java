package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.ProfileDTO;
import org.hzero.platform.domain.entity.Profile;

import io.choerodon.mybatis.common.BaseMapper;


/**
 * <p>
 * 配置维护mapper
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/12 11:48
 */
public interface ProfileMapper extends BaseMapper<Profile> {

    /**
     * 查询带描述的profile
     *
     * @param profile profile
     * @return profileDTO
     */
    List<ProfileDTO> selectProfile(Profile profile);

    /**
     * 模糊查询配置维护
     *
     * @param profile 配置维护
     * @return 配置维护list
     */
    List<ProfileDTO> selectProfileLike(Profile profile);

    /**
     * 查询租户级配置维护
     *
     * @param profile
     * @return
     */
    List<ProfileDTO> selectTenantProfileLike(Profile profile);

    /**
     * 按照层级依次查询配置维护值
     *
     * @param tenantId 租户Id
     * @param profileName 配置名称
     * @param levelCode 层级编码
     * @param levelValue 层级值
     * @return 配置维护值
     */
    String selectProfileValueByOptions(@Param("tenantId") Long tenantId, @Param("profileName") String profileName,
                    @Param("levelCode") String levelCode, @Param("levelValue") String levelValue);
}
