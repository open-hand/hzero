package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Profile;
import org.hzero.iam.domain.vo.ProfileVO;
import org.springframework.stereotype.Repository;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/20 14:20
 */
@Repository
public interface HiamProfileMapper extends BaseMapper<Profile> {

    /**
     * 查询配置信息
     *
     * @param tenantId     租户ID
     * @param profileName  配置名
     * @return
     */
    List<ProfileVO> queryProfileVO(@Param("tenantId") Long tenantId,
                                   @Param("profileName") String profileName);
}
