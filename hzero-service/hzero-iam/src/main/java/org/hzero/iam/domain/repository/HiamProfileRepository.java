package org.hzero.iam.domain.repository;


import java.util.List;

import org.hzero.iam.domain.entity.Profile;
import org.hzero.iam.domain.vo.ProfileVO;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/20 14:10
 */
public interface HiamProfileRepository extends BaseRepository<Profile> {

    /**
     * 查询配置信息
     *
     * @param tenantId    租户ID
     * @param profileName 配置名
     * @return 配置信息
     */
    List<ProfileVO> queryProfileVO(Long tenantId, String profileName);
}
