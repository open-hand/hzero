package org.hzero.iam.infra.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import io.choerodon.core.exception.CommonException;

/**
 * HIAM 层级值
 */
public enum HiamResourceLevel {

    /**
     * 平台层
     */
    SITE(0, "site", "role/site/custom/", "S", Collections.unmodifiableSet(Sets.newHashSet("site"))),

    /**
     * 租户层 此处的租户层相当于猪齿鱼的组织层，为了保持统一使用 organization
     */
    ORGANIZATION(1, "organization", "role/organization/custom/", "T", Collections.unmodifiableSet(Sets.newHashSet("organization"))),

    /**
     * 组织层
     *
     * @deprecated 只保留平台级和租户级
     */
    @Deprecated
    ORG(2, "org", "role/org/custom/", "O", Collections.unmodifiableSet(Sets.newHashSet("org"))),

    /**
     * 项目层
     *
     * @deprecated 只保留平台级和租户级
     */
    @Deprecated
    PROJECT(3, "project", "role/project/custom/", "P", Collections.unmodifiableSet(Sets.newHashSet("project"))),

    /**
     * 用户层
     *
     * @deprecated 只保留平台级和租户级
     */
    @Deprecated
    USER(4, "user", "role/user/custom/", "U", Collections.unmodifiableSet(Sets.newHashSet("user")));

    private final int level;
    private final String value;
    private final String code;
    private final String simpleCode;
    private final Set<String> apiLevel;

    private static Map<String, HiamResourceLevel> resourceLevelMap = new HashMap<>(8);
    static {
        for (HiamResourceLevel resourceLevel : HiamResourceLevel.values()) {
            resourceLevelMap.put(resourceLevel.value(), resourceLevel);
        }
    }

    /**
     *
     * @param level 角色层级值
     * @param value 角色层级
     * @param code 自定义角色编码前缀
     */
    HiamResourceLevel(int level, String value, String code, String simpleCode, Set<String> apiLevel) {
        this.level = level;
        this.value = value;
        this.code = code;
        this.simpleCode = simpleCode;
        this.apiLevel = apiLevel;
    }

    public int level() {
        return level;
    }

    public String value() {
        return value;
    }

    public String code() {
        return code;
    }

    public String simpleCode() {
        return simpleCode;
    }

    public Set<String> getApiLevel() {
        return apiLevel;
    }

    public static HiamResourceLevel levelOfNullable(String level) {
        return resourceLevelMap.get(level);
    }

    public static HiamResourceLevel levelOf(String level) {
        HiamResourceLevel resourceLevel = resourceLevelMap.get(level);
        if (resourceLevel == null) {
            throw new CommonException("hiam.warn.resource.resourceLevelIllegal");
        }
        return resourceLevel;
    }

}
