package org.hzero.boot.imported.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;


/**
 * 要导入的数据:
 *
 * @author chunqiang.bai@hand-china.com
 */
@Table(name = "himp_data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportData {

    public static final String FIELD_ID = "id";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_DATA_STATUS = "dataStatus";
    public static final String FIELD_ERROR_MSG = "errorMsg";
    public static final String FIELD_BACK_INFO = "backInfo";

    @Id
    @GeneratedValue
    @OrderBy
    @JsonProperty("_id")
    @Encrypt
    private Long id;
    @NotBlank
    @ApiModelProperty("批次")
    @JsonProperty("_batch")
    private String batch;
    @NotBlank
    @ApiModelProperty("模板编码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    @JsonProperty("_templateCode")
    private String templateCode;
    @NotNull
    @ApiModelProperty("页下标")
    @JsonProperty("_sheetIndex")
    private Integer sheetIndex;
    @Column
    @NotNull
    @ApiModelProperty("数据状态[NEW(Excel导入),VALID_SUCCESS(验证成功),VALID_FAILED(验证失败),IMPORT_SUCCESS(导入成功),IMPORT_FAILED(导入失败)]")
    @JsonProperty("_dataStatus")
    @LovValue("HIMP.DATA_STATUS")
    private DataStatus dataStatus;
    @ApiModelProperty("错误信息")
    @JsonProperty("_errorMsg")
    @JsonIgnore
    private String errorMsg;
    @ApiModelProperty("数据")
    @JsonProperty("_data")
    private String data;
    @ApiModelProperty("回写信息")
    @JsonProperty("_backInfo")
    @JsonIgnore
    private String backInfo;

    @Transient
    @JsonProperty("_dataStatusMeaning")
    private String dataStatusMeaning;

    @Transient
    @JsonProperty("_info")
    private String info;

    public Long getId() {
        return id;
    }

    public ImportData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getBatch() {
        return batch;
    }

    public ImportData setBatch(String batch) {
        this.batch = batch;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public ImportData setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public ImportData setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    public DataStatus getDataStatus() {
        return dataStatus;
    }

    public ImportData setDataStatus(DataStatus dataStatus) {
        this.dataStatus = dataStatus;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public ImportData setErrorMsg(String errorMsg) {
        if (StringUtils.isBlank(errorMsg)) {
            this.errorMsg = errorMsg;
        }
        this.errorMsg = StringUtils.abbreviate(errorMsg, 250);
        return this;
    }

    /**
     * 追加错误信息
     */
    public ImportData addErrorMsg(String errorMsg) {
        this.errorMsg = StringUtils.isBlank(this.errorMsg) ? errorMsg : this.errorMsg + ";" + errorMsg;
        this.errorMsg = StringUtils.abbreviate(this.errorMsg, 250);
        return this;
    }

    public String getData() {
        return data;
    }

    public ImportData setData(String data) {
        this.data = data;
        return this;
    }

    public String getBackInfo() {
        return backInfo;
    }

    public ImportData setBackInfo(String backInfo) {
        this.backInfo = backInfo;
        return this;
    }

    /**
     * 追加错误信息
     */
    public void addBackInfo(String backInfo) {
        this.backInfo = StringUtils.isBlank(this.backInfo) ? backInfo : this.backInfo + ";" + backInfo;
    }

    public String getDataStatusMeaning() {
        return dataStatusMeaning;
    }

    public ImportData setDataStatusMeaning(String dataStatusMeaning) {
        this.dataStatusMeaning = dataStatusMeaning;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public ImportData setInfo(String info) {
        this.info = info;
        return this;
    }

    @Override
    public String toString() {
        return "ImportData{" +
                "id=" + id +
                ", batch='" + batch + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", sheetIndex=" + sheetIndex +
                ", dataStatus=" + dataStatus +
                ", errorMsg='" + errorMsg + '\'' +
                ", data='" + data + '\'' +
                ", backInfo='" + backInfo + '\'' +
                ", dataStatusMeaning='" + dataStatusMeaning + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}