package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.ProfileValueDTO;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.platform.domain.vo.ProfileValueVO;

/**
 * <p>
 * 配置维护值仓储层
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/07 19:49
 */
public interface ProfileValueRepository extends BaseRepository<ProfileValue> {

    /**
     * 查询出所有的配置维护值集
     *
     * @author yunxiang.zhou01@hand-china.com 2018-06-07 19:18
     * @return 配置维护值集dto list
     */
    List<ProfileValueVO> selectAllProfileAndProfileValue();

    /**
     * 查出所有的配置维护初始化到redis中
     */
    void initAllProfileValueToRedis();

    /**
     * 查询带有层级值描述的配置维护
     *
     * @param profileId 配置维护id
     * @return 带有层级值描述的配置维护list
     */
    List<ProfileValueDTO> selectProfileValue(Long profileId);
}
