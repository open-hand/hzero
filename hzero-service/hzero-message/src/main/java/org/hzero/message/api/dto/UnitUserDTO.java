package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 部门查询用户DTO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/06/12 10:01
 */
@ApiModel("部门用户DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitUserDTO {

    @ApiModelProperty("部门Id")
    private Long unitId;
    @ApiModelProperty("租户Id")
    private Long tenantId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "UnitUserDTO{" + "unitId=" + unitId + ", tenantId=" + tenantId + '}';
    }
}
