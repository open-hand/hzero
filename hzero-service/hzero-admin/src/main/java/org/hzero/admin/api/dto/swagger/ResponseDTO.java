package org.hzero.admin.api.dto.swagger;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 */
public class ResponseDTO {

    @ApiModelProperty(value = "http状态码")
    private String httpStatus;
    @ApiModelProperty(value = "状态码描述")
    private String description;

    @ApiModelProperty(value = "接口返回的结果")
    private String body;

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}