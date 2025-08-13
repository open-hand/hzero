package org.hzero.platform.api.dto;

import java.util.Map;

/**
 * 脚本测试DTO
 *
 * @author shuangfei.zhu@hand-china.com 2018/10/11 17:25
 */
public class RuleScriptTestDTO {

    private Long tenantId;
    private String scriptCode;
    private Map<String,Object> params;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
