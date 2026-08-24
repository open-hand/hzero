package org.hzero.admin.api.dto.swagger;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author superlee
 */
public class PathDTO implements Comparable<PathDTO> {

    @ApiModelProperty(value = "请求url")
    private String url;
    @ApiModelProperty(value = "请求方法")
    private String method;
    @ApiModelProperty(value = "请求接受类型")
    private List<String> consumes;
    @ApiModelProperty(value = "请求响应类型")
    private List<String> produces;
    @ApiModelProperty(hidden = true)
    private String operationId;
    @ApiModelProperty(value = "参数集合")
    private List<ParameterDTO> parameters;
    @ApiModelProperty(value = "响应集合")
    private List<ResponseDTO> responses;
    @ApiModelProperty(value = "接口描述")
    private String remark;
    @ApiModelProperty(value = "接口自定义扩展详细信息")
    private String description;
    @ApiModelProperty(value = "接口相关联的controller")
    private String refController;
    @ApiModelProperty(value = "内部调用的接口")
    private Boolean innerInterface;
    @ApiModelProperty(value = "请求的basePath")
    private String basePath;
    @ApiModelProperty(value = "权限code")
    private String code;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<ParameterDTO> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDTO> parameters) {
        this.parameters = parameters;
    }

    public List<ResponseDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<ResponseDTO> responses) {
        this.responses = responses;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRefController() {
        return refController;
    }

    public void setRefController(String refController) {
        this.refController = refController;
    }

    public Boolean getInnerInterface() {
        return innerInterface;
    }

    public void setInnerInterface(Boolean innerInterface) {
        this.innerInterface = innerInterface;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PathDTO pathDTO = (PathDTO) o;

        if (url != null ? !url.equals(pathDTO.url) : pathDTO.url != null) {
            return false;
        }
        if (method != null ? !method.equals(pathDTO.method) : pathDTO.method != null) {
            return false;
        }
        if (operationId != null ? !operationId.equals(pathDTO.operationId) : pathDTO.operationId != null) {
            return false;
        }
        if (remark != null ? !remark.equals(pathDTO.remark) : pathDTO.remark != null) {
            return false;
        }
        return (description != null ? description.equals(pathDTO.description) : pathDTO.description == null) && (refController != null ? refController.equals(pathDTO.refController) : pathDTO.refController == null);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (operationId != null ? operationId.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (refController != null ? refController.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PathDTO{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", consumes=" + consumes +
                ", produces=" + produces +
                ", operationId='" + operationId + '\'' +
                ", parameters=" + parameters +
                ", responses=" + responses +
                ", remark='" + remark + '\'' +
                ", description='" + description + '\'' +
                ", refController='" + refController + '\'' +
                '}';
    }

    @Override
    public int compareTo(PathDTO o) {
        return o.url.compareTo(this.url);
    }
}