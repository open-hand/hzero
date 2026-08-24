package org.hzero.boot.platform.entity.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * entity注册property类
 *
 * @author xingxing.wu@hand-china.com 2019/07/18 10:37
 */
@ConfigurationProperties(prefix = EntityRegistProperties.ENTITY_REGIST_PROPERTIES_PREFIX)
public class EntityRegistProperties {

    public static final String ENTITY_REGIST_PROPERTIES_PREFIX = "hzero.platform.regist-entity";
    private boolean enable = false;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
