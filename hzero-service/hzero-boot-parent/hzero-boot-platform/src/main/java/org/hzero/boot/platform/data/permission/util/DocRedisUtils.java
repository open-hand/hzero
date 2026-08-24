package org.hzero.boot.platform.data.permission.util;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 单据权限缓存校验工具类
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/19 10:34
 */
public class DocRedisUtils {

    private static RedisHelper redisHelper;

    public static RedisHelper getRedisHelper() {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        redisHelper.setCurrentDatabase(HZeroService.Iam.REDIS_DB);
        return redisHelper;
    }

    public static void clear() {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 设置角色权限头缓存
     */
    public static void setDocRoleAuthHeaderRedis(Long docTypeId, String authScopeCode, Long roleId) {
        String redisKey = StringUtils.join("hiam:doc-type:role-authority:", docTypeId, ":", authScopeCode, ":", roleId);
        setDocAuthRedisValue(redisKey);
    }
    /**
     * 删除角色权限头缓存
     */
    public static void delDocRoleAuthHeaderRedis(Long docTypeId, String authScopeCode, Long roleId) {
        String redisKey = StringUtils.join("hiam:doc-type:role-authority:", docTypeId, ":", authScopeCode, ":", roleId);
        delDocAuthRedisValue(redisKey);
    }
    /**
     * 设置角色权限行缓存
     */
    public static void setDocRoleAuthLineRedis(Long docTypeId, String authScopeCode, String authTypeCode, Long roleId) {
        String redisKey = StringUtils.join("hiam:doc-type:role-authority-line:", docTypeId, ":", authScopeCode, ":", authTypeCode, ":", roleId);
        setDocAuthRedisValue(redisKey);
    }
    /**
     * 删除角色权限行缓存
     */
    public static void delDocRoleAuthLineRedis(Long docTypeId, String authScopeCode, String authTypeCode, Long roleId) {
        String redisKey = StringUtils.join("hiam:doc-type:role-authority-line:", docTypeId, ":", authScopeCode, ":", authTypeCode, ":", roleId);
        delDocAuthRedisValue(redisKey);
    }
    /**
    /**
     * 设置用户权限缓存
     */
    public static void setDocUserAuthRedis(Long tenantId, String authTypeCode, Long userId) {
        String redisKey = StringUtils.join("hiam:doc-type:user-authority:", tenantId, ":", authTypeCode, ":", userId);
        setDocAuthRedisValue(redisKey);
    }
    /**
     * 删除用户权限缓存
     */
    public static void delDocUserAuthRedis(Long tenantId, String authTypeCode, Long userId) {
        String redisKey = StringUtils.join("hiam:doc-type:user-authority:", tenantId, ":", authTypeCode, ":", userId);
        delDocAuthRedisValue(redisKey);
    }

    /**\
     *  是否分配了角色权限头
     *
     * @param docTypeId     关联单据Id
     * @param authScopeCode 单据权限维度编码
     * @param roleMergeIds  角色合并Ids
     * @return true为已分配，false为未分配
     */
    public static Boolean checkRoleAuthHeaderAssign(Long docTypeId, String authScopeCode, List<Long> roleMergeIds) {
        String redisKey = StringUtils.join("hiam:doc-type:role-authority:", docTypeId, ":", authScopeCode);
        return getDocRoleAuthRedis(redisKey, roleMergeIds);
    }

    /**
     * 是否分配了角色权限行
     *
     * @param docTypeId     关联单据Id
     * @param authScopeCode 单据权限维度编码
     * @param authTypeCode  单据权限类型编码
     * @param roleMergeIds  角色合并Ids
     * @return true为已分配，false为未分配
     */
    public static Boolean checkRoleAuthLineAssign(Long docTypeId, String authScopeCode, String authTypeCode, List<Long> roleMergeIds) {
        String redisKey = StringUtils.join("hiam:doc-type:role-authority-line:", docTypeId, ":", authScopeCode,":", authTypeCode);
        return getDocRoleAuthRedis(redisKey, roleMergeIds);
    }

    /**
     * 是否分配了用户权限
     *
     * @param tenantId     租户Id
     * @param authTypeCode 单据权限类型编码
     * @param userId       用户ID
     * @return true为已分配，false为未分配
     */
    public static Boolean checkUserAuthAssign(Long tenantId, String authTypeCode, Long userId) {
        String redisKey = StringUtils.join("hiam:doc-type:user-authority:", tenantId, ":", authTypeCode);
        return getDocUserAuthRedis(redisKey, userId);
    }

    /**
     * 获取单据权限角色缓存，支持角色合并
     *
     * @param redisKey     redis缓存Key，Key的最后需要拼接:roleId
     * @param roleMergeIds 角色合并ids，用于组装redisKey
     * @return 缓存是否存在，存在则返回True，否则返回false
     */
    public static Boolean getDocRoleAuthRedis(String redisKey, List<Long> roleMergeIds) {
        if (roleMergeIds == null || CollectionUtils.isEmpty(roleMergeIds)) {
            return false;
        }
        for (Long roleMergeId : roleMergeIds) {
            String finalRedisKey = StringUtils.join(redisKey, BaseConstants.Symbol.COLON, roleMergeId);
            String result = getDocAuthRedisValue(finalRedisKey);
            if (result != null && Objects.equals(result, BaseConstants.Flag.YES.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取单据权限用户缓存
     *
     * @param redisKey     redis缓存Key，Key的最后需要拼接:userId
     * @param userId       用户Id
     * @return 缓存是否存在，存在则返回True，否则返回false
     */
    public static Boolean getDocUserAuthRedis(String redisKey, Long userId) {
        if (userId == null) {
            return false;
        }
        String finalRedisKey = StringUtils.join(redisKey, BaseConstants.Symbol.COLON, userId);
        String result = getDocAuthRedisValue(finalRedisKey);
        return result != null && Objects.equals(result, BaseConstants.Flag.YES.toString());
    }

    /**
     * 获取缓存
     *
     * @return 缓存值
     */
    private static String getDocAuthRedisValue(String redisKey) {
        String result = getRedisHelper().strGet(redisKey);
        clear();
        return result;
    }
    /**
     * 设置缓存
     */
    private static void setDocAuthRedisValue(String redisKey) {
        getRedisHelper().strSet(redisKey, BaseConstants.Flag.YES.toString());
        clear();
    }

    /**
     * 清除缓存
     */
    private static void delDocAuthRedisValue(String redisKey) {
        getRedisHelper().delKey(redisKey);
        clear();
    }

}
