package org.hzero.platform.infra.repository.impl;

import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.ProfileValueDTO;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.platform.domain.repository.ProfileValueRepository;
import org.hzero.platform.domain.vo.ProfileValueVO;
import org.hzero.platform.infra.mapper.ProfileValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yunxiang.zhou01@hand-china.com 2018/06/05
 */
@Component
public class ProfileValueRepositoryImpl extends BaseRepositoryImpl<ProfileValue> implements ProfileValueRepository {

    @Autowired
    private ProfileValueMapper profileValueMapper;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<ProfileValueVO> selectAllProfileAndProfileValue() {
        return profileValueMapper.selectAllProfileAndProfileValue();
    }

    @Override
    public void initAllProfileValueToRedis() {
        SecurityTokenHelper.close();
        List<ProfileValueVO> profileValueVOList = profileValueMapper.selectAllProfileAndProfileValue();
        profileValueVOList.forEach(vo -> {
            String key = Profile.generateCacheKey(vo.getTenantId(), vo.getProfileName(),
                            vo.getLevelCode(), vo.getLevelValue());
            redisHelper.strSet(key, vo.getValue());
        });
        SecurityTokenHelper.clear();
    }

    @Override
    public List<ProfileValueDTO> selectProfileValue(Long profileId) {
        return profileValueMapper.selectProfileValue(profileId);
    }
}
