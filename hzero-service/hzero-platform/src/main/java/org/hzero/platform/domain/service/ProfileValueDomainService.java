package org.hzero.platform.domain.service;

import java.util.List;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.platform.domain.repository.ProfileValueRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 配置维护值集领域层service
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/07 20:16
 */
@Component
public class ProfileValueDomainService {

    @Autowired
    private ProfileValueRepository profileValueRepository;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 插入到数据库并插入到redis中
     *
     * @param profileValue 配置维护值
     * @param profile 配置维护
     * @return 获得主键id的配置维护值
     */
    public ProfileValue insert(ProfileValue profileValue, Profile profile) {
        ProfileValue tempProfileValue = new ProfileValue();
        tempProfileValue.setProfileId(profileValue.getProfileId());
        tempProfileValue.setLevelCode(profileValue.getLevelCode());
        tempProfileValue.setLevelValue(profileValue.getLevelValue());
        int count = profileValueRepository.selectCount(tempProfileValue);
        if (count != 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
        profileValue.setTenantId(profile.getTenantId());
        profileValueRepository.insertSelective(profileValue);
        // 根据entity获取key值
        String key = Profile.generateCacheKey(profile.getTenantId(), profile.getProfileName(),
                        profileValue.getLevelCode(), profileValue.getLevelValue());
        ProfileValue.initCache(redisHelper, key, profileValue.getValue());
        return profileValue;
    }

    /**
     * 更新数据库并更新redis
     *
     * @param profileValue 配置维护
     * @param profile 配置维护值
     * @return profileValue
     */
    public ProfileValue update(ProfileValue profileValue, Profile profile) {
        // 查出准备更新配置维护值的原先数据，用于删除redis的key
        List<ProfileValue> profileValueList =
                        profileValueRepository.select(ProfileValue.PROFILE_VALUE_ID, profileValue.getProfileValueId());
        ProfileValue oldProfileValue = null;
        if (CollectionUtils.isNotEmpty(profileValueList)) {
            oldProfileValue = profileValueList.remove(0);
        }
        profileValue.setTenantId(profile.getTenantId());
        profileValueRepository.updateByPrimaryKeySelective(profileValue);
        // 根据entity获取key值
        String key = Profile.generateCacheKey(profile.getTenantId(), profile.getProfileName(),
                        profileValue.getLevelCode(), profileValue.getLevelValue());
        String oldKey = null;
        // 删除更新之前的redis中的key值
        if (oldProfileValue != null) {
            oldKey = Profile.generateCacheKey(profile.getTenantId(), profile.getProfileName(),
                            oldProfileValue.getLevelCode(), oldProfileValue.getLevelValue());
        }
        ProfileValue.refreshCache(redisHelper, key, profileValue.getValue(), oldKey);
        return profileValue;
    }

    /**
     * 从数据库删除后，也从redis中删除
     *
     * @param profileValueId 配置维护值id
     * @param level 配置维护头应用层级
     * @param tenantId 租户id
     * @param profileName 配置维护名
     * @param levelCode 配置维护行应用层级
     * @param levelValue 配置维护行应用层级值
     */
    public void delete(Long profileValueId, String level, Long tenantId, String profileName, String levelCode,
                    String levelValue) {
        // 从数据库中删除
        ProfileValue profileValue = new ProfileValue();
        profileValue.setProfileValueId(profileValueId);
        profileValueRepository.deleteByPrimaryKey(profileValue);
        // 从redis中删除
        String key = Profile.generateCacheKey(tenantId, profileName, levelCode, levelValue);
        ProfileValue.deleteCache(redisHelper, key);
    }

    /**
     * 根据配置维护删除数据库以及redis
     *
     * @param profile 配置维护
     */
    public void deleteProfileValueByProfileId(Profile profile) {
        // 删除配置维护行信息
        ProfileValue profileValue = new ProfileValue();
        profileValue.setProfileId(profile.getProfileId());
        List<ProfileValue> profileValueList = profileValueRepository.select(profileValue);
        profileValueRepository.delete(profileValue);
        if (profileValueList != null && !profileValueList.isEmpty()) {
            profileValueList.forEach(value -> {
                String key = Profile.generateCacheKey(profile, value);
                redisHelper.delKey(key);
            });
        }
    }

    /**
     * 根据配置维护值名得到当前用户最低层次的配置维护值 默认 租户级 > 角色级 > 用户级
     * FIX 2019-04-19 去除获取平台级配置维护值信息
     *
     * @param tenantId 租户id
     * @param profileName 配置维护值名
     * @param userId 用户id
     * @param roleId 角色id
     * @return 配置维护值
     */
    public String getProfileValueByProfileName(Long tenantId, String profileName, Long userId,
                    Long roleId) {
        // 用户级的key
        String userKey = null;
        if (userId != null) {
            userKey = Profile.generateCacheKey(tenantId, profileName, FndConstants.ProfileLevelCode.USER,
                    userId.toString());
        }
        // 角色级的key
        String roleKey = null;
        if (roleId != null) {
            roleKey = Profile.generateCacheKey(tenantId, profileName, FndConstants.ProfileLevelCode.ROLE,
                    roleId.toString());
        }
        // 租户级的key
        String tenantKey = null;
        if (tenantId != null) {
            tenantKey = Profile.generateCacheKey(tenantId, profileName, FndConstants.ProfileLevelCode.GLOBAL,
                    FndConstants.ProfileLevelCode.GLOBAL);
        }
        // 0租户的key,若查不到当前租户下的配置维护值就查询0租户下的全局配置维护值
        String siteKey = Profile.generateCacheKey(BaseConstants.DEFAULT_TENANT_ID, profileName, FndConstants.ProfileLevelCode.GLOBAL,
                FndConstants.ProfileLevelCode.GLOBAL);
        return getProfileValueByKeys(userKey, roleKey, tenantKey, siteKey);
    }

    /**
     * 根据keys从redis中获取值，如果不存在则返回null
     *
     * @param keys keys
     * @return value值
     */
    private String getProfileValueByKeys(String... keys) {
        if (keys != null) {
            for (String key : keys) {
                if (key != null) {
                    String value = redisHelper.strGet(key);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return null;
    }
}
