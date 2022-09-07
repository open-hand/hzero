package org.hzero.platform.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.ProfileService;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.repository.ProfileRepository;
import org.hzero.platform.domain.service.ProfileDomainService;
import org.hzero.platform.domain.service.ProfileValueDomainService;
import org.hzero.platform.infra.constant.FndConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 配置维护serviceImpl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/07 19:04
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileDomainService profileDomainService;

    @Autowired
    private ProfileValueDomainService profileValueDomainService;

    @Override
    public List<Profile> list(PageRequest pageRequest, Profile profile) {
        return profileRepository.pageAndSort(pageRequest, profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Profile insertOrUpdate(Profile profile) {
        profile.validate();
        return profileDomainService.insertOrUpdate(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long profileId, Long tenantId, Boolean isPlatform) {
        Profile profile = profileRepository.selectByPrimaryKey(profileId);
        if (!isPlatform && !tenantId.equals(profile.getTenantId())) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        profileDomainService.delete(profile);
        profileValueDomainService.deleteProfileValueByProfileId(profile);
    }
}
