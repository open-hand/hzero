package org.hzero.iam.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * description
 *
 * @author gaokuo.dai@hand-china.com 2018年8月13日上午12:24:22
 */
@ApiModel("用户权限DTO")
@ExcelSheet(zh = "用户权限", en = "User Authority")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserAuthorityExportDTO {
    
    @ExcelColumn(zh = "权限类型", en = "Authority Type")
    @ApiModelProperty("权限类型含义")
    private String authorityTypeCodeMeaning;
    @ApiModelProperty("代码")
    @ExcelColumn(zh = "代码", en = "Code")
    private String dataCode;
    @ExcelColumn(zh = "名称", en = "Name")
    @ApiModelProperty("名称")
    private String dataName;
    @ApiModelProperty("权限类型代码")
    @LovValue(lovCode = "HIAM.AUTHORITY_TYPE_CODE")
    private String authorityTypeCode;

    /**
     * @return 权限类型含义
     */
    public String getAuthorityTypeCodeMeaning() {
        return authorityTypeCodeMeaning;
    }
    /**
     * @return 代码
     */
    public String getDataCode() {
        return dataCode;
    }
    /**
     * @return 名称
     */
    public String getDataName() {
        return dataName;
    }
    /**
     * @return 权限类型代码
     */
    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }
    public void setAuthorityTypeCodeMeaning(String authorityTypeCodeMeaning) {
        this.authorityTypeCodeMeaning = authorityTypeCodeMeaning;
    }
    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }
    public void setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserAuthorityExportDTO [authorityTypeCodeMeaning=");
        builder.append(authorityTypeCodeMeaning);
        builder.append(", dataCode=");
        builder.append(dataCode);
        builder.append(", dataName=");
        builder.append(dataName);
        builder.append(", authorityTypeCode=");
        builder.append(authorityTypeCode);
        builder.append(", userId=");
        builder.append(", authorityTypeQueryParams=");
        builder.append("]");
        return builder.toString();
    }
    
}
