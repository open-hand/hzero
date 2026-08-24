package org.hzero.swagger.api.dto.swagger;

import java.util.List;

/**
 * @author superlee
 */
public class ResponseDTO {
    private String httpStatus;
    private String description;
    private List<String> schema;

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

    public List<String> getSchema() {
        return schema;
    }

    public void setSchema(List<String> schema) {
        this.schema = schema;
    }
}
