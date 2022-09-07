package org.hzero.file.api.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

/**
 * 保存回调
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:48:05
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveCallbackParamDTO {

    @ApiModelProperty("文件唯一标识")
    @NotBlank
    private String fileId;

    @ApiModelProperty("文件地址")
    @NotBlank
    private String url;

    @ApiModelProperty("修改内容信息")
    @NotEmpty
    private List<ReviewChangeDTO> reviewChanges;

    @ApiModelProperty("额外参数")
    @NotBlank
    private String extra;

    public String getFileId() {
        return fileId;
    }

    public SaveCallbackParamDTO setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public SaveCallbackParamDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<ReviewChangeDTO> getReviewChanges() {
        return reviewChanges;
    }

    public SaveCallbackParamDTO setReviewChanges(List<ReviewChangeDTO> reviewChanges) {
        this.reviewChanges = reviewChanges;
        return this;
    }

    public String getExtra() {
        return extra;
    }

    public SaveCallbackParamDTO setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    @Override
    public String toString() {
        return "SaveCallbackParam{" +
                "fileId='" + fileId + '\'' +
                ", url='" + url + '\'' +
                ", reviewChanges=" + reviewChanges +
                ", extra='" + extra + '\'' +
                '}';
    }
}
