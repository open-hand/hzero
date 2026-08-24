package org.hzero.platform.app.service.impl;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.ProfileValueService;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.platform.domain.repository.ProfileRepository;
import org.hzero.platform.domain.repository.ProfileValueRepository;
import org.hzero.platform.domain.service.ProfileValueDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 配置维护app层service实现
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/07 19:11
 */
@Service
public class ProfileValueServiceImpl implements ProfileValueService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileValueRepository profileValueRepository;

    @Autowired
    private ProfileValueDomainService profileValueDomainService;

    @Override
    public List<ProfileValue> listByProfileId(PageRequest pageRequest, Long profileId) {
        ProfileValue profileValue = new ProfileValue();
        profileValue.setProfileId(profileId);
        return profileValueRepository.pageAndSort(pageRequest, profileValue);
    }

    @Override
    public String getProfileValueByProfileName(String level, String profileName, Long userId, Long roleId, Long tenantId) {
        return profileValueDomainService.getProfileValueByProfileName(tenantId, profileName, userId, roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long profileValueId, Long organizationId) {
        ProfileValue profileValue = profileValueRepository.selectByPrimaryKey(profileValueId);
        if (profileValue == null) {
            return;
        }
        Profile profile = profileRepository.selectByPrimaryKey(profileValue.getProfileId());
        if (organizationId != null && !organizationId.equals(profile.getTenantId())) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        Assert.notNull(profile, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        profileValueDomainService.delete(profileValueId, profile.getProfileLevel(), profile.getTenantId(), profile.getProfileName(), profileValue.getLevelCode(), profileValue.getLevelValue());
    }
}
