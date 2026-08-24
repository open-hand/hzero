package org.hzero.iam.api.dto;


import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色导出DTO
 *
 * @author gaokuo.dai@hand-china.com 2018年8月12日下午11:36:59
 */
@ApiModel("角色导出DTO")
@ExcelSheet(zh = "角色", en = "Role")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RoleExportDTO {
    
    @ApiModelProperty("角色代码")
    @ExcelColumn(zh = "角色代码", en = "Role Code")
    private String code;
    @ExcelColumn(zh = "角色名称", en = "Role Name")
    @ApiModelProperty("角色名称")
    private String name;
    
    /**
     * @return 角色名称
     */
    public String getName() {
        return name;
    }
    /**
     * @return 角色代码
     */
    public String getCode() {
        return code;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoleExportDTO [code=");
        builder.append(code);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }
    
    
}
