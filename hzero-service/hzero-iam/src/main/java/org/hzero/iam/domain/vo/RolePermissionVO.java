package org.hzero.iam.domain.vo;

import javax.persistence.Column;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class RolePermissionVO {

    private Long tenantId;
    @Column(name = "h_level_path")
    private String levelPath;
    @Column(name = "fd_level")
    private String level;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public RolePermissionVO setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public RolePermissionVO setLevel(String level) {
        this.level = level;
        return this;
    }
}
