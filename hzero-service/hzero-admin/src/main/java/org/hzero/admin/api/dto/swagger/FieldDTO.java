package org.hzero.admin.api.dto.swagger;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 */
public class FieldDTO {

    @ApiModelProperty("字段注释")
    private String comment;
    private String type;
    @ApiModelProperty(hidden = true)
    private String ref;
    @ApiModelProperty(hidden = true)
    private String itemType;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
