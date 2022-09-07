package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.hzero.iam.domain.entity.Profile;
import org.hzero.iam.domain.repository.HiamProfileRepository;
import org.hzero.iam.domain.vo.ProfileVO;
import org.hzero.iam.infra.mapper.HiamProfileMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/20 14:11
 */
@Repository
public class HiamProfileRepositoryImpl extends BaseRepositoryImpl<Profile> implements HiamProfileRepository {

    @Autowired
    private HiamProfileMapper profileMapper;

    @Override
    public List<ProfileVO> queryProfileVO(Long tenantId, String profileName) {
        return profileMapper.queryProfileVO(tenantId,profileName);
    }
}
