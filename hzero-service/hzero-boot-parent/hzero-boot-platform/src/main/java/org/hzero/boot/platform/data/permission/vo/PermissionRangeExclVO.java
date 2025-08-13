package org.hzero.boot.platform.data.permission.vo;

/**
 * 屏蔽范围黑名单
 *
 * @author qingsheng.chen@hand-china.com 2020-06-10 10:17:25
 */
public class PermissionRangeExclVO {
    private Long rangeExcludeId;
    private Long rangeId;
    private String serviceName;
    private Long tenantId;
    private String sqlId;

    public Long getRangeExcludeId() {
        return rangeExcludeId;
    }

    public PermissionRangeExclVO setRangeExcludeId(Long rangeExcludeId) {
        this.rangeExcludeId = rangeExcludeId;
        return this;
    }

    public Long getRangeId() {
        return rangeId;
    }

    public PermissionRangeExclVO setRangeId(Long rangeId) {
        this.rangeId = rangeId;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public PermissionRangeExclVO setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public PermissionRangeExclVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getSqlId() {
        return sqlId;
    }

    public PermissionRangeExclVO setSqlId(String sqlId) {
        this.sqlId = sqlId;
        return this;
    }
}
