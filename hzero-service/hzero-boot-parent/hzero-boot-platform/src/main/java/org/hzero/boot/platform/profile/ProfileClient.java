package org.hzero.boot.platform.profile;

import org.hzero.boot.platform.profile.constant.ProfileConstants;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 编码规则客户端
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/08 10:07
 */
@Component
public class ProfileClient {
    /**
     * FIX 20200629 配置维护去除回溯数据库操作，采用日志提醒
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileClient.class);
    @Autowired
    private RedisHelper redisHelper;

    private ProfileClient() {

    }

    /**
     * 根据配置维护值名得到当前用户最低层次的配置维护值 默认 租户级 > 角色级 > 用户级 FIX 2019-04-19 去除获取平台级配置维护值信息
     *
     * @param tenantId 租户id
     * @param profileName 配置维护值名
     * @param userId 用户id
     * @param roleId 角色id
     * @return 配置维护值
     */
    public String getProfileValueByOptions(Long tenantId, Long userId, Long roleId, String profileName) {
        if (profileName == null) {
            // 配置维护名不输入则返回空
            return null;
        }
        // 用户级的key
        String userKey = null;
        if (userId != null) {
            userKey = this.generateCacheKey(tenantId, profileName, ProfileConstants.USER, userId.toString());
        }
        // 角色级的key
        String roleKey = null;
        if (roleId != null) {
            roleKey = this.generateCacheKey(tenantId, profileName, ProfileConstants.ROLE, roleId.toString());
        }
        // 租户级的key
        String tenantKey = null;
        if (tenantId != null) {
            tenantKey = this.generateCacheKey(tenantId, profileName, ProfileConstants.GLOBAL, ProfileConstants.GLOBAL);
        }
        // 0租户的key,若查不到当前租户下的配置维护值就查询0租户下的全局配置维护值
        String siteKey = this.generateCacheKey(BaseConstants.DEFAULT_TENANT_ID, profileName, ProfileConstants.GLOBAL,
                        ProfileConstants.GLOBAL);
        String profileValueByKeys = getProfileValueByKeys(userKey, roleKey, tenantKey, siteKey);
        if (profileValueByKeys != null) {
            return profileValueByKeys;
        } else {
            LOGGER.warn("this profile name can not get profile value, please refresh profile cache and try again!, profile name is : {}", profileName);
            return null;
        }
    }

    /**
     * 根据配置维护值名得到当前用户最低层次的配置维护值 默认 租户级 > 角色级 > 用户级 FIX 2019-04-19 去除获取平台级配置维护值信息
     *
     * @param profileName 配置维护值名
     * @return 配置维护值
     */
    public String getProfileValueByOptions(String profileName) {
        if (profileName == null) {
            // 配置维护名不输入则返回空
            return null;
        }
        // 0租户的key,若查不到当前租户下的配置维护值就查询0租户下的全局配置维护值
        String siteKey = this.generateCacheKey(BaseConstants.DEFAULT_TENANT_ID, profileName, ProfileConstants.GLOBAL,
                ProfileConstants.GLOBAL);
        String profileValueByKeys = getProfileValueByKeys(null, null, null, siteKey);
        if (profileValueByKeys != null) {
            return profileValueByKeys;
        } else {
            LOGGER.warn("this profile name can not get profile value, please refresh profile cache and try again!, profile name is : {}", profileName);
            return null;
        }
    }

    /**
     * 生成redis缓存key值 FIX 2019-04-19 去除缓存key中的维度值
     *
     * @param tenantId 租户id
     * @param profileName 配置维护名
     * @param levelCode 应用层级
     * @param levelValue 应用层级值
     * @return key
     */
    private String generateCacheKey(Long tenantId, String profileName, String levelCode, String levelValue) {
        StringBuilder sb = new StringBuilder(ProfileConstants.PROFILE_KEY + BaseConstants.Symbol.COLON);
        sb = sb.append(tenantId).append(BaseConstants.Symbol.POINT).append(profileName)
                        .append(BaseConstants.Symbol.POINT).append(levelCode).append(BaseConstants.Symbol.POINT)
                        .append(levelValue);
        return sb.toString();
    }

    /**
     * 根据keys从redis中获取值，如果不存在则返回null
     *
     * @param keys keys
     * @return value值
     */
    private String getProfileValueByKeys(String... keys) {
        try{
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
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
        }finally {
            redisHelper.clearCurrentDatabase();
        }
        return null;
    }
}
