package org.hzero.admin.api.dto.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * @author superlee
 */
public class SchemaDTO {
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty(hidden = true)
    private String format;
    @ApiModelProperty(hidden = true)
    private Map<String, String> items;
    @ApiModelProperty(hidden = true)
    private Map<String, String> additionalProperties;
    @ApiModelProperty(hidden = true)
    @JsonProperty("$ref")
    private String ref;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}