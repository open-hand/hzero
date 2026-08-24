package org.hzero.platform.domain.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.platform.domain.repository.ProfileRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 配置维护值领域层service
 * </p>
 * 
 * @author yunxiang.zhou01@hand-china.com 2018/06/07 19:10
 */
@Component
public class ProfileDomainService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileValueDomainService profileValueDomainService;

    public Profile insertOrUpdate(Profile profile) {
        // 判断是否有主键，如果无则插入数据
        if (profile.judgeInsert()) {
            profile = this.create(profile);
        } else {
            // 更新配置维护头数据时需要校验数据合法性
            Profile tempProfile = profileRepository.selectByPrimaryKey(profile.getProfileId());
            if (tempProfile == null || !Objects.equals(profile.getTenantId(), tempProfile.getTenantId())){
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
            }
            // 限制仅可更新配置维护头描述信息
            profileRepository.updateOptional(profile, Profile.FIELD_DESCRIPTION);
        }
        List<ProfileValue> profileValueList = profile.getProfileValueList();
        // 将插入之后获取id的profileValue替代原本无主键的
        if (CollectionUtils.isNotEmpty(profileValueList)) {
            for (ProfileValue profileValue : profileValueList) {
                // 判断配置维护值是否有主键，有就插入，否则更新
                if (profileValue.judgeInsert()) {
                    // 设置头表主键id
                    profileValue.setProfileId(profile.getProfileId());
                    profileValueDomainService.insert(profileValue, profile);
                } else {
                    profileValueDomainService.update(profileValue, profile);
                }
            }
        }
        return profile;
    }

    /**
     * 加上了唯一性约束判断的新增
     *
     * @param profile 配置维护entity
     * @return 新增后带有主键的entity
     */
    public Profile create(Profile profile) {
        Profile tempProfile = new Profile();
        tempProfile.setProfileName(profile.getProfileName());
        tempProfile.setTenantId(profile.getTenantId());
        int count = profileRepository.selectCount(tempProfile);
        if (count != 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
        profileRepository.insertSelective(profile);
        return profile;
    }

    /**
     * 删除头信息以及redis信息
     *
     * @param profile 配置维护
     */
    public void delete(Profile profile) {
        // 删除配置维护头
        profileRepository.deleteByPrimaryKey(profile);
    }
}
