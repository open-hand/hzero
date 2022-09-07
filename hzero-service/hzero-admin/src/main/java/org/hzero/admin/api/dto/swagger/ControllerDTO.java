package org.hzero.admin.api.dto.swagger;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author superlee
 */
public class ControllerDTO {

    @ApiModelProperty(value = "controller的名字")
    private String name;
    @ApiModelProperty(value = "controller的描述")
    private String description;
    @ApiModelProperty(value = "接口集合")
    private List<PathDTO> paths;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PathDTO> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDTO> paths) {
        this.paths = paths;
    }
}