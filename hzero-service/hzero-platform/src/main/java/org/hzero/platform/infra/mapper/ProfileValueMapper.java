package org.hzero.platform.infra.mapper;

import org.hzero.platform.api.dto.ProfileValueDTO;
import org.hzero.platform.domain.entity.ProfileValue;

import io.choerodon.mybatis.common.BaseMapper;
import org.hzero.platform.domain.vo.ProfileValueVO;

import java.util.List;

/**
 * <p>
 * 配置维护值mapper
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/12 11:48
 */
public interface ProfileValueMapper extends BaseMapper<ProfileValue> {

    /**
     * 查询出所有的配置维护值集
     *
     * @return 配置维护值集dto list
     * @author yunxiang.zhou01@hand-china.com 2018-06-07 19:18
     */
    List<ProfileValueVO> selectAllProfileAndProfileValue();

    /**
     * 查询带有层级值描述的配置维护
     *
     * @param profileId 配置维护id
     * @return 带有层级值描述的配置维护list
     */
    List<ProfileValueDTO> selectProfileValue(Long profileId);
}
