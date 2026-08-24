package org.hzero.platform.infra.repository.impl;

import java.util.List;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.ProfileDTO;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.repository.ProfileRepository;
import org.hzero.platform.domain.repository.ProfileValueRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 配置维护仓储层impl
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/05
 */
@Component
public class ProfileRepositoryImpl extends BaseRepositoryImpl<Profile> implements ProfileRepository {

    @Autowired
    private ProfileValueRepository profileValueRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public Profile query(Profile profile) {
        profile = this.selectOne(profile);
        profile.setProfileValueList(profileValueRepository.select(Profile.PROFILE_ID, profile.getProfileId()));
        return profile;
    }

    @Override
    public ProfileDTO selectProfile(Profile profile) {
        List<ProfileDTO> profileDTOList = profileMapper.selectProfile(profile);
        if (CollectionUtils.isNotEmpty(profileDTOList) && profileDTOList.size() == 1) {
            ProfileDTO profileDTO = profileDTOList.remove(0);
            profileDTO.setProfileValueDTOList(profileValueRepository.selectProfileValue(profile.getProfileId()));
            return profileDTO;
        }
        throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
    }

    @Override
    public List<ProfileDTO> selectProfileLike(PageRequest pageRequest, Profile profile) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        return profileMapper.selectProfileLike(profile);
    }

    @Override
    public List<ProfileDTO> selectTenantProfileLike(PageRequest pageRequest, Profile profile) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        return profileMapper.selectTenantProfileLike(profile);
    }

    @Override
    public String getProfileValueByLevel(Long tenantId, String profileName, Long userId, Long roleId) {
        // 按照层级依次获取配置维护值，先查询用户级，为空则再查询角色级，为空则再查询全局级
        String userProfileValue =
                        profileMapper.selectProfileValueByOptions(tenantId, profileName,
                                        FndConstants.ProfileLevelCode.USER, Long.toString(userId));
        if (userProfileValue != null) {
            return userProfileValue;
        }
        // 不存在用户级的配置维护值，查询角色级
        String roleProfileValue =
                        profileMapper.selectProfileValueByOptions(tenantId, profileName,
                                        FndConstants.ProfileLevelCode.ROLE, Long.toString(roleId));
        if (roleProfileValue != null) {
            return roleProfileValue;
        }
        // 不存在用户级和角色级，查询租户级
        String tenantProfileValue =
                        profileMapper.selectProfileValueByOptions(tenantId, profileName,
                                        FndConstants.ProfileLevelCode.GLOBAL, FndConstants.ProfileLevelCode.GLOBAL);
        if (tenantProfileValue != null) {
            return tenantProfileValue;
        }
        // 该租户下不存在配置信息，获取0租户下的租户级配置维护值
        String globalProfileValue =
                        profileMapper.selectProfileValueByOptions(BaseConstants.DEFAULT_TENANT_ID, profileName,
                                        FndConstants.ProfileLevelCode.GLOBAL, FndConstants.ProfileLevelCode.GLOBAL);
        if (globalProfileValue != null) {
            return globalProfileValue;
        } else {
            return null;
        }
    }
}
