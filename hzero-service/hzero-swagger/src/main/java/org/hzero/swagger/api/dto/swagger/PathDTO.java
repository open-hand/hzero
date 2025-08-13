package org.hzero.swagger.api.dto.swagger;

import java.util.List;

/**
 * @author superlee
 */
public class PathDTO implements Comparable<PathDTO> {

    private String url;
    private String method;
    private List<String> consumes;
    private List<String> produces;
    private String operationId;
    private List<ParameterDTO> parameters;
    private List<ResponseDTO> responses;
    private String remark;
    private String description;
    private String refController;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathDTO pathDTO = (PathDTO) o;

        if (url != null ? !url.equals(pathDTO.url) : pathDTO.url != null) return false;
        if (method != null ? !method.equals(pathDTO.method) : pathDTO.method != null) return false;
        if (consumes != null ? !consumes.equals(pathDTO.consumes) : pathDTO.consumes != null) return false;
        if (produces != null ? !produces.equals(pathDTO.produces) : pathDTO.produces != null) return false;
        if (operationId != null ? !operationId.equals(pathDTO.operationId) : pathDTO.operationId != null) return false;
        if (parameters != null ? !parameters.equals(pathDTO.parameters) : pathDTO.parameters != null) return false;
        if (responses != null ? !responses.equals(pathDTO.responses) : pathDTO.responses != null) return false;
        if (remark != null ? !remark.equals(pathDTO.remark) : pathDTO.remark != null) return false;
        if (description != null ? !description.equals(pathDTO.description) : pathDTO.description != null) return false;
        return refController != null ? refController.equals(pathDTO.refController) : pathDTO.refController == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (consumes != null ? consumes.hashCode() : 0);
        result = 31 * result + (produces != null ? produces.hashCode() : 0);
        result = 31 * result + (operationId != null ? operationId.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (responses != null ? responses.hashCode() : 0);
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
