package org.hzero.admin.api.dto.swagger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterDTO {

    @ApiModelProperty("参数描述")
    private String description;
    @ApiModelProperty("参数位置")
    private String in;
    @ApiModelProperty("参数名称")
    private String name;
    @ApiModelProperty("是否必填")
    private Boolean required;
    @ApiModelProperty("参数类型")
    private String type;
    @ApiModelProperty("格式")
    private String format;
    @ApiModelProperty(hidden = true)
    private String collectionFormat;
    @ApiModelProperty(hidden = true)
    @JsonProperty("schema")
    private SchemaDTO schema;
    @ApiModelProperty(hidden = true)
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCollectionFormat() {
        return collectionFormat;
    }

    public void setCollectionFormat(String collectionFormat) {
        this.collectionFormat = collectionFormat;
    }

    public SchemaDTO getSchema() {
        return schema;
    }

    public void setSchema(SchemaDTO schema) {
        this.schema = schema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParameterDTO that = (ParameterDTO) o;

        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (in != null ? !in.equals(that.in) : that.in != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (required != null ? !required.equals(that.required) : that.required != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (format != null ? !format.equals(that.format) : that.format != null) {
            return false;
        }
        if (collectionFormat != null ? !collectionFormat.equals(that.collectionFormat) : that.collectionFormat != null) {
            return false;
        }
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) {
            return false;
        }
        return body != null ? body.equals(that.body) : that.body == null;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (in != null ? in.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (collectionFormat != null ? collectionFormat.hashCode() : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }
}